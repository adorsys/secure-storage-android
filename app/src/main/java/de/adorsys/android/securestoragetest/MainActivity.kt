package de.adorsys.android.securestoragetest

import android.os.*
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import de.adorsys.android.securestoragelibrary.SecureStorageException
import de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    companion object {
        private val KEY = "TEMPTAG"
        private val TAG = "LOGTAG"
    }

    private lateinit var inputEditText: EditText
    private lateinit var keyInfoTextView: TextView
    private lateinit var generateKeyButton: Button
    private lateinit var clearPreferencesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputEditText = findViewById(R.id.plain_message_edit_text)
        keyInfoTextView = findViewById(R.id.key_info_text_view)
        generateKeyButton = findViewById(R.id.generate_key_button)
        clearPreferencesButton = findViewById(R.id.clear_preferences_button)

        generateKeyButton.setOnClickListener {
            if (!TextUtils.isEmpty(inputEditText.text)) {
                EncryptAsyncTask(WeakReference(this)).execute()
            } else {
                Toast.makeText(this@MainActivity, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        clearPreferencesButton.setOnClickListener {
            try {
                SecurePreferences.clearAllValues(this@MainActivity)
                Toast.makeText(this@MainActivity, "SecurePreferences cleared and KeyPair deleted", Toast.LENGTH_SHORT).show()
                keyInfoTextView.text = ""
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleException(e: SecureStorageException) {
        Log.e(TAG, e.message)
        when (e.type) {
            KEYSTORE_NOT_SUPPORTED_EXCEPTION -> Toast.makeText(this, R.string.error_not_supported, Toast.LENGTH_LONG).show()
            KEYSTORE_EXCEPTION -> Toast.makeText(this, R.string.error_fatal, Toast.LENGTH_LONG).show()
            CRYPTO_EXCEPTION -> Toast.makeText(this, R.string.error_encryption, Toast.LENGTH_LONG).show()
            INTERNAL_LIBRARY_EXCEPTION -> Toast.makeText(this, R.string.error_library, Toast.LENGTH_LONG).show()
            else -> return
        }
    }

    class EncryptAsyncTask(private val activity: WeakReference<MainActivity>) : AsyncTask<Void, Boolean, Boolean>() {
        private val handler = Handler(Looper.getMainLooper())
        private var decryptedMessage: String? = null

        override fun doInBackground(vararg params: Void?): Boolean? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val activity = activity.get() ?: return false
                val generateKeyButton = activity.generateKeyButton
                val inputEditText = activity.inputEditText
                if (generateKeyButton.text.toString() == activity.getString(R.string.button_generate_encrypt)) {
                    generateKeyButton.setText(R.string.button_encrypt)
                }
                try {
                    SecurePreferences.setValue(activity, KEY, inputEditText.text.toString())
                    decryptedMessage = SecurePreferences.getStringValue(activity, KEY, "")
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, decryptedMessage!! + " ")
                    }
                    return true
                } catch (e: SecureStorageException) {
                    handler.post({
                        activity.handleException(e)
                    })
                    return false
                }
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            val activity = activity.get() ?: return
            val keyInfoTextView = activity.keyInfoTextView
            val inputEditText = activity.inputEditText
            keyInfoTextView.text = activity.getString(R.string.message_encrypted_decrypted,
                    inputEditText.text.toString(), decryptedMessage)
        }
    }
}