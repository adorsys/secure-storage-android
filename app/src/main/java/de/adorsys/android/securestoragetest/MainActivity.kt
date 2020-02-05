package de.adorsys.android.securestoragetest

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecureStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_store.setOnClickListener {
            storeData()
        }

        button_get.setOnClickListener {
            retrieveData()
        }

        button_remove.setOnClickListener {
            removeData()
        }

    }

    @SuppressLint("SetTextI18n")
    fun storeData() {
        when {
            edit_text_store_key.text.isNullOrEmpty() || edit_text_store_value.text.isNullOrEmpty() ->
                Toast.makeText(
                        this@MainActivity,
                        "Fields cannot be empty",
                        Toast.LENGTH_SHORT
                ).show()
            else -> {
                SecureStorage.putString(
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

    @SuppressLint("SetTextI18n")
    fun retrieveData() {
        when {
            edit_text_get_key.text.isNullOrEmpty() -> Toast.makeText(
                    this@MainActivity,
                    "Field cannot be empty",
                    Toast.LENGTH_SHORT
            ).show()
            else -> {
                val decryptedData =
                        SecureStorage.getString(
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

    @SuppressLint("SetTextI18n")
    fun removeData() {
        when {
            edit_text_remove_key.text.isNullOrEmpty() -> Toast.makeText(
                    this@MainActivity,
                    "Field cannot be empty",
                    Toast.LENGTH_SHORT
            ).show()
            else -> {
                SecureStorage.remove(this@MainActivity, edit_text_remove_key.text.toString())

                text_view_deleted_data.text = "Value for key: ${edit_text_remove_key.text} successfully deleted"

                edit_text_remove_key.text.clear()
                text_view_retrieved_data.text = ""
                text_view_stored_data.text = ""
            }
        }
    }
}