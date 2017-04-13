package de.adorsys.android.securestorage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Drilon Reçica
 * @since 2/17/17.
 */
public class SecurePreferences {
    private static final String KEY_SHARED_PREFERENCES_NAME = "SecurePreferences";

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key,
                                @NonNull String value,
                                @NonNull Context context) throws CryptoException {
        if (!KeystoreTool.keyPairExists()) {
            KeystoreTool.generateKeyPair(context);
        }

        String transformedValue = KeystoreTool.encryptMessage(context, value);
        if (!TextUtils.isEmpty(transformedValue)) {
            setSecureValue(key, transformedValue, context);
        } else {
            throw new CryptoException(context.getString(R.string.message_problem_encryption), null);
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, boolean value,
                                @NonNull Context context) throws CryptoException {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, float value,
                                @NonNull Context context) throws CryptoException {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, long value,
                                @NonNull Context context) throws CryptoException {
        setValue(key, String.valueOf(value), context);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void setValue(@NonNull String key, int value,
                                @NonNull Context context) throws CryptoException {
        setValue(key, String.valueOf(value), context);
    }


    @Nullable
    public static String getStringValue(@NonNull String key,
                                        @NonNull Context context,
                                        @Nullable String defValue) {
        String result = getSecureValue(key, context);
        try {
            if (!TextUtils.isEmpty(result)) {
                return KeystoreTool.decryptMessage(context, result);
            } else {
                return defValue;
            }
        } catch (CryptoException e) {
            return defValue;
        }
    }

    public static boolean getBooleanValue(@NonNull String key, @NonNull Context context, boolean defValue) {
        return Boolean.parseBoolean(getStringValue(key, context, String.valueOf(defValue)));
    }

    public static float getFloatValue(@NonNull String key, @NonNull Context context, float defValue) {
        return Float.parseFloat(getStringValue(key, context, String.valueOf(defValue)));
    }

    public static float getLongValue(@NonNull String key, @NonNull Context context, long defValue) {
        return Long.parseLong(getStringValue(key, context, String.valueOf(defValue)));
    }

    public static float getIntValue(@NonNull String key, @NonNull Context context, int defValue) {
        return Integer.parseInt(getStringValue(key, context, String.valueOf(defValue)));
    }


    public static void clearAllValues(@NonNull Context context) throws CryptoException {
        if (KeystoreTool.keyPairExists()) {
            KeystoreTool.deleteKeyPair(context);
        }
        clearAllSecureValues(context);
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    private static void setSecureValue(@NonNull String key, @NonNull String value, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    @Nullable
    private static String getSecureValue(@NonNull String key, @NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    private static void clearAllSecureValues(@NonNull Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}