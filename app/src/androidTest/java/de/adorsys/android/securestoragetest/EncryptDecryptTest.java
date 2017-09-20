package de.adorsys.android.securestoragetest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.adorsys.android.securestoragelibrary.SecureStorageException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class EncryptDecryptTest {
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws SecureStorageException {
        // Set an empty value in securePreferences to create key for usage in test.
        SecurePreferences.setValue("EMPTY", "empty");
    }

    @Test
    public void testEncryptionWorked() {
        final String testString = "TEST_STRING";
        final String KEY = "TEMPTAG";

        onView(withId(R.id.plain_message_edit_text)).perform(typeText(testString));
        onView(withId(R.id.generate_key_button)).perform(click());

        Assert.assertNotNull(SecurePreferences.getStringValue(KEY, null));
        Assert.assertEquals(testString, SecurePreferences.getStringValue(KEY, ""));
    }
}
