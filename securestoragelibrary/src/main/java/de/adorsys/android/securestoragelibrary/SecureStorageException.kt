/*
 * Copyright (C) 2019 adorsys GmbH & Co. KG
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

package de.adorsys.android.securestoragelibrary

@Suppress("unused")
/**
 *
 * Useful for catching possible exceptions thrown internally in the library so the user knows what to expect
 *
 * @param detailMessage Exception message that will be shown to developer when it is thrown
 * @param cause Cause of the throwable that has been caught (can be null)
 * @param type Type of exception that should be thrown
 * @see ExceptionType
 */
class SecureStorageException(detailMessage: String, cause: Throwable?, private val type: ExceptionType) :
    Exception(detailMessage, cause) {

    /**
     *
     * Used for throwing exceptions with a type that makes more sense to the developer based on the cause
     *
     * @property KEYSTORE_EXCEPTION If this exception type is defined you cannot use the keystore / this library on
     * the current device. This is fatal and most likely due to native key store issues.
     * @property CRYPTO_EXCEPTION If this exception type is defined a problem during encryption has occurred.
     * Most likely this is due to using an invalid key for encryption or decryption.
     * @property KEYSTORE_NOT_SUPPORTED_EXCEPTION If this exception type is set it means simply that the keystore
     * cannot be used on the current device as it is not supported by this library.
     * @property INTERNAL_LIBRARY_EXCEPTION If this exception type is set it means that something with
     * this library is wrong.
     */
    enum class ExceptionType {

        KEYSTORE_EXCEPTION,

        CRYPTO_EXCEPTION,

        KEYSTORE_NOT_SUPPORTED_EXCEPTION,

        INTERNAL_LIBRARY_EXCEPTION
    }

    companion object {
        const val MESSAGE_KEY_DOES_NOT_EXIST = "Key does not exist"
        const val MESSAGE_KEYPAIR_DOES_NOT_EXIST = "Keypair does not exist"
        const val MESSAGE_ERROR_WHILE_DELETING_KEY = "Error occurred while trying to delete key"
        const val MESSAGE_ERROR_WHILE_DELETING_KEYPAIR = "Error occurred while trying to delete keypair"
        const val MESSAGE_ERROR_WHILE_RETRIEVING_KEY = "Error while retrieving key"
        const val MESSAGE_ERROR_WHILE_RETRIEVING_KEYPAIR = "Error while retrieving keypair"
        const val MESSAGE_ERROR_WHILE_RETRIEVING_PRIVATE_KEY = "Error while retrieving private key"
        const val MESSAGE_ERROR_WHILE_RETRIEVING_PUBLIC_KEY = "Error while retrieving public key"
        const val MESSAGE_ERROR_WHILE_GETTING_KEYSTORE_INSTANCE = "Error while getting keystore instance"
    }
}