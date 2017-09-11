package de.adorsys.android.securestoragelibrary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.CRYPTO_EXCEPTION;

public class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";
    private static final String KEY_SET_COUNT_POSTFIX = "_count";

    public static void setValue(@NonNull Context context,
                                @NonNull String key,
                                @NonNull String value) throws SecureStorageException {
        if (!KeystoreTool.keyPairExists()) {
            KeystoreTool.generateKeyPair(context);
        }

        String transformedValue = KeystoreTool.encryptMessage(context, value);
        if (TextUtils.isEmpty(transformedValue)) {
            throw new SecureStorageException(context.getString(R.string.message_problem_encryption), null, CRYPTO_EXCEPTION);
        } else {
            setSecureValue(context, key, transformedValue);
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
        String result = getSecureValue(context, key);
        try {
            if (!TextUtils.isEmpty(result)) {
                return KeystoreTool.decryptMessage(context, result);
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
    public static Set<String> getStringSetValue(@NonNull String key,
                                                @NonNull Context context,
                                                @NonNull Set<String> defValue) {
        int size = getIntValue(context, key + KEY_SET_COUNT_POSTFIX, -1);

        if (size == -1) {
            return defValue;
        }

        Set<String> res = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            res.add(getStringValue(context,key + "_" + i, ""));
        }

        return res;
    }

    public static void removeValue(@NonNull Context context,
                                   @NonNull String key) {
        removeSecureValue(context, key);
    }


    public static void clearAllValues(@NonNull Context context) throws SecureStorageException {
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(context);
        }
        clearAllSecureValues(context);
    }

    @SuppressLint("ApplySharedPref")
    private static void setSecureValue(@NonNull Context context,
                                       @NonNull String key,
                                       @NonNull String value) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    @Nullable
    private static String getSecureValue(@NonNull Context context,
                                         @NonNull String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    @SuppressLint("ApplySharedPref")
    private static void removeSecureValue(@NonNull Context context,
                                          @NonNull String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().remove(key).commit();
    }

    @SuppressLint("ApplySharedPref")
    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}