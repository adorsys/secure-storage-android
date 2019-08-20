#!/usr/bin/env bash

echo -e "\033[0;32m ./gradlew :securestoragelibrary:clean \033[0m"
./gradlew :securestoragelibrary:clean --stacktrace

echo -e "\033[0;32m ./gradlew :securestoragelibrary:install \033[0m"
./gradlew :securestoragelibrary:install --stacktrace

if [ "$CI" == true ] && [ "$TRAVIS_PULL_REQUEST" == false ] && [ "$TRAVIS_BRANCH" == "master" ]; then

    echo -e "\033[0;32m ./gradlew :securestoragelibrary:bintrayUpload \033[0m"
    ./gradlew :securestoragelibrary:bintrayUpload --stacktrace

else
   echo -e "\033[0;32m Current branch is not master, will not upload to bintray. \033[0m"
fi