#!/bin/bash
# Starts the entire Ascent platform, including all log aggregation services

BASE_DIR="../docker-compose"

echo "Building local vault image first..."
docker build -t vault-local ../containers/vault/


docker-compose -f $BASE_DIR/docker-compose.vault.yml \
	-f $BASE_DIR/docker-compose.vault.override.yml \
	-f $BASE_DIR/docker-compose.logging.yml \
	-f $BASE_DIR/docker-compose.logging.override.yml \
	pull

docker-compose -f $BASE_DIR/docker-compose.vault.yml \
	-f $BASE_DIR/docker-compose.vault.override.yml \
	-f $BASE_DIR/docker-compose.logging.yml \
	-f $BASE_DIR/docker-compose.logging.override.yml \
	up -d
