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
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
	companion object {
		private val KEY = "TEMPTAG"
		private val TAG = "LOGTAG"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val inputEditText = findViewById(R.id.plain_message_edit_text) as EditText
		val keyInfoTextView = findViewById(R.id.key_info_text_view) as TextView
		val generateKeyButton = findViewById(R.id.generate_key_button) as Button
		val clearPreferencesButton = findViewById(R.id.clear_preferences_button) as Button

		generateKeyButton.setOnClickListener {
			if (!TextUtils.isEmpty(inputEditText.text)) {
				EncryptTask(
						WeakReference(this),
						WeakReference(generateKeyButton),
						WeakReference(inputEditText),
						WeakReference(keyInfoTextView)).execute()
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

	class EncryptTask(private val activity: WeakReference<MainActivity>,
					  private val generateKeyButton: WeakReference<Button>,
					  private val inputEditText: WeakReference<EditText>,
					  private val keyInfoTextView: WeakReference<TextView>) : AsyncTask<Void, Boolean, Boolean>() {
		private val handler = Handler(Looper.getMainLooper())
		private var decryptedMessage: String? = null

		override fun doInBackground(vararg params: Void?): Boolean? {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				val activity = activity.get() ?: return false
				val generateKeyButton = generateKeyButton.get() ?: return false
				val inputEditText = inputEditText.get() ?: return false
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
				} catch (e: Exception) {
					handler.post({Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()})
					return false
				}
			}
			return false
		}

		override fun onPostExecute(result: Boolean?) {
			val activity = activity.get() ?: return
			val keyInfoTextView = keyInfoTextView.get() ?: return
			val inputEditText = inputEditText.get() ?: return
			keyInfoTextView.text = activity.getString(R.string.message_encrypted_decrypted,
					inputEditText.text.toString(), decryptedMessage)

		}
	}
}