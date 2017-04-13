#!/usr/bin/env bash

echo -e "Start clean module"
./gradlew :securestorage:clean
echo -e "Finished clean"

echo -e "Start install module"
./gradlew :securestorage:install
echo -e "Finished install"

echo -e "Start bintrayUpload"
./gradlew :securestorage:bintrayUpload
echo -e "Finished bintrayUpload"