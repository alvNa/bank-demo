# bank-demo

# Compile the project
mvn clean install

# Execute the tests
mvn test

# Start local db with Docker compose
docker-compose up -d

# Run the server in local
mvn spring-boot:run

# Execute the endpoints
In the doc folder there is a postman collection including the original endpoints in the Fabrick server and also the local endpoints in the project. 

# Stop local db with Docker compose
docker-compose down