# Secure Device Storage - Android

## Storing Credentials Securely on Android Devices

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
compile project(':securedevicestorage')
```

To store a string value in our __SecurePreferences__ you have to call:
```java
SecurePreferences.setValue("KEY", "PLAIN_MESSAGE", Context);
```

This works for every other primitive data type. So for storing a boolean value:
```java
SecurePreferences.setValue("KEY", true/false, Context);
```

for int
```java
SecurePreferences.setValue("KEY", 100, Context);
```

for float and long
```java
SecurePreferences.setValue("KEY", 100.12345, Context);
```

To retrieve a string value:
```java
SecurePreferences.getStringValue("KEY", Context);
```

And respectively for the other types
```java
SecurePreferences.getBooleanValue("KEY", Context);
```
```java
SecurePreferences.getIntValue("KEY", Context);
```
```java
SecurePreferences.getFloatValue("KEY", Context);
```
```java
SecurePreferences.getLongValue("KEY", Context);
```

There also is a method for clearing the SecurePreferences and deleting the KeyPair.
To do that call:
```java
SecurePreferences.clearAllValues(Context);
```

Everything about the cryptographic keys such as generating, maintaining and usage is handled internally by the module, so you do not need to worry about it.

### Want to know more:

These links cover security aspect of the android keystore :
https://developer.android.com/training/articles/keystore.html#SecurityFeatures
https://codingquestion.blogspot.de/2016/09/how-to-use-android-keystore-api-with.html
http://nelenkov.blogspot.de/2012/05/storing-application-secrets-in-androids.html
http://nelenkov.blogspot.de/2015/06/keystore-redesign-in-android-m.html
http://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/

This link covers security aspect of the android storage :
https://developer.android.com/guide/topics/data/data-storage.html
http://stackoverflow.com/a/26077852/3392276

