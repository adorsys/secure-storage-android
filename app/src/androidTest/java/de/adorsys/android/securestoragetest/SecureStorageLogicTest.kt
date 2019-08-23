@file:Suppress("LocalVariableName")

package de.adorsys.android.securestoragetest

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import de.adorsys.android.securestoragelibrary.SecurePreferences
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
open class SecureStorageLogicTest : SecureStorageBaseTest() {

    // "\uD83D\uDE8C" = 🚌
    @Test
    fun testStoreRetrieveAndRemoveStringValue() {
        val KEY_STRING = "KEY_STRING"
        val VALUE_STRING = "The wheels on the \uD83D\uDE8C go, Round and round, Round and round, Round and round." +
                " The wheels on the \uD83D\uDE8C go Round and round," +
                " All through the town. The doors on the \uD83D\uDE8C go," +
                " Open and shut ♫, Open and shut ♫, Open and shut."
        val context = activityRule.activity.applicationContext

        // Store a simple String value in SecureStorage
        SecurePreferences.setValue(context, KEY_STRING, VALUE_STRING)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecurePreferences.contains(context, KEY_STRING))

        // Retrieve the previously stored String value from the SecureStorage
        val retrievedValue = SecurePreferences.getStringValue(context, KEY_STRING, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_STRING, retrievedValue)

        // Remove the String value from SecureStorage
        SecurePreferences.removeValue(context, KEY_STRING)

        // Check if the String value has been removed from SecureStorage
        Assert.assertFalse(SecurePreferences.contains(context, KEY_STRING))

        // Delete keys and clear SecureStorage
        SecurePreferences.clearAllValues(context)
    }

    @Test
    fun testStoreRetrieveAndRemoveBooleanValue() {
        val KEY_BOOLEAN = "KEY_BOOLEAN"
        val VALUE_BOOLEAN = true
        val context = activityRule.activity.applicationContext

        // Store a simple Boolean value in SecureStorage
        SecurePreferences.setValue(context, KEY_BOOLEAN, VALUE_BOOLEAN)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecurePreferences.contains(context, KEY_BOOLEAN))

        // Retrieve the previously stored Boolean value from the SecureStorage
        val retrievedValue = SecurePreferences.getBooleanValue(context, KEY_BOOLEAN, false)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_BOOLEAN, retrievedValue)

        // Remove the Boolean value from SecureStorage
        SecurePreferences.removeValue(context, KEY_BOOLEAN)

        // Check if the Boolean value has been removed from SecureStorage
        Assert.assertFalse(SecurePreferences.contains(context, KEY_BOOLEAN))

        // Delete keys and clear SecureStorage
        SecurePreferences.clearAllValues(context)
    }

    @Test
    fun testStoreRetrieveAndRemoveIntValue() {
        val KEY_INT = "KEY_INT"
        val VALUE_INT = 2147483647
        val context = activityRule.activity.applicationContext

        // Store a simple Int value in SecureStorage
        SecurePreferences.setValue(context, KEY_INT, VALUE_INT)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecurePreferences.contains(context, KEY_INT))

        // Retrieve the previously stored Int value from the SecureStorage
        val retrievedValue = SecurePreferences.getIntValue(context, KEY_INT, 93)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_INT, retrievedValue)

        // Remove the Int value from SecureStorage
        SecurePreferences.removeValue(context, KEY_INT)

        // Check if the Int value has been removed from SecureStorage
        Assert.assertFalse(SecurePreferences.contains(context, KEY_INT))

        // Delete keys and clear SecureStorage
        SecurePreferences.clearAllValues(context)
    }

    @Test
    fun testStoreRetrieveAndRemoveLongValue() {
        val KEY_LONG = "KEY_LONG"
        val VALUE_LONG = 9223372036854775807
        val context = activityRule.activity.applicationContext

        // Store a simple Long value in SecureStorage
        SecurePreferences.setValue(context, KEY_LONG, VALUE_LONG)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecurePreferences.contains(context, KEY_LONG))

        // Retrieve the previously stored Long value from the SecureStorage
        val retrievedValue = SecurePreferences.getLongValue(context, KEY_LONG, 93)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_LONG, retrievedValue)

        // Remove the Long value from SecureStorage
        SecurePreferences.removeValue(context, KEY_LONG)

        // Check if the Long value has been removed from SecureStorage
        Assert.assertFalse(SecurePreferences.contains(context, KEY_LONG))

        // Delete keys and clear SecureStorage
        SecurePreferences.clearAllValues(context)
    }

    @Test
    fun testStoreRetrieveAndRemoveFloatValue() {
        val KEY_FLOAT = "KEY_FLOAT"
        val VALUE_FLOAT = Float.MAX_VALUE
        val context = activityRule.activity.applicationContext

        // Store a simple Float value in SecureStorage
        SecurePreferences.setValue(context, KEY_FLOAT, VALUE_FLOAT)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecurePreferences.contains(context, KEY_FLOAT))

        // Retrieve the previously stored Float value from the SecureStorage
        val retrievedValue = SecurePreferences.getFloatValue(context, KEY_FLOAT, 9.3f)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_FLOAT, retrievedValue)

        // Remove the Float value from SecureStorage
        SecurePreferences.removeValue(context, KEY_FLOAT)

        // Check if the Float value has been removed from SecureStorage
        Assert.assertFalse(SecurePreferences.contains(context, KEY_FLOAT))

        // Delete keys and clear SecureStorage
        SecurePreferences.clearAllValues(context)
    }
}