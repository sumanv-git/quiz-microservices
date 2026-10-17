# creates jar files for each module
mvn clean package -DskipTests

#creates Docker images for each module
docker-compose build

#deploys the Docker containers
docker-compose up -d


# View logs of all services
docker-compose logs

# View logs of specific service
docker-compose logs question-service

# View logs in real-time (follow)
docker-compose logs -f

# View last 50 lines of question-service
docker-compose logs -f --tail=50 question-service

# View logs with timestamps
docker-compose logs --timestamps