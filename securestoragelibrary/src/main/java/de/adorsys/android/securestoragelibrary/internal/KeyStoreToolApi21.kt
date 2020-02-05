/*
 * Copyright (C) 2019 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("DEPRECATION")

package de.adorsys.android.securestoragelibrary.internal

import android.annotation.SuppressLint
import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import de.adorsys.android.securestoragelibrary.SecureStorage
import de.adorsys.android.securestoragelibrary.SecureStorageException
import de.adorsys.android.securestoragelibrary.execute
import de.adorsys.android.securestoragelibrary.internal.AesCbcWithIntegrity.SecretKeys
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.PrivateKey
import java.security.PublicKey
import java.util.GregorianCalendar
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

internal object KeyStoreToolApi21 {

    //================================================================================
    // SecureStorage KeyStoreTool API >= 21 && API < 23 Logic
    //================================================================================

    private const val RSA_KEY_PAIR_VALIDITY_IN_YEARS = 99
    private const val KEY_PAIR_GENERATOR_PROVIDER = "AndroidKeyStore"
    private const val RSA_ALGORITHM = "RSA"
    private const val AES_ALGORITHM = "RSA"
    private const val KEY_AES_CONFIDENTIALITY_KEY = "AesConfidentialityKey"
    private const val KEY_AES_INTEGRITY_KEY = "AesIntegrityKey"

    @Throws(SecureStorageException::class)
    internal fun keyExists(keyStoreInstance: KeyStore): Boolean =
        rsaKeyPairExists(keyStoreInstance)

    internal fun generateKey(context: Context) {
        generateRsaKey(context)
    }

    // We have to suppress the lint warning even though .apply is used in .execute() extension function
    @SuppressLint("CommitPrefEdits")
    @Throws(SecureStorageException::class)
    internal fun deleteKey(context: Context, keyStoreInstance: KeyStore) {
        // Delete Symmetric Key from SecureStorage
        SecureStorage.getSharedPreferencesInstance(context).edit().clear().execute()

        // Delete Asymmetric KeyPair from Keystore
        when {
            rsaKeyPairExists(keyStoreInstance) -> try {
                keyStoreInstance.deleteEntry(SecureStorage.ENCRYPTION_KEY_ALIAS)
            } catch (e: KeyStoreException) {
                throw SecureStorageException(
                    if (!e.message.isNullOrBlank()) e.message!!
                    else SecureStorageException.MESSAGE_ERROR_WHILE_DELETING_KEYPAIR,
                    e,
                    SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
                )
            }
            else -> throw SecureStorageException(
                SecureStorageException.MESSAGE_KEYPAIR_DOES_NOT_EXIST,
                null,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    internal fun encryptValue(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        key: String,
        value: String
    ): String {
        return when {
            keyExists(keyStoreInstance) -> {
                generateAesKey(context, keyStoreInstance, cipher, key)

                val aesKey = getAesKey(context, keyStoreInstance, cipher, key)
                AesCbcWithIntegrity.encrypt(value, aesKey).toString()
            }
            else -> throw SecureStorageException(
                SecureStorageException.MESSAGE_KEYPAIR_DOES_NOT_EXIST,
                null,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    internal fun decryptValue(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        key: String,
        value: String
    ): String {
        val aesKey = getAesKey(context, keyStoreInstance, cipher, key)
        return AesCbcWithIntegrity.decryptString(
            AesCbcWithIntegrity.CipherTextIvMac(value),
            aesKey
        )
    }

    private fun getKeyPairGenerator(): KeyPairGenerator =
        KeyPairGenerator.getInstance(RSA_ALGORITHM, KEY_PAIR_GENERATOR_PROVIDER)

    @Throws(SecureStorageException::class)
    private fun rsaKeyPairExists(keyStoreInstance: KeyStore): Boolean {
        try {
            return keyStoreInstance.getCertificate(SecureStorage.ENCRYPTION_KEY_ALIAS) != null
                    && keyStoreInstance.getKey(SecureStorage.ENCRYPTION_KEY_ALIAS, null) != null
        } catch (e: Exception) {
            throw SecureStorageException(
                if (!e.message.isNullOrBlank()) e.message!!
                else SecureStorageException.MESSAGE_ERROR_WHILE_RETRIEVING_KEYPAIR,
                e,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    private fun generateRsaKey(context: Context) {
        val keyPairGenerator = getKeyPairGenerator()
        val keyStartDate = GregorianCalendar.getInstance()
        keyStartDate.add(GregorianCalendar.DAY_OF_MONTH, -1)
        val keyEndDate = GregorianCalendar.getInstance()
        keyEndDate.add(GregorianCalendar.YEAR, RSA_KEY_PAIR_VALIDITY_IN_YEARS)

        val keyPairGeneratorSpecBuilder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(SecureStorage.ENCRYPTION_KEY_ALIAS)
            .setSubject(X500Principal(SecureStorage.X500PRINCIPAL))
            .setSerialNumber(BigInteger.TEN)
            .setStartDate(keyStartDate.time)
            .setEndDate(keyEndDate.time)

        keyPairGenerator.initialize(keyPairGeneratorSpecBuilder.build())
        keyPairGenerator.generateKeyPair()
    }

    private fun generateAesKey(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        keyPrefix: String
    ): SecretKeys {
        val secretKeys = AesCbcWithIntegrity.generateKey()

        storeAesKeyPart(
            context,
            keyStoreInstance,
            cipher,
            keyPrefix + KEY_AES_CONFIDENTIALITY_KEY,
            encodeKeyToString(secretKeys.confidentialityKey)
        )

        storeAesKeyPart(
            context,
            keyStoreInstance,
            cipher,
            keyPrefix + KEY_AES_INTEGRITY_KEY,
            encodeKeyToString(secretKeys.integrityKey)
        )

        return secretKeys
    }

    // We have to suppress the lint warning even though .apply is used in .execute() extension function
    @SuppressLint("CommitPrefEdits")
    private fun storeAesKeyPart(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        keyValueKey: String,
        aesKey: String
    ) {
        val publicKey = getPublicKey(keyStoreInstance)

        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val bytes = cipher.doFinal(aesKey.toByteArray())

        SecureStorage.getSharedPreferencesInstance(context).edit()
            .putString(keyValueKey, Base64.encodeToString(bytes, Base64.DEFAULT)).execute()
    }

    private fun getAesKeyPart(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        keyValueKey: String
    ): String {
        val privateKey = getPrivateKey(keyStoreInstance)

        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return when (val encodedKey =
            SecureStorage.getSharedPreferencesInstance(context).getString(keyValueKey, null)) {
            null -> throw SecureStorageException(
                SecureStorageException.MESSAGE_KEY_DOES_NOT_EXIST,
                null,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
            else -> {
                val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)

                val decryptedKey = cipher.doFinal(decodedKey)
                String(decryptedKey)
            }
        }
    }

    private fun encodeKeyToString(key: SecretKey): String = Base64.encodeToString(key.encoded, Base64.DEFAULT)

    private fun decodeStringToKey(encodedString: String): SecretKey {
        val encodedKey = Base64.decode(encodedString, Base64.DEFAULT)
        return SecretKeySpec(encodedKey, 0, encodedKey.size, AES_ALGORITHM)
    }

    private fun getAesKey(
        context: Context,
        keyStoreInstance: KeyStore,
        cipher: Cipher,
        keyPrefix: String
    ): SecretKeys {
        val encodedIntegrityKey = getAesKeyPart(
            context,
            keyStoreInstance,
            cipher,
            keyPrefix + KEY_AES_INTEGRITY_KEY
        )
        val encodedConfidentialityKey =
            getAesKeyPart(context, keyStoreInstance, cipher, keyPrefix + KEY_AES_CONFIDENTIALITY_KEY)

        val reconstructedIntegrityKey = decodeStringToKey(encodedIntegrityKey)
        val reconstructedConfidentialityKey = decodeStringToKey(encodedConfidentialityKey)

        return SecretKeys(reconstructedConfidentialityKey, reconstructedIntegrityKey)
    }

    @Throws(SecureStorageException::class)
    private fun getPrivateKey(keyStoreInstance: KeyStore): PrivateKey {
        keyStoreInstance.getKey(SecureStorage.ENCRYPTION_KEY_ALIAS, null)

        try {
            when {
                rsaKeyPairExists(keyStoreInstance) -> return keyStoreInstance.getKey(
                    SecureStorage.ENCRYPTION_KEY_ALIAS,
                    null
                ) as PrivateKey
                else -> throw SecureStorageException(
                    SecureStorageException.MESSAGE_KEYPAIR_DOES_NOT_EXIST,
                    null,
                    SecureStorageException.ExceptionType.INTERNAL_LIBRARY_EXCEPTION
                )
            }
        } catch (e: Exception) {
            throw SecureStorageException(
                if (!e.message.isNullOrBlank()) e.message!!
                else SecureStorageException.MESSAGE_ERROR_WHILE_RETRIEVING_PRIVATE_KEY,
                e,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    @Throws(SecureStorageException::class)
    private fun getPublicKey(keyStoreInstance: KeyStore): PublicKey {
        try {
            when {
                rsaKeyPairExists(keyStoreInstance) ->
                    return keyStoreInstance.getCertificate(SecureStorage.ENCRYPTION_KEY_ALIAS).publicKey
                else -> throw SecureStorageException(
                    SecureStorageException.MESSAGE_KEYPAIR_DOES_NOT_EXIST,
                    null,
                    SecureStorageException.ExceptionType.INTERNAL_LIBRARY_EXCEPTION
                )
            }
        } catch (e: Exception) {
            throw SecureStorageException(
                if (!e.message.isNullOrBlank()) e.message!!
                else SecureStorageException.MESSAGE_ERROR_WHILE_RETRIEVING_PUBLIC_KEY,
                e,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }
}