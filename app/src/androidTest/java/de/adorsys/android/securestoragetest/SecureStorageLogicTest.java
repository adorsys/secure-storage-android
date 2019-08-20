package de.adorsys.android.securestoragetest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.adorsys.android.securestoragelibrary.SecureStorageException;

@RunWith(AndroidJUnit4.class)
public class SecureStorageLogicTest {
    private static final String KEY = "KEY_STRING";
    private static final String VALUE = "VALUE_STRING";
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testEncryptionAndDecryption() throws SecureStorageException {
        SecurePreferences.clearAllValues(activityRule.getActivity());

        SecurePreferences.setValue(activityRule.getActivity(), KEY, VALUE);

        Assert.assertTrue(SecurePreferences.contains(activityRule.getActivity(), KEY));

        String retrievedValue = SecurePreferences.getStringValue(activityRule.getActivity(), KEY, null);

        Assert.assertNotNull(retrievedValue);

        Assert.assertEquals(retrievedValue, VALUE);
    }
}