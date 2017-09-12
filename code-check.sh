#!/usr/bin/env bash

echo -e "\033[0;32mStart normal check \033[0m"
./gradlew check
echo -e "\033[0;32m Finished normal check \033[0m"

echo -e "\033[0;32m Start checkstyle check \033[0m"
./gradlew checkstyle
echo -e "\033[0;32m Finished checkstyle check \033[0m"

echo -e "\033[0;32m Start pmd check \033[0m"
./gradlew pmd
echo -e "\033[0;32m Finished pmd check \033[0m"