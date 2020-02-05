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
import android.os.Build
import android.view.View.LAYOUT_DIRECTION_RTL
import de.adorsys.android.securestoragelibrary.SecureStorage
import de.adorsys.android.securestoragelibrary.SecureStorage.KEY_INSTALLATION_API_VERSION_UNDER_M
import de.adorsys.android.securestoragelibrary.SecureStorageException
import de.adorsys.android.securestoragelibrary.execute
import de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher

@SuppressLint("NewApi")
internal object KeyStoreTool {

    private const val KEY_KEYSTORE_NAME = "AndroidKeyStore"
    private const val KEY_ASYMMETRIC_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private const val KEY_SYMMETRIC_TRANSFORMATION_ALGORITHM = "AES/CBC/PKCS7Padding"

    //================================================================================
    // SecureStorage KeyStoreTool Logic
    //================================================================================

    internal fun generateKey(context: Context) {
        when {
            !keyExists(context) -> {
                when {
                    isRTL(context) -> Locale.setDefault(Locale.US)
                }
                when {
                    apiVersionMAndAbove(context) -> KeyStoreToolApi23.generateKey(context)
                    else -> KeyStoreToolApi21.generateKey(context)
                }
            }
        }
    }

    internal fun keyExists(context: Context): Boolean {
        return when {
            apiVersionMAndAbove(context) -> KeyStoreToolApi23.keyExists(getKeyStoreInstance())
            else -> KeyStoreToolApi21.keyExists(getKeyStoreInstance())
        }
    }

    internal fun encryptValue(context: Context, key: String, value: String): String {
        return when {
            apiVersionMAndAbove(context) -> KeyStoreToolApi23.encryptValue(
                    context,
                    getKeyStoreInstance(),
                    getCipher(context),
                    key,
                    value
            )
            else -> KeyStoreToolApi21.encryptValue(context, getKeyStoreInstance(), getCipher(context), key, value)
        }
    }

    internal fun decryptValue(context: Context, key: String, value: String): String? {
        return when {
            apiVersionMAndAbove(context) -> KeyStoreToolApi23.decryptValue(
                    context,
                    getKeyStoreInstance(),
                    getCipher(context),
                    key,
                    value
            )
            else -> KeyStoreToolApi21.decryptValue(context, getKeyStoreInstance(), getCipher(context), key, value)
        }
    }

    internal fun deleteKey(context: Context) {
        when {
            apiVersionMAndAbove(context) -> KeyStoreToolApi23.deleteKey(getKeyStoreInstance())
            else -> KeyStoreToolApi21.deleteKey(context, getKeyStoreInstance())
        }
    }

    @SuppressLint("CommitPrefEdits")
    internal fun setInstallApiVersionFlag(context: Context, forceSet: Boolean = false) {
        val preferences = SecureStorage.getSharedPreferencesInstance(context)

        when {
            forceSet -> {
                SecureStorage.getSharedPreferencesInstance(context).edit()
                        .putBoolean(KEY_INSTALLATION_API_VERSION_UNDER_M, true).execute()
                return
            }
            else -> {
                val installationApiVersionUnderM = preferences.contains(KEY_INSTALLATION_API_VERSION_UNDER_M)

                when {
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                            && !installationApiVersionUnderM -> SecureStorage.getSharedPreferencesInstance(
                            context
                    ).edit()
                            .putBoolean(KEY_INSTALLATION_API_VERSION_UNDER_M, true).execute()
                }
            }
        }
    }

    internal fun apiVersionMAndAbove(context: Context): Boolean {
        val installationApiVersionUnderM =
                SecureStorage.getSharedPreferencesInstance(context).contains(KEY_INSTALLATION_API_VERSION_UNDER_M)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !installationApiVersionUnderM
    }

    @Throws(SecureStorageException::class)
    private fun getKeyStoreInstance(): KeyStore {
        try {
            // Get the AndroidKeyStore instance
            val keyStore = KeyStore.getInstance(KEY_KEYSTORE_NAME)

            // Relict of the JCA API - you have to call load even
            // if you do not have an input stream you want to load or it'll crash
            keyStore.load(null)

            return keyStore
        } catch (e: Exception) {
            throw SecureStorageException(
                    if (!e.message.isNullOrBlank()) e.message!!
                    else SecureStorageException.MESSAGE_ERROR_WHILE_GETTING_KEYSTORE_INSTANCE,
                    e,
                    KEYSTORE_EXCEPTION
            )
        }
    }

    @Throws(SecureStorageException::class)
    private fun getCipher(context: Context): Cipher {
        getKeyStoreInstance()

        return when {
            apiVersionMAndAbove(context) -> Cipher.getInstance(KEY_SYMMETRIC_TRANSFORMATION_ALGORITHM)
            else -> // https://stackoverflow.com/a/36394097/3392276
                Cipher.getInstance(KEY_ASYMMETRIC_TRANSFORMATION_ALGORITHM)
        }
    }

    private fun isRTL(context: Context): Boolean =
            context.resources.configuration.layoutDirection == LAYOUT_DIRECTION_RTL
}