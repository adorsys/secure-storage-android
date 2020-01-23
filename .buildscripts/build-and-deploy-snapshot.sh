#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :securestoragelibrary:clean \033[0m"
./gradlew :securestoragelibrary:clean --info

echo -e "\033[0;32m ./gradlew :securestoragelibrary:install \033[0m"
./gradlew :securestoragelibrary:build --info

echo -e "\033[0;32m ./gradlew :securestoragelibrary:bintrayUpload \033[0m"
./gradlew :securestoragelibrary:bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_API_KEY -PdryRun=false --info