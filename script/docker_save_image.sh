#!/usr/bin/env bash

# script/save-docker-image: Save the docker image as a tar

set -e

[ -z "$DEBUG" ] || set -x

cd "$(dirname "$0")/.."

function main {
    echo -e "\e[31m===>\e[37m Saving docker image saved for later exporting\e[0m"
    docker image save builders/baggage-handler:latest -o build/libs/baggage-handler.tar
    echo -e "\e[31m===>\e[37m Docker image saved\e[0m"
}

main