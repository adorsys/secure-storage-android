@file:Suppress("LocalVariableName")

package de.adorsys.android.securestoragetest

import android.content.Context
import android.os.Build
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import de.adorsys.android.securestoragelibrary.SecureStorage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
open class SecureStorage2LogicTest : SecureStorage2BaseTest() {

    // "\uD83D\uDE8C" = 🚌
    @Test
    fun testStoreRetrieveAndRemoveStringValue() {
        val KEY_STRING = "KEY_STRING"
        val VALUE_STRING = "The wheels on the \uD83D\uDE8C go, Round and round, Round and round, Round and round." +
                " The wheels on the \uD83D\uDE8C go Round and round," +
                " All through the town. The doors on the \uD83D\uDE8C go," +
                " Open and shut ♫, Open and shut ♫, Open and shut. The doors on the \uD83D\uDE8C go Open and shut," +
                " All through the town. The Driver on the \uD83D\uDE8C says, \"Move on back! Move on back! Move on back!\"" +
                " The Driver on the \uD83D\uDE8C says, \"Move on back!\" All through the town."
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple String value in SecureStorage
        SecureStorage.putString(context, KEY_STRING, VALUE_STRING)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_STRING))

        // Retrieve the previously stored String value from the SecureStorage
        val retrievedValue = SecureStorage.getString(context, KEY_STRING, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_STRING, retrievedValue)

        // Remove the String value from SecureStorage
        SecureStorage.remove(context, KEY_STRING)

        // Check if the String value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_STRING))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    @Test
    fun testStoreRetrieveAndRemoveBooleanValue() {
        val KEY_BOOLEAN = "KEY_BOOLEAN"
        val VALUE_BOOLEAN = true
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple Boolean value in SecureStorage
        SecureStorage.putBoolean(context, KEY_BOOLEAN, VALUE_BOOLEAN)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_BOOLEAN))

        // Retrieve the previously stored Boolean value from the SecureStorage
        val retrievedValue = SecureStorage.getBoolean(context, KEY_BOOLEAN, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_BOOLEAN, retrievedValue)

        // Remove the Boolean value from SecureStorage
        SecureStorage.remove(context, KEY_BOOLEAN)

        // Check if the Boolean value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_BOOLEAN))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    @Test
    fun testStoreRetrieveAndRemoveIntValue() {
        val KEY_INT = "KEY_INT"
        val VALUE_INT = 2147483647
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple Int value in SecureStorage
        SecureStorage.putInt(context, KEY_INT, VALUE_INT)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_INT))

        // Retrieve the previously stored Int value from the SecureStorage
        val retrievedValue = SecureStorage.getInt(context, KEY_INT, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_INT, retrievedValue)

        // Remove the Int value from SecureStorage
        SecureStorage.remove(context, KEY_INT)

        // Check if the Int value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_INT))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    @Test
    fun testStoreRetrieveAndRemoveLongValue() {
        val KEY_LONG = "KEY_LONG"
        val VALUE_LONG = 9223372036854775807
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple Long value in SecureStorage
        SecureStorage.putLong(context, KEY_LONG, VALUE_LONG)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_LONG))

        // Retrieve the previously stored Long value from the SecureStorage
        val retrievedValue = SecureStorage.getLong(context, KEY_LONG, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_LONG, retrievedValue)

        // Remove the Long value from SecureStorage
        SecureStorage.remove(context, KEY_LONG)

        // Check if the Long value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_LONG))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    @Test
    fun testStoreRetrieveAndRemoveDoubleValue() {
        val KEY_DOUBLE = "KEY_DOUBLE"
        val VALUE_DOUBLE = Double.MAX_VALUE
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple Double value in SecureStorage
        SecureStorage.putDouble(context, KEY_DOUBLE, VALUE_DOUBLE)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_DOUBLE))

        // Retrieve the previously stored Double value from the SecureStorage
        val retrievedValue = SecureStorage.getDouble(context, KEY_DOUBLE, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        // If test arrived here then retrievedValue cannot be null
        Assert.assertEquals(VALUE_DOUBLE, retrievedValue!!, 0.0)

        // Remove the Double value from SecureStorage
        SecureStorage.remove(context, KEY_DOUBLE)

        // Check if the Double value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_DOUBLE))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    @Test
    fun testStoreRetrieveAndRemoveFloatValue() {
        val KEY_FLOAT = "KEY_FLOAT"
        val VALUE_FLOAT = Float.MAX_VALUE
        val context = activityRule.activity.applicationContext

        // Simply generate keys
        SecureStorage.initSecureStorageKeys(context)

        // Check if INSTALLATION_FLAG exists in SecureStorage
        assertInstallationFlag(context, true)

        // Store a simple Float value in SecureStorage
        SecureStorage.putFloat(context, KEY_FLOAT, VALUE_FLOAT)

        // Check if the value exists in SecureStorage
        Assert.assertTrue(SecureStorage.contains(context, KEY_FLOAT))

        // Retrieve the previously stored Float value from the SecureStorage
        val retrievedValue = SecureStorage.getFloat(context, KEY_FLOAT, null)

        // Check if the retrievedValue is not null
        Assert.assertNotNull(retrievedValue)

        // Check if the retrievedValue equals the pre-stored value
        Assert.assertEquals(VALUE_FLOAT, retrievedValue)

        // Remove the Float value from SecureStorage
        SecureStorage.remove(context, KEY_FLOAT)

        // Check if the Float value has been removed from SecureStorage
        Assert.assertFalse(SecureStorage.contains(context, KEY_FLOAT))

        // Delete keys and clear SecureStorage
        SecureStorage.clearAllValuesAndDeleteKeys(context)

        // Check if INSTALLATION_FLAG is no longer in SecureStorage, which would mean SecureStorage was cleared
        assertInstallationFlag(context, false)
    }

    private fun assertInstallationFlag(context: Context, shouldExist: Boolean) {
        val INSTALLATION_FLAG_KEY = "INSTALLATION_API_VERSION_UNDER_M"
        // INSTALLATION_FLAG is set only on API 21 & 22
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> when {
                shouldExist -> // Check if INSTALLATION_FLAG exists in SecureStorage
                    Assert.assertTrue(SecureStorage.contains(context, INSTALLATION_FLAG_KEY))
                else -> // Check if INSTALLATION_FLAG exists in SecureStorage
                    Assert.assertFalse(SecureStorage.contains(context, INSTALLATION_FLAG_KEY))
            }
        }
    }
}