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

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_store.setOnClickListener {
            when {
                edit_text_store_key.text.isNullOrEmpty() || edit_text_store_value.text.isNullOrEmpty() ->
                    Toast.makeText(
                            this@MainActivity,
                            "Fields cannot be empty",
                            Toast.LENGTH_SHORT
                    ).show()
                else -> {
                    SecurePreferences.setValue(
                            this@MainActivity,
                            edit_text_store_key.text.toString(),
                            edit_text_store_value.text.toString()
                    )

                    text_view_stored_data.text =
                            "Value: ${edit_text_store_value.text} successfully saved for key: ${edit_text_store_key.text}"

                    edit_text_store_key.text.clear()
                    edit_text_store_value.text.clear()

                    text_view_deleted_data.text = ""
                    text_view_retrieved_data.text = ""
                }
            }
        }

        button_get.setOnClickListener {
            when {
                edit_text_get_key.text.isNullOrEmpty() -> Toast.makeText(
                        this@MainActivity,
                        "Field cannot be empty",
                        Toast.LENGTH_SHORT
                ).show()
                else -> {
                    val decryptedData =
                            SecurePreferences.getStringValue(
                                    this@MainActivity,
                                    edit_text_get_key.text.toString(),
                                    "FAILED"
                            )
                    text_view_retrieved_data.text =
                            "Decrypted Data for key: ${edit_text_get_key.text} = $decryptedData"

                    edit_text_get_key.text.clear()
                    text_view_deleted_data.text = ""
                    text_view_stored_data.text = ""
                }
            }
        }

        button_remove.setOnClickListener {
            when {
                edit_text_remove_key.text.isNullOrEmpty() -> Toast.makeText(
                        this@MainActivity,
                        "Field cannot be empty",
                        Toast.LENGTH_SHORT
                ).show()
                else -> {
                    SecurePreferences.removeValue(this@MainActivity, edit_text_remove_key.text.toString())

                    text_view_deleted_data.text = "Value for key: ${edit_text_remove_key.text} successfully deleted"

                    edit_text_remove_key.text.clear()
                    text_view_retrieved_data.text = ""
                    text_view_stored_data.text = ""
                }
            }
        }

    }
}