/*
 * Copyright (C) 2017 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.android.securestoragetest

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import de.adorsys.android.securestoragelibrary.SecurePreferences
import de.adorsys.android.securestoragelibrary.SecureStorageException
import de.adorsys.android.securestoragelibrary.SecureStorageException.ExceptionType.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val KEY = "TEMPTAG"
        private val TAG = "LOGTAG"
    }

    private lateinit var inputEditText: EditText
    private lateinit var keyInfoTextView: TextView
    private lateinit var generateKeyButton: Button
    private lateinit var clearFieldButton: Button
    private lateinit var shieldImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputEditText = findViewById(R.id.plain_message_edit_text)
        keyInfoTextView = findViewById(R.id.key_info_text_view)
        generateKeyButton = findViewById(R.id.generate_key_button)
        clearFieldButton = findViewById(R.id.clear_field_button)
        shieldImageView = findViewById(R.id.shield_image)

        generateKeyButton.setOnClickListener { handleOnGenerateKeyButtonClick() }
        clearFieldButton.setOnClickListener { handleOnClearFieldButtonClick() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_all -> {
                try {
                    SecurePreferences.clearAllValues()
                    Toast.makeText(this@MainActivity, "SecurePreferences cleared and KeyPair deleted", Toast.LENGTH_SHORT).show()
                    inputEditText.setText("")
                    keyInfoTextView.text = ""
                    clearFieldButton.isEnabled = false
                    shieldImageView.setImageResource(R.drawable.shield_unlocked)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
                return true
            }
            R.id.action_info -> {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/adorsys/secure-storage-android/blob/master/README.md")))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun handleOnGenerateKeyButtonClick() {
        if (!TextUtils.isEmpty(inputEditText.text)) {
            if (generateKeyButton.text.toString() == getString(R.string.button_generate_encrypt)) {
                generateKeyButton.setText(R.string.button_encrypt)
            }
            try {
                SecurePreferences.setValue(KEY, inputEditText.text.toString())
                val decryptedMessage = SecurePreferences.getStringValue(KEY, "")

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.duration = 500
                val fadeOut = AlphaAnimation(1f, 0f)
                fadeOut.duration = 500

                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        shieldImageView.setImageResource(R.drawable.shield_locked)
                        shieldImageView.startAnimation(fadeIn)
                        clearFieldButton.isEnabled = true

                        val finalMessage = String.format(getString(R.string.message_encrypted_decrypted,
                                inputEditText.text.toString(), decryptedMessage))
                        keyInfoTextView.text = getSpannedText(finalMessage)
                    }
                })
                shieldImageView.startAnimation(fadeOut)
            } catch (e: SecureStorageException) {
                handleException(e)
            }
        } else {
            Toast.makeText(this@MainActivity, "Field cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleOnClearFieldButtonClick() {
        SecurePreferences.removeValue(KEY)
        inputEditText.setText("")
        keyInfoTextView.text = ""
        clearFieldButton.isEnabled = false
        shieldImageView.setImageResource(R.drawable.shield_unlocked)
    }

    private fun getSpannedText(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
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
}