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

package de.adorsys.android.securestoragelibrary.internal

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import de.adorsys.android.securestoragelibrary.SecureStorage
import de.adorsys.android.securestoragelibrary.SecureStorageException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec

internal object KeyStoreToolApi23 {

    //================================================================================
    // SecureStorage KeyStoreTool API >= 23 Logic
    //================================================================================

    private const val AES_KEY_BIT_SIZE = 256
    private const val KEY_GENERATOR_PROVIDER = "AndroidKeyStore"
    private const val KEY_CIPHER_IV = "KeyCipherIV"

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(SecureStorageException::class)
    internal fun keyExists(keyStoreInstance: KeyStore): Boolean {
        try {
            return keyStoreInstance.getKey(SecureStorage.ENCRYPTION_KEY_ALIAS, null) != null
        } catch (e: Exception) {
            throw SecureStorageException(
                    if (!e.message.isNullOrBlank()) e.message!!
                    else SecureStorageException.MESSAGE_ERROR_WHILE_RETRIEVING_KEY,
                    e,
                    SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun generateKey(context: Context) {
        val keyGenerator = getKeyGenerator()

        val keyStartDate = GregorianCalendar.getInstance()
        keyStartDate.add(GregorianCalendar.DAY_OF_MONTH, -1)

        val keyGenParameterSpecBuilder =
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> KeyGenParameterSpec.Builder(
                            SecureStorage.ENCRYPTION_KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                            .setKeyValidityStart(keyStartDate.time)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setKeySize(AES_KEY_BIT_SIZE)
                            .setIsStrongBoxBacked(hasStrongBox(context))
                    else -> KeyGenParameterSpec.Builder(
                            SecureStorage.ENCRYPTION_KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                            .setKeyValidityStart(keyStartDate.time)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setKeySize(AES_KEY_BIT_SIZE)
                }

        keyGenerator.init(keyGenParameterSpecBuilder.build())
        keyGenerator.generateKey()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun hasStrongBox(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun encryptValue(
            context: Context,
            keyStoreInstance: KeyStore,
            cipher: Cipher,
            key: String,
            value: String
    ): String {

        val secretKey = when {
            keyExists(keyStoreInstance) -> getSecretKey(keyStoreInstance)
            else -> throw SecureStorageException(
                    SecureStorageException.MESSAGE_KEY_DOES_NOT_EXIST,
                    null,
                    SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val bytes = cipher.doFinal(value.toByteArray())
        saveIVInSecureStorage(context, key, cipher.iv)
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun decryptValue(
            context: Context,
            keyStoreInstance: KeyStore,
            cipher: Cipher,
            key: String,
            value: String
    ): String {

        val secretKey = getSecretKey(keyStoreInstance) ?: throw SecureStorageException(
                SecureStorageException.MESSAGE_KEY_DOES_NOT_EXIST,
                null,
                SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
        )

        cipher.init(
                Cipher.DECRYPT_MODE, secretKey,
                IvParameterSpec(getIVFromSecureStorage(context, key))
        )
        val encryptedData = Base64.decode(value, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun deleteKey(keyStoreInstance: KeyStore) {
        // Delete Key from Keystore
        when {
            keyExists(keyStoreInstance) -> try {
                keyStoreInstance.deleteEntry(SecureStorage.ENCRYPTION_KEY_ALIAS)
            } catch (e: KeyStoreException) {
                throw SecureStorageException(
                        if (!e.message.isNullOrBlank()) e.message!!
                        else SecureStorageException.MESSAGE_ERROR_WHILE_DELETING_KEY,
                        e,
                        SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
                )
            }
            else -> throw SecureStorageException(
                    SecureStorageException.MESSAGE_KEY_DOES_NOT_EXIST,
                    null,
                    SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun saveIVInSecureStorage(context: Context, key: String, iv: ByteArray) {
        val preferences =
                context.getSharedPreferences(SecureStorage.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)
        preferences.edit().putString("$KEY_CIPHER_IV$key", encodedIv).apply()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getIVFromSecureStorage(context: Context, key: String): ByteArray {
        val preferences =
                context.getSharedPreferences(SecureStorage.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val encodedIv = preferences.getString("$KEY_CIPHER_IV$key", null)
        return Base64.decode(encodedIv, Base64.DEFAULT)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getKeyGenerator(): KeyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_GENERATOR_PROVIDER)

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSecretKey(keyStoreInstance: KeyStore): SecretKey? = (keyStoreInstance
            .getEntry(SecureStorage.ENCRYPTION_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getKeyInfo(secretKey: SecretKey?): KeyInfo? {
        val factory = SecretKeyFactory.getInstance(secretKey?.algorithm, KEY_GENERATOR_PROVIDER)

        return try {
            factory.getKeySpec(secretKey, KeyInfo::class.java) as KeyInfo
        } catch (e: InvalidKeySpecException) {
            // Not an Android KeyStore key.
            null
        }
    }
}