#!/bin/bash

set +e

echo -e "\033[0;32m ./gradlew app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=de.adorsys.android.securestoragetest.SecureStorageLogicTest --stacktrace \033[0m"
./gradlew app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=de.adorsys.android.securestoragetest.SecureStorageLogicTest --stacktrace