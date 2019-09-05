#!/usr/bin/env bash

function main {
    remove_dangling_images
    remove_baggage_handler_images
    remove_volumes
}

function remove_dangling_images {
    if [[ "$(docker images --filter dangling=true -q | wc -l)" != "0" ]]; then
        echo -e "\e[31m===>\e[37m Removing dangling images...\e[0m"
        docker rmi $(docker images --filter dangling=true -q)
    fi
}

function remove_baggage_handler_images {
    if [[ "$(docker images builders/baggage-handler -q | wc -l)" != "0" ]]; then
        echo -e "\e[31m===>\e[37m Removing Baggage Handler images...\e[0m"
        docker rmi -f $(docker images builders/baggage-handler -q)
    fi
}

function remove_volumes {
    if [[ "$(docker volume ls -q | wc -l)" != "0" ]]; then
        echo -e "\e[31m===>\e[37m Removing volumes...\e[0m"
        docker volume rm $(docker volume ls -q)
    fi
}

main