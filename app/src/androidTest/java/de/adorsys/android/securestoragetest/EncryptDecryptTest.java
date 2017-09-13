package de.adorsys.android.securestoragetest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class EncryptDecryptTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testEncryptionWorked() {
        final String testString = "TEST_STRING";
        final String KEY = "TEMPTAG";

        onView(withId(R.id.plain_message_edit_text)).perform(typeText(testString));
        onView(withId(R.id.generate_key_button)).perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        Assert.assertNotNull(SecurePreferences.getStringValue(KEY, ""));
        Assert.assertEquals(testString, SecurePreferences.getStringValue(KEY, ""));
    }
}
