# Stops the vault container

docker-compose -f docker-compose.vault.yml \
	-f docker-compose.vault.override.yml \
	down -v
