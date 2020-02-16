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

package de.adorsys.android.securestoragelibrary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import de.adorsys.android.securestoragelibrary.internal.KeyStoreTool
import java.lang.Boolean.parseBoolean
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.lang.Long.parseLong

@Suppress("unused")
object SecureStorage {
    internal const val KEY_INSTALLATION_API_VERSION_UNDER_M = "INSTALLATION_API_VERSION_UNDER_M"

    internal lateinit var SHARED_PREFERENCES_NAME: String
    internal lateinit var ENCRYPTION_KEY_ALIAS: String
    internal lateinit var X500PRINCIPAL: String

    /**
     *
     * Initialize the library with desired options
     *
     * @param context Context is used internally
     * @param encryptionKeyAlias Alias for the encryption key/keypair (default value: SecureStorage2Key)
     * @param x500Principal Distinguished Name used for generating KeyPair for asymmetric en/decryption
     * (default value: CN=SecureStorage2 , O=Adorsys GmbH & Co. KG., C=Germany)
     */
    fun init(
            context: Context,
            encryptionKeyAlias: String? = null,
            x500Principal: String? = null
    ) {
        SHARED_PREFERENCES_NAME = context.applicationContext.packageName + ".SecureStorage2"
        ENCRYPTION_KEY_ALIAS = encryptionKeyAlias ?: "SecureStorage2Key"
        X500PRINCIPAL = x500Principal ?: "CN=SecureStorage2 , O=Adorsys GmbH & Co. KG., C=Germany"
    }

    /**
     *
     * Initialize SecureStorage keys for library usage.
     *
     * @param context Context is used internally
     *
     */
    @Throws(SecureStorageException::class)
    fun initSecureStorageKeys(context: Context) {
        KeyStoreTool.setInstallApiVersionFlag(context.applicationContext)

        when {
            !KeyStoreTool.keyExists(context.applicationContext) -> KeyStoreTool.generateKey(context.applicationContext)
        }
    }

    /**
     *
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain String value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putString(context: Context, key: String, value: String) {
        val encryptedValue = KeyStoreTool.encryptValue(context.applicationContext, key, value)

        putSecureValue(context.applicationContext, key, encryptedValue)
    }

    /**
     *
     * Takes plain boolean value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain boolean value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putBoolean(context: Context, key: String, value: Boolean) =
            putString(context.applicationContext, key, value.toString())

    /**
     *
     * Takes plain int value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain int value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putInt(context: Context, key: String, value: Int) = putString(context.applicationContext, key, value.toString())

    /**
     *
     * Takes plain long value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain long value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putLong(context: Context, key: String, value: Long) =
            putString(context.applicationContext, key, value.toString())

    /**
     *
     * Takes plain double value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain double value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putDouble(context: Context, key: String, value: Double) =
            putString(context.applicationContext, key, value.toString())

    /**
     *
     * Takes plain float value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param value Plain float value that will be encrypted and stored in the SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun putFloat(context: Context, key: String, value: Float) =
            putString(context.applicationContext, key, value.toString())

    /**
     *
     * Gets encrypted String value for given key from the SecureStorage on the Android Device, decrypts it
     * and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default String value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted String value associated with given key from SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun getString(context: Context, key: String, defaultValue: String?): String? {
        val encryptedValue = getSecureValue(context.applicationContext, key)

        return when {
            encryptedValue.isNullOrBlank() -> defaultValue
            else -> {
                val decryptedValue = KeyStoreTool.decryptValue(context.applicationContext, key, encryptedValue)
                when {
                    decryptedValue.isNullOrBlank() -> defaultValue
                    else -> decryptedValue
                }
            }
        }

    }

    /**
     *
     * Gets encrypted boolean value for given key from the SecureStorage on the Android Device, decrypts it
     * and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default boolean value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted boolean value associated with given key from SecureStorage
     *
     */
    fun getBoolean(context: Context, key: String, defaultValue: Boolean?): Boolean =
            parseBoolean(
                    getString(
                            context.applicationContext,
                            key,
                            defaultValue?.toString()
                    )
            )

    /**
     *
     * Gets encrypted int value for given key  from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default int value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted int value associated with given key from SecureStorage
     *
     */
    fun getInt(context: Context, key: String, defaultValue: Int?): Int? {
        val retrievedValue = getString(
                context.applicationContext,
                key,
                defaultValue?.toString()
        )
        return when {
            retrievedValue.isNullOrBlank() -> defaultValue
            else -> parseInt(retrievedValue)
        }
    }

