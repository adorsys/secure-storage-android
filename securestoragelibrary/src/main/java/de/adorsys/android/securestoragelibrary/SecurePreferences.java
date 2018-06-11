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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.CRYPTO_EXCEPTION;
import static de.adorsys.android.securestoragelibrary.SecureStorageProvider.context;

public final class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String KEY_SET_COUNT_POSTFIX = "_count";

    // hidden constructor to disable initialization
    private SecurePreferences() {
    }

    public static void setValue(@NonNull String key,
                                @NonNull String value) throws SecureStorageException {
        if (!KeystoreTool.keyPairExists()) {
            KeystoreTool.generateKeyPair();
        }

        String transformedValue = KeystoreTool.encryptMessage(value);
        if (TextUtils.isEmpty(transformedValue)) {
            throw new SecureStorageException(context.get().getString(R.string.message_problem_encryption), null, CRYPTO_EXCEPTION);
        } else {
            setSecureValue(key, transformedValue);
        }
    }

    public static void setValue(@NonNull String key,
                                boolean value) throws SecureStorageException {
        setValue(key, String.valueOf(value));
    }

    public static void setValue(@NonNull String key,
                                float value) throws SecureStorageException {
        setValue(key, String.valueOf(value));
    }

    public static void setValue(@NonNull String key,
                                long value) throws SecureStorageException {
        setValue(key, String.valueOf(value));
    }

    public static void setValue(@NonNull String key,
                                int value) throws SecureStorageException {
        setValue(key, String.valueOf(value));
    }

    public static void setValue(@NonNull String key,
                                @NonNull Set<String> value) throws SecureStorageException {
        setValue(key + KEY_SET_COUNT_POSTFIX, String.valueOf(value.size()));

        int i = 0;
        for (String s : value) {
            setValue(key + "_" + (i++), s);
        }
    }

    @Nullable
    public static String getStringValue(@NonNull String key,
                                        @Nullable String defValue) {
        String result = getSecureValue(key);
        try {
            if (!TextUtils.isEmpty(result)) {
                return KeystoreTool.decryptMessage(result);
            } else {
                return defValue;
            }
        } catch (SecureStorageException e) {
            return defValue;
        }
    }

    public static boolean getBooleanValue(@NonNull String key,
                                          boolean defValue) {
        return Boolean.parseBoolean(getStringValue(key, String.valueOf(defValue)));
    }

    public static float getFloatValue(@NonNull String key,
                                      float defValue) {
        return Float.parseFloat(getStringValue(key, String.valueOf(defValue)));
    }

    public static long getLongValue(@NonNull String key,
                                    long defValue) {
        return Long.parseLong(getStringValue(key, String.valueOf(defValue)));
    }

    public static int getIntValue(@NonNull String key,
                                  int defValue) {
        return Integer.parseInt(getStringValue(key, String.valueOf(defValue)));
    }

    @NonNull
    public static Set<String> getStringSetValue(@NonNull String key,
                                                @NonNull Set<String> defValue) {
        int size = getIntValue(key + KEY_SET_COUNT_POSTFIX, -1);

        if (size == -1) {
            return defValue;
        }

        Set<String> res = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            res.add(getStringValue(key + "_" + i, ""));
        }

        return res;
    }

    public static boolean contains(@NonNull String key) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.contains(key);
    }

    public static void removeValue(@NonNull String key) {
        removeSecureValue(key);
    }

    public static void clearAllValues() throws SecureStorageException {
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair();
        }
        clearAllSecureValues();
    }

    @NonNull
    public static Map<String, ?> getAllKeysWithValues(SharedPreferences preferences) {
        return preferences.getAll();
    }

    public static void registerOnSharedPreferenceChangeListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static void setSecureValue(@NonNull String key,
                                       @NonNull String value) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    @Nullable
    private static String getSecureValue(@NonNull String key) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private static void removeSecureValue(@NonNull String key) {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().remove(key).apply();
    }

    private static void clearAllSecureValues() {
        SharedPreferences preferences = context.get()
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}