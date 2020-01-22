#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :securestoragelibrary:clean \033[0m"
./gradlew :securestoragelibrary:clean --stacktrace

echo -e "\033[0;32m ./gradlew :securestoragelibrary:install \033[0m"
./gradlew :securestoragelibrary:install --stacktrace

echo -e "\033[0;32m ./gradlew :securestoragelibrary:bintrayUpload \033[0m"
./gradlew :securestoragelibrary:bintrayUpload --stacktrace