    /**
     *
     * Gets encrypted long value for given key from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default long value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted long value associated with given key from SecureStorage
     *
     */
    fun getLong(context: Context, key: String, defaultValue: Long?): Long? {
        val retrievedValue = getString(
                context.applicationContext,
                key,
                defaultValue?.toString()
        )
        return when {
            retrievedValue.isNullOrBlank() -> defaultValue
            else -> parseLong(retrievedValue)
        }
    }

    /**
     *
     * Gets encrypted double value for given key from the SecureStorage on the Android Device, decrypts it
     * and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default double value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted double value associated with given key from SecureStorage
     *
     */
    fun getDouble(context: Context, key: String, defaultValue: Double?): Double? {
        val retrievedValue = getString(
                context.applicationContext,
                key,
                defaultValue?.toString()
        )
        return when {
            retrievedValue.isNullOrBlank() -> defaultValue
            else -> parseDouble(retrievedValue)
        }
    }

    /**
     *
     * Gets encrypted float value for given key from the SecureStorage on the Android Device, decrypts it
     * and returns it
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     * @param defaultValue Default float value that will be returned if the value with given key doesn't exist or
     * an exception is thrown
     *
     * @return Decrypted float value associated with given key from SecureStorage
     *
     */
    fun getFloat(context: Context, key: String, defaultValue: Float?): Float? {
        val retrievedValue = getString(
                context.applicationContext,
                key,
                defaultValue?.toString()
        )
        return when {
            retrievedValue.isNullOrBlank() -> defaultValue
            else -> parseFloat(retrievedValue)
        }
    }

    /**
     *
     * Checks if SecureStorage contains a value for the given key (Does not return the value or check what type it is)
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     *
     * @return True if value exists in SecureStorage, otherwise false
     *
     */
    @Throws(SecureStorageException::class)
    fun contains(context: Context, key: String): Boolean {
        return getSharedPreferencesInstance(context.applicationContext).contains(key)
    }

    /**
     *
     * Removes the value for a given key from SecureStorage
     *
     * @param context Context is used internally
     * @param key Key used to identify the stored value in SecureStorage
     *
     */
    @Throws(SecureStorageException::class)
    fun remove(context: Context, key: String) {
        removeSecureValue(context.applicationContext, key)
    }

    /**
     *
     * Clears all values from the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     *
     */
    @Throws(SecureStorageException::class)
    fun clearAllValues(context: Context) {
        val apiVersionUnderMExisted = contains(context.applicationContext, KEY_INSTALLATION_API_VERSION_UNDER_M)

        clearAllSecureValues(context.applicationContext)

        when {
            apiVersionUnderMExisted -> KeyStoreTool.setInstallApiVersionFlag(context.applicationContext, true)
        }
    }

    /**
     *
     * Clears all values from the SecureStorage on the Android Device and deletes the en/decryption keys
     * Means new keys/keypairs have to be generated for the library to be able to work
     *
     * @param context Context is used internally
     *
     */
    @Throws(SecureStorageException::class)
    fun clearAllValuesAndDeleteKeys(context: Context) {
        when {
            KeyStoreTool.keyExists(context.applicationContext) -> KeyStoreTool.deleteKey(context.applicationContext)
        }
        clearAllValues(context.applicationContext)
    }

    /**
     *
     * Registers SecureStorageChangeListener to listen to any changes in SecureStorage
     *
     * @param context Context is used internally
     * @param listener Provided listener with given behaviour from the developer that will be registered
     *
     */
    @Throws(SecureStorageException::class)
    fun registerOnSecureStorageChangeListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSharedPreferencesInstance(context.applicationContext).registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     *
     * Unregisters SecureStorageChangeListener from SecureStorage
     *
     * @param context Context is used internally
     * @param listener Provided listener with given behaviour from the developer that will be unregistered
     *
     */
    @Throws(SecureStorageException::class)
    fun unregisterOnSecureStorageChangeListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSharedPreferencesInstance(context.applicationContext).unregisterOnSharedPreferenceChangeListener(listener)
    }

    internal fun getSharedPreferencesInstance(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
        )
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSecureValue(context: Context, key: String, value: String) {
        getSharedPreferencesInstance(context).edit().putString(key, value).execute()
    }

    private fun getSecureValue(context: Context, key: String): String? =
            getSharedPreferencesInstance(context).getString(key, null)

    @SuppressLint("CommitPrefEdits")
    private fun removeSecureValue(context: Context, key: String) {
        getSharedPreferencesInstance(context).edit().remove(key).execute()
    }

    private fun clearAllSecureValues(context: Context) = getSharedPreferencesInstance(context).edit().clear().execute()
}

//================================================================================
// SecureStorage Extension Function
//================================================================================

internal fun SharedPreferences.Editor.execute() {
    apply()
}