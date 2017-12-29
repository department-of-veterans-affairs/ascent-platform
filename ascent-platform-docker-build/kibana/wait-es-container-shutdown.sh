#!/bin/bash 
stillup=0
while [[ $stillup -eq 0  ]]; do
    echo "--------es-config is still up. polling again."
    ping -c 1 es-config
    stillup=$?
    sleep 5
done
