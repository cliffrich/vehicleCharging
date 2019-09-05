#!/usr/bin/env bash

# script/unit-test: Run only the unit tests for the application

set -e

[ -z "$DEBUG" ] || set -x

cd "$(dirname "$0")/.."

function main {
    echo -e "\e[31m===>\e[37m Running unit tests...\e[0m"
    ./gradlew test
    echo -e "\e[31m===>\e[37m Unit test reports at \e[94mbuild/test-results/test\e[0m"
}

main