# Starts the vault container for Ascent platform

docker-compose -f docker-compose.vault.yml \
	-f docker-compose.vault.override.yml \
	up --build -d
