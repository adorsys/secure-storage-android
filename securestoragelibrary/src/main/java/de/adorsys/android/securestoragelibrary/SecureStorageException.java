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

package de.adorsys.android.securestoragelibrary;

public class SecureStorageException extends Exception {
    public final ExceptionType type;
    public SecureStorageException(String detailMessage, Throwable cause, ExceptionType type) {
        super(detailMessage, cause);
        this.type = type;
    }

    public enum ExceptionType {
        /**
         * If this exception type is defined you cannot use the keystore / this library on the current device.
         * This is fatal and most likely due to native key store issues.
         */
        KEYSTORE_EXCEPTION,
        /**
         * If this exception type is defined a problem during encryption has occurred.
         * Most likely this is due to using an invalid key for encryption or decryption.
         */
        CRYPTO_EXCEPTION,
        /**
         * If this exception type is set it means simply that the keystore cannot be used on the current device as it is not supported by this library.
         * This probably means that you are targeting a device prior to api 23 or any not supported fingerprint sensors of Samsung
         */
        KEYSTORE_NOT_SUPPORTED_EXCEPTION,
        /**
         * If this exception type is set it means that something with this library is wrong.
         */
        INTERNAL_LIBRARY_EXCEPTION
    }
}