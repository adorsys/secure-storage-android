/*
 * Copyright (C) 2017 adorsys GmbH & Co. KG
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

package de.adorsys.android.securestoragelibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;
import static de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.CRYPTO_EXCEPTION;

/**
 * Handles every use case for the developer using Secure Storage.
 * Encryption, Decryption, Storage, Removal etc.
 */
public final class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String KEY_SET_COUNT_POSTFIX = "_count";

    // hidden constructor to disable initialization
    private SecurePreferences() {
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain String value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                @NonNull String value) throws SecureStorageException {
        Context applicationContext = context.getApplicationContext();
        if (!KeystoreTool.keyPairExists()) {
            KeystoreTool.generateKeyPair(applicationContext);
        }

        String transformedValue = KeystoreTool.encryptMessage(applicationContext, value);
        if (TextUtils.isEmpty(transformedValue)) {
            throw new SecureStorageException(context.getString(R.string.message_problem_encryption), null, CRYPTO_EXCEPTION);
        } else {
            setSecureValue(applicationContext, key, transformedValue);
        }
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain boolean value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                boolean value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain float value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                float value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain long value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                long value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain int value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                int value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    /**
     * Takes plain string value, encrypts it and stores it encrypted in the SecureStorage on the Android Device
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @param value   Plain Set(type: String) value that will be encrypted and stored in the SecureStorage
     */
    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                @NonNull Set<String> value) throws SecureStorageException {
        setValue(context, key + KEY_SET_COUNT_POSTFIX, String.valueOf(value.size()));

        int i = 0;
        for (String s : value) {
            setValue(context, key + "_" + (i++), s);
        }
    }

    /**
     * Gets encrypted String value for given key from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default String value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted String value associated with given key from SecureStorage
     */
    @Nullable
    public static String getStringValue(@NonNull Context context,
                                        @NonNull String key,
                                        @Nullable String defValue) {
        Context applicationContext = context.getApplicationContext();
        String result = getSecureValue(applicationContext, key);
        try {
            if (!TextUtils.isEmpty(result)) {
                return KeystoreTool.decryptMessage(applicationContext, result);
            } else {
                return defValue;
            }
        } catch (SecureStorageException e) {
            return defValue;
        }
    }

    /**
     * Gets encrypted boolean value for given key from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default boolean value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted boolean value associated with given key from SecureStorage
     */
    public static boolean getBooleanValue(@NonNull Context context,
                                          @NonNull String key,
                                          boolean defValue) {
        return Boolean.parseBoolean(getStringValue(context, key, String.valueOf(defValue)));
    }

    /**
     * Gets encrypted float value for given key from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default float value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted float value associated with given key from SecureStorage
     */
    public static float getFloatValue(@NonNull Context context,
                                      @NonNull String key,
                                      float defValue) {
        return Float.parseFloat(getStringValue(context, key, String.valueOf(defValue)));
    }

    /**
     * Gets encrypted long value for given key from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default long value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted long value associated with given key from SecureStorage
     */
    public static long getLongValue(@NonNull Context context,
                                    @NonNull String key,
                                    long defValue) {
        return Long.parseLong(getStringValue(context, key, String.valueOf(defValue)));
    }

    /**
     * Gets encrypted int value for given key  from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default int value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted int value associated with given key from SecureStorage
     */
    public static int getIntValue(@NonNull Context context,
                                  @NonNull String key,
                                  int defValue) {
        return Integer.parseInt(getStringValue(context, key, String.valueOf(defValue)));
    }

    /**
     * Gets encrypted int value for given key  from the SecureStorage on the Android Device, decrypts it and returns it
     *
     * @param context  Context is used internally
     * @param key      Key used to identify the stored value in SecureStorage
     * @param defValue Default Set(type: String) value that will be returned if the value with given key doesn't exist or an exception is thrown
     * @return Decrypted Set(type: String) value associated with given key from SecureStorage
     */
    @NonNull
    public static Set<String> getStringSetValue(@NonNull Context context,
                                                @NonNull String key,
                                                @NonNull Set<String> defValue) {
        int size = getIntValue(context, key + KEY_SET_COUNT_POSTFIX, -1);

        if (size == -1) {
            return defValue;
        }

        Set<String> res = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            res.add(getStringValue(context, key + "_" + i, ""));
        }

        return res;
    }

    /**
     * Checks if SecureStorage contains a value for the given key (Does not return the value or check what type it is)
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     * @return True if value exists in SecureStorage, otherwise false
     */
    public static boolean contains(@NonNull Context context,
                                   @NonNull String key) {
        Context applicationContext = context.getApplicationContext();
        SharedPreferences preferences = applicationContext
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        try {
            return preferences.contains(key) && KeystoreTool.keyPairExists();
        } catch (SecureStorageException e) {
            return false;
        }
    }

    /**
     * Removes the value for a given key from SecureStorage
     *
     * @param context Context is used internally
     * @param key     Key used to identify the stored value in SecureStorage
     */
    public static void removeValue(@NonNull Context context,
                                   @NonNull String key) {
        Context applicationContext = context.getApplicationContext();
        removeSecureValue(applicationContext, key);
    }

    /**
     * Clears all values from the SecureStorage on the Android Device and deletes the en/decryption keys
     * Means new keys/keypairs have to be generated for the library to be able to work
     *
     * @param context Context is used internally
     */
    public static void clearAllValues(@NonNull Context context) throws SecureStorageException {
        Context applicationContext = context.getApplicationContext();
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(applicationContext);
        }
        clearAllSecureValues(applicationContext);
    }

    /**
     * Registers SecureStorageChangeListener to listen to any changes in SecureStorage
     *
     * @param context  Context is used internally
     * @param listener Provided listener with given behaviour from the developer that will be registered
     */
    public static void registerOnSharedPreferenceChangeListener(@NonNull Context context,
                                                                @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        Context applicationContext = context.getApplicationContext();
        SharedPreferences preferences = applicationContext
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregisters SecureStorageChangeListener from SecureStorage
     *
     * @param context  Context is used internally
     * @param listener Provided listener with given behaviour from the developer that will be unregistered
     */
    public static void unregisterOnSharedPreferenceChangeListener(@NonNull Context context,
                                                                  @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        Context applicationContext = context.getApplicationContext();
        SharedPreferences preferences = applicationContext
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static void setSecureValue(@NonNull Context context,
                                       @NonNull String key,
                                       @NonNull String value) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    @Nullable
    private static String getSecureValue(@NonNull Context context,
                                         @NonNull String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private static void removeSecureValue(@NonNull Context context,
                                          @NonNull String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().remove(key).apply();
    }

    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}