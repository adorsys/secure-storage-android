package de.adorsys.android.securestoragetest;

import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.adorsys.android.securestoragelibrary.SecureStorageException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SecureStorageUITest {
    private static final String KEY = "KEY_STRING";
    private static final String VALUE = "VALUE_STRING";

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws SecureStorageException {
        // Set an empty value in securePreferences to create key for usage in test.
        SecurePreferences.setValue(activityRule.getActivity(), KEY, VALUE);
    }

    @Test
    public void testEncryptionWorked() {
        onView(withId(R.id.plain_message_edit_text))
                .perform(scrollTo())
                .perform(typeText(VALUE))
                .perform(closeSoftKeyboard());

        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));

        onView(withId(R.id.generate_key_button))
                .perform(scrollTo())
                .perform(click());

        assertNotNull(SecurePreferences.getStringValue(activityRule.getActivity(), KEY, null));
        assertTrue(SecurePreferences.contains(activityRule.getActivity(), KEY));

        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));

        assertEquals(VALUE, SecurePreferences.getStringValue(activityRule.getActivity(), KEY, ""));
    }
}
