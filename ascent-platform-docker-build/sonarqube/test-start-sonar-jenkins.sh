#!/bin/bash

# Should have vault running already.
# Start vault with:
#     cd ..
#     docker-compose -f docker-compose.vault.yml -f docker-compose.vault-override.yml up --build

docker-compose -f docker-compose.jenkins-sonar.yml \
	up --build -d 
