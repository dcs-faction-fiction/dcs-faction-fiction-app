#!/bin/bash -xe

export API_JWT_SECRET=secret
export API_DATABASE_URL=jdbc:postgresql://localhost:54321/postgres

export API_ENABLE_ENDPOINTS=true
export API_ENABLE_CONSUMERS=true
export API_ENABLE_SCHEDULED=true
export API_ENABLE_MIGRATION=true
export API_DATABASE_MAXPOOLSZE=2

docker-compose down --remove-orphans -v
yes|docker volume prune
docker-compose up -d

./mvnw clean package
java -jar target/dcs-faction-fiction-app-1.0-SNAPSHOT.jar