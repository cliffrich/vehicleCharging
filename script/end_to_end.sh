#!/usr/bin/env bash

# script/end-to-end-test: Start running docker container of application and run end to end tests

set -e

[[ -z "$DEBUG" ]] || set -x

cd "$(dirname "$0")/.."

function main {
    script/docker
    script/wait-for-application-to-be-ready

    run_end_to_end_tests
    docker logs baggage-handler
    stop_docker_container
    clear_outbox
}

function run_end_to_end_tests {
    echo -e "\e[31m===>\e[37m Running end to end tests...\e[0m"
    trap stop_docker_container INT TERM EXIT
    ./gradlew endToEndTest
    trap - INT TERM EXIT
}

function stop_docker_container {
    echo -e "\e[31m===>\e[37m Stopping application...\e[0m"
    docker-compose down
}

function clear_outbox {
    rm -f /tmp/outbox/*
}

main