#!/usr/bin/env bash

# script/server: Launch application locally without any dependencies

set -e

[ -z "$DEBUG" ] || set -x

cd "$(dirname "$0")/.."

function main {
    create_folders
    run_support_containers
    run_application
}

function create_folders {
    if [[ ! -d "/tmp/inbox" ]]; then
        echo -e "\e[31m===>\e[37m Creating inbox...\e[0m"
        mkdir /tmp/inbox
    fi
    if [[ ! -d "/tmp/outbox" ]]; then
        echo -e "\e[31m===>\e[37m Creating outbox...\e[0m"
        mkdir /tmp/outbox
    fi
}

function run_support_containers {
    echo -e "\e[31m===>\e[37m Start running support containers...\e[0m"
    docker-compose -f docker-compose-local.yml up -d
}

function run_application {
    echo -e "\e[31m===>\e[37m Running application...\e[0m"
    ./gradlew bootRun --args="--spring.profiles.active=local"
}

main