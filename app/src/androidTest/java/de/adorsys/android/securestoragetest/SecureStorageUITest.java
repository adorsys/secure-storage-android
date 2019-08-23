package de.adorsys.android.securestoragetest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import de.adorsys.android.securestoragelibrary.SecureStorageException;

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
        //TODO implement new UI test based on new Layout
//        onView(withId(R.id.plain_message_edit_text))
//                .perform(scrollTo())
//                .perform(typeText(VALUE))
//                .perform(closeSoftKeyboard());
//
//        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));
//
//        onView(withId(R.id.generate_key_button))
//                .perform(scrollTo())
//                .perform(click());
//
//        assertNotNull(SecurePreferences.getStringValue(activityRule.getActivity(), KEY, null));
//        assertTrue(SecurePreferences.contains(activityRule.getActivity(), KEY));
//
//        Log.d("SecureStorageTest Time", String.valueOf(System.currentTimeMillis()));
//
//        assertEquals(VALUE, SecurePreferences.getStringValue(activityRule.getActivity(), KEY, ""));
    }
}
