#!/bin/bash
echo -e "Start clean module"
./gradlew clean
echo -e "Finished clean"

echo -e "Start install module"
./gradlew install
echo -e "Finished install"

