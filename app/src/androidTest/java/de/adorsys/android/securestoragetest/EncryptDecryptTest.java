package de.adorsys.android.securestoragetest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.adorsys.android.securestoragelibrary.SecureStorageException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EncryptDecryptTest {
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws SecureStorageException {
        // Set an empty value in securePreferences to create key for usage in test.
        SecurePreferences.setValue(activityRule.getActivity(), "EMPTY", "empty");
    }

    @Test
    public void testEncryptionWorked() {
        final String testString = "TEST_STRING";
        final String KEY = "TEMPTAG";

        onView(withId(R.id.plain_message_edit_text))
                .perform(scrollTo())
                .perform(typeText(testString))
                .perform(closeSoftKeyboard());

        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));

        onView(withId(R.id.generate_key_button))
                .perform(scrollTo())
                .perform(click());

        assertNotNull(SecurePreferences.getStringValue(activityRule.getActivity(), KEY, null));
        assertTrue(SecurePreferences.contains(activityRule.getActivity(), KEY));

        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));

        assertEquals(testString, SecurePreferences.getStringValue(activityRule.getActivity(), KEY, ""));
    }
}
