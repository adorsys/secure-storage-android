package de.adorsys.android.securestorage;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import static android.os.Build.VERSION_CODES.M;

class KeystoreTool {
    private static final String KEY_ALIAS = "adorsysKeyPair";
    private static final String KEY_ENCRYPTION_ALGORITHM = "RSA";
    private static final String KEY_CHARSET = "UTF-8";
    private static final String KEY_KEYSTORE_NAME = "AndroidKeyStore";
    private static final String KEY_CIPHER_JELLYBEAN_PROVIDER = "AndroidOpenSSL";
    private static final String KEY_CIPHER_MARSHMALLOW_PROVIDER = "AndroidKeyStoreBCWorkaround";
    private static final String KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String KEY_X500PRINCIPAL = "CN=SecureDeviceStorage, O=Adorsys, C=Germany";

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Nullable
    static String encryptMessage(@NonNull Context context, @NonNull String plainMessage) throws CryptoException {
        try {
            Cipher input;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && Build.VERSION.SDK_INT < M) {
                input = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                input = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER);
            } else {
                Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_supported_api));
                throw new CryptoException(context.getString(R.string.message_supported_api), null);
            }
            input.init(Cipher.ENCRYPT_MODE, getPublicKey(context));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(plainMessage.getBytes(KEY_CHARSET));
            cipherOutputStream.close();

            byte[] values = outputStream.toByteArray();
            return Base64.encodeToString(values, Base64.DEFAULT);

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | NoSuchPaddingException
                | InvalidKeyException
                | IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Nullable
    static String decryptMessage(@NonNull Context context, @NonNull String encryptedMessage) throws CryptoException {
        try {
            Cipher output;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && Build.VERSION.SDK_INT < M) {
                output = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER);
            } else if (Build.VERSION.SDK_INT >= M) {
                output = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER);
            } else {
                Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_supported_api));
                throw new CryptoException(context.getString(R.string.message_supported_api), null);
            }

            output.init(Cipher.DECRYPT_MODE, getPrivateKey(context));

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encryptedMessage, Base64.DEFAULT)), output);
            List<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return new String(bytes, 0, bytes.length, KEY_CHARSET);

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | NoSuchPaddingException
                | InvalidKeyException
                | IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    static boolean keyPairExists() {
        try {
            return getKeyStoreInstance().getKey(KEY_ALIAS, null) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static void generateKeyPair(@NonNull Context context) throws CryptoException {
        // Create new key if needed
        if (!keyPairExists()) {
            if (Build.VERSION.SDK_INT >= M) {
                generateMarshmallowKeyPair();
            } else if (Build.VERSION.SDK_INT < M
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                generateJellyBeanKeyPair(context);
            } else {
                Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_supported_api));
                throw new CryptoException(context.getString(R.string.message_supported_api), null);
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(KeystoreTool.class.getName(),
                        context.getString(R.string.message_keypair_already_exists));
            }
        }
    }

    static void deleteKeyPair(@NonNull Context context) throws CryptoException {
        // Delete Key from Keystore
        if (keyPairExists()) {
            try {
                getKeyStoreInstance().deleteEntry(KEY_ALIAS);
            } catch (KeyStoreException e) {
                throw new CryptoException(e.getMessage(), e);
            }
        } else {
            Log.e(KeystoreTool.class.getName(),
                    context.getString(R.string.message_keypair_does_not_exist));
        }
    }

    @Nullable
    private static PublicKey getPublicKey(@NonNull Context context) throws CryptoException {
        try {
            if (keyPairExists()) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
                return privateKeyEntry.getCertificate().getPublicKey();
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_keypair_does_not_exist));
                }
                throw new CryptoException(context.getString(R.string.message_keypair_does_not_exist), null);
            }
        } catch (NoSuchAlgorithmException
                | UnrecoverableEntryException
                | KeyStoreException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Nullable
    private static PrivateKey getPrivateKey(@NonNull Context context) throws CryptoException {
        try {
            if (keyPairExists()) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
                return privateKeyEntry.getPrivateKey();
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e(KeystoreTool.class.getName(), context.getString(R.string.message_keypair_does_not_exist));
                }
                throw new CryptoException(context.getString(R.string.message_keypair_does_not_exist), null);
            }
        } catch (NoSuchAlgorithmException
                | UnrecoverableEntryException
                | KeyStoreException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @RequiresApi(M)
    private static void generateMarshmallowKeyPair() throws CryptoException {
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 99);

            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT)
                    .setKeyValidityStart(start.getTime())
                    .setKeyValidityEnd(end.getTime())
                    .setUserAuthenticationRequired(false)
                    .setCertificateSerialNumber(BigInteger.ONE)
                    .setCertificateSubject(new X500Principal(KEY_X500PRINCIPAL))
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .build();

            KeyPairGenerator generator
                    = KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);
            generator.initialize(spec);

            generator.generateKeyPair();
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void generateJellyBeanKeyPair(@NonNull Context context) throws CryptoException {
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 99);

            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal(KEY_X500PRINCIPAL))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();

            KeyPairGenerator generator
                    = KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);
            generator.initialize(spec);
            generator.generateKeyPair();
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @NonNull
    private static KeyStore getKeyStoreInstance() throws CryptoException {
        try {
            // Get the AndroidKeyStore instance
            KeyStore keyStore = KeyStore.getInstance(KEY_KEYSTORE_NAME);

            // Relict of the JCA API - you have to call load even
            // if you do not have an input stream you want to load or it'll crash
            keyStore.load(null);

            return keyStore;
        } catch (CertificateException
                | NoSuchAlgorithmException
                | KeyStoreException
                | IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }
}