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

public final class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String KEY_SET_COUNT_POSTFIX = "_count";

    // hidden constructor to disable initialization
    private SecurePreferences() {
    }

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

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                boolean value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                float value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                long value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                int value) throws SecureStorageException {
        setValue(context, key, String.valueOf(value));
    }

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                @NonNull Set<String> value) throws SecureStorageException {
        setValue(context, key + KEY_SET_COUNT_POSTFIX, String.valueOf(value.size()));

        int i = 0;
        for (String s : value) {
            setValue(context, key + "_" + (i++), s);
        }
    }

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

    public static boolean getBooleanValue(@NonNull Context context,
                                          @NonNull String key,
                                          boolean defValue) {
        return Boolean.parseBoolean(getStringValue(context, key, String.valueOf(defValue)));
    }

    public static float getFloatValue(@NonNull Context context,
                                      @NonNull String key,
                                      float defValue) {
        return Float.parseFloat(getStringValue(context, key, String.valueOf(defValue)));
    }

    public static long getLongValue(@NonNull Context context,
                                    @NonNull String key,
                                    long defValue) {
        return Long.parseLong(getStringValue(context, key, String.valueOf(defValue)));
    }

    public static int getIntValue(@NonNull Context context,
                                  @NonNull String key,
                                  int defValue) {
        return Integer.parseInt(getStringValue(context, key, String.valueOf(defValue)));
    }

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

    public static void removeValue(@NonNull Context context,
                                   @NonNull String key) {
        Context applicationContext = context.getApplicationContext();
        removeSecureValue(applicationContext, key);
    }

    public static void clearAllValues(@NonNull Context context) throws SecureStorageException {
        Context applicationContext = context.getApplicationContext();
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(applicationContext);
        }
        clearAllSecureValues(applicationContext);
    }

    public static void registerOnSharedPreferenceChangeListener(@NonNull Context context,
                                                                @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        Context applicationContext = context.getApplicationContext();
        SharedPreferences preferences = applicationContext
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

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