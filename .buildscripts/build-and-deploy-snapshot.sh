#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :securestoragelibrary:clean \033[0m"
./gradlew :securestoragelibrary:clean --stacktrace

echo -e "\033[0;32m ./gradlew :securestoragelibrary:install \033[0m"
./gradlew :securestoragelibrary:build --stacktrace

echo -e "\033[0;32m ./gradlew :securestoragelibrary:bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_API_KEY -PdryRun=false \033[0m"
./gradlew :securestoragelibrary:bintrayUpload -PbintrayUser="$BINTRAY_USERNAME" -PbintrayKey="$BINTRAY_API_KEY" -PdryRun=false --stacktrace