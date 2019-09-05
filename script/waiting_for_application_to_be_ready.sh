#!/usr/bin/env bash

# script/wait-for-application-to-be-ready:  Wait for health end point to be available.

set -e

[ -z "$DEBUG" ] || set -x

cd "$(dirname "$0")/.."

function main {
    wait_for_application
}

function wait_for_application {
    echo -e "\e[31m===>\e[37m Waiting for application health endpoint...\e[0m"
    while [[ "$(poll_application)" != "200" ]]; do
        echo "."
        sleep 5
    done
}

function poll_application {
    curl -s -o /dev/null -w "%{http_code}" localhost:8080/health
}

main