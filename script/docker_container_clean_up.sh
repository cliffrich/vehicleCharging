#!/usr/bin/env bash

function main {
    stop_containers
    remove_containers
}

function stop_containers {
    if [[ "$(docker ps -aq | wc -l)" != "0" ]]; then
        echo -e "\e[31m===>\e[37m Stopping containers...\e[0m"
        docker stop $(docker ps -aq)
    fi
}

function remove_containers {
    if [[ "$(docker ps -aq | wc -l)" != "0" ]]; then
        echo -e "\e[31m===>\e[37m Removing stopped containers...\e[0m"
        docker rm $(docker ps -aq)
    fi
}

main