#!/usr/bin/env bash

# script/docker: Run latest application as a docker container
# Option to pass in extra variable "logs" to print docker logs to terminal (e.g. /script/docker logs)

set -e

[[ -z "$DEBUG" ]] || set -x

cd "$(dirname "$0")/.."

function main {
    echo -e "\e[31m===>\e[37m Building application and docker image...\e[0m"
    initialise_directories
    build_jar
    build_docker_image
    build_other_docker_image
    run_application_via_docker
    show_application_logs_if_requested "$1"
}

function initialise_directories {
    echo -e "\e[31m===>\e[37m Check directories exist...\e[0m"
    make_directory_if_required "/tmp/inbox/.camel"
    make_directory_if_required "/tmp/outbox"

    echo -e "\e[31m===>\e[37m Set permissions...\e[0m"
    chmod -R o+rw /tmp/inbox
    chmod -R o+rw /tmp/outbox
}

function make_directory_if_required {
    if [[ ! -d "$1" ]]; then
        echo -e "\e[31m===>\e[37m Create directory $1\e[0m"
        mkdir -p "$1"
    fi
}

function build_jar {
  ./gradlew clean build wireMockJar
}

function build_docker_image {
    echo -e "\e[31m===>\e[37m Building docker image for application...\e[0m"
    VERSION="$(cat VERSION)"
    docker build . -t builders/vehicle-charging:latest -t builders/vehicle-charging:"$VERSION" --build-arg JAR_FILE="./build/libs/baggage-handler-*.jar"
}

function build_other_docker_image {
    echo -e "\e[31m===>\e[37m Building docker image for gateway...\e[0m"
    docker build . \
        -f src/test/resources/Dockerfile \
        -t builders/gateway:latest \
        --build-arg JAR_FILE="./build/libs/wiremock-*.jar"
}

function run_application_via_docker {
    echo -e "\e[31m===>\e[37m Running application via docker\e[0m"
    docker-compose up -d
}

function show_application_logs_if_requested {
    if [[ "$1" == "logs" ]]; then
        echo -e "\e[31m===>\e[37m Showing logs for docker container\e[0m"
        docker-compose logs -f baggage-handler
    fi
}

main "$1"
