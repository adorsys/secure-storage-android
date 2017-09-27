# Secure Device Storage - Android

## Storing Credentials Securely on Android Devices

[![Build Status](https://travis-ci.org/adorsys/secure-storage-android.svg?branch=develop)](https://travis-ci.org/adorsys/secure-storage-android)  
[![Download](https://api.bintray.com/packages/andev/adorsys/securestoragelibrary/images/download.svg) ](https://bintray.com/andev/adorsys/securestoragelibrary/_latestVersion) 
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Secure%20Storage%20Android-blue.svg?style=flat)](https://android-arsenal.com/details/1/5648)
[![API](https://img.shields.io/badge/API-18%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=18)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)



### Introduction

Storing Credentials securely on a Device is a must.
To make that possible we have combined the Android Keystore for generating Cryptographic keys, and storing them securely and using those keys we encrypt the credentials and save them in the SharedPreferences.

The cool thing about that is that those generated keys are never exposed to the kernel when the device is equipped with a “Trusted Execution Environment”. A so called TEE is a secure area inside the main processor of a smartphone which runs code isolated from other processes. That means even if the device gets compromised or hacked those keys can’t be easily extracted. Already 80–90% of all modern Android phones out there are equipped with a TEE (mostly because it’s often used to play DRM protected material) and it even is a requirement for Google’s Android Nougat certification — so every new phone running Android Nougat will come with a TEE installed.

We also use specific SharedPrefences only for the Secure Device Storage module, so as not to have conflicts with other possible SharedPreferences instances. This also means that the content of the SharedPreferences can only be read from the app where the module is implemented and not from other apps.

Even if the credentials where to be gotten somehow, they would be in an encrypted form which is nearly-impposible to decrypt without having the Cryptographic key used to encrypt it, which as was stated earlier is pretty secure itself sitting in the TEE.

### Supported API's

__Symmetric__ key generation and storage in the Android KeyStore is supported from __Android 6.0 (API Level 23) onwards.__
__Asymmetric__ key generation and storage in the Android KeyStore is supported from __Android 4.3 (API Level 18) onwards.__

To support more devices we have used the Assymetric key generation, which in the case of storing simple credentials is very secure and the potential lack of speed in contrast to symmetric key generation, is not noticeable.

### Usage

Add the module to your apps build.gradle:

```golang
compile 'de.adorsys.android:securestoragelibrary:1.0.0'
```

To store a string value in our __SecurePreferences__ you have to call:
```java
SecurePreferences.setValue("KEY", "PLAIN_MESSAGE");
```

This works for every other primitive data type. So for storing a boolean value:
```java
SecurePreferences.setValue("KEY", true/false);
```

for int
```java
SecurePreferences.setValue("KEY", 100);
```

for float and long
```java
SecurePreferences.setValue("KEY", 100.12345);
```

To retrieve a string value:
```java
SecurePreferences.getStringValue("KEY");
```

And respectively for the other types
```java
SecurePreferences.getBooleanValue("KEY");
```
```java
SecurePreferences.getIntValue("KEY");
```
```java
SecurePreferences.getFloatValue("KEY");
```
```java
SecurePreferences.getLongValue("KEY");
```

See if an entry exists in the SecurePreferences:
```java
SecurePreferences.contains("KEY");
```

You can also remove an entry from the SecurePreferences:
```java
SecurePreferences.removeValue("KEY");
```

Clearing the SecurePreferences and deleting the KeyPair:
```java
SecurePreferences.clearAllValues();
```

Everything about the cryptographic keys such as generating, maintaining and usage is handled internally by the module, so you do not need to worry about it.


### Error handling
The library throws for everything a SecureStorageException. Within the SecureStorageException you can find a exception type. You can handle the error which occurred with the help of this type as follows:

```kotlin
try {
    SecurePreferences.setValue(KEY, "Secret")
    // or
    val decryptedMessage = SecurePreferences.getStringValue(KEY, "")
} catch (e: SecureStorageException) {
    handleException(e)
}
//
private fun handleException(e: SecureStorageException) {
    Log.e(TAG, e.message)
    when (e.type) {
        KEYSTORE_NOT_SUPPORTED_EXCEPTION -> Toast.makeText(this, "Oh", Toast.LENGTH_LONG).show()
        KEYSTORE_EXCEPTION -> Toast.makeText(this, "Fatal - YARK", Toast.LENGTH_LONG).show()
        CRYPTO_EXCEPTION -> Toast.makeText(this, "2h&$==0j", Toast.LENGTH_LONG).show()
        INTERNAL_LIBRARY_EXCEPTION -> Toast.makeText(this, "Blame it all on us", Toast.LENGTH_LONG).show()
        else -> return
    }
}
```

### Contributors:
[@drilonreqica](https://github.com/drilonreqica)

[@itsmortoncornelius](https://github.com/itsmortoncornelius)

### Want to know more:

These links cover security aspect of the android keystore:  
https://developer.android.com/training/articles/keystore.html#SecurityFeatures  
https://source.android.com/security/keystore/  
https://codingquestion.blogspot.de/2016/09/how-to-use-android-keystore-api-with.html  
http://nelenkov.blogspot.de/2012/05/storing-application-secrets-in-androids.html  
http://nelenkov.blogspot.de/2015/06/keystore-redesign-in-android-m.html  
http://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/  

This link covers security aspect of the android storage:  
https://developer.android.com/guide/topics/data/data-storage.html  
http://stackoverflow.com/a/26077852/3392276  

### Screenshots:

Default Layout             |  After Encryption         |  Extra Options
:-------------------------:|:-------------------------:|:-------------------------:
![](https://github.com/adorsys/secure-storage-android/blob/master/screenshots/screenshot-1.jpg)  |  ![](https://github.com/adorsys/secure-storage-android/blob/master/screenshots/screenshot-2.jpg) |  ![](https://github.com/adorsys/secure-storage-android/blob/master/screenshots/screenshot-3.jpg)


