# FROM tells Docker that this image is based on the OpenJDK 11 Alpine
# base image. This means we’ll be using Alpine Linux, which is
# lightweight and fast. It’s bundled up with a Java installation so we
# don’t have to worry about installing it separately.
FROM eclipse-temurin:17-jre

# Create a non-root group and user
# RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# RUN addgroup -S tsys && adduser -S dhaval -G tsys
RUN addgroup tsys
RUN adduser dhaval --ingroup tsys

# Tell docker that all future commands should run as the appuser user
# USER appuser
USER dhaval:tsys

# No default value set, pass one from docker build or gradle build
ARG BUILD_VERSION

# Set default value or pass one from gradle build
ARG JAR_FILE=build/libs/*.jar
#ARG JAR_FILE=build/libs/fraud_checker-${BUILD_VERSION}.jar
# COPY will copy the application jar file into the image
COPY ${JAR_FILE} fraud-checker-service.jar

# It's important that the -D parameters are before your app.jar
# otherwise they are not recognized.
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=development", "/fraud-checker-service.jar"]

# BUILDING
# To build this image use, in the directory where Dockerfile is present
# $> docker build -t dhavaldalal/fraud-checker-service:1.0.0 --build-arg BUILD_VERSION=1.0.0 .

# RUNNING
# To run this image use
# We need --expose=8000 as EXPOSE is not a part of this Dockerfile
# docker run --expose=CONTAINER_PORT -p CONTAINER_PORT:HOST_PORT dhavaldalal/fraud-checker-service:1.0.0
# $> docker run --name fraud-checker-service --expose=8000 -p 8000:9001 dhavaldalal/fraud-checker-service:1.0.0
# OR simply without --expose option (target url will be --> http://localhost:8000)
# $> docker run --name fraud-checker-service -p 8000:9001 dhavaldalal/fraud-checker-service:1.0.0
# OR simply without --expose option (target url will be --> http://localhost:9001)
# $> docker run --name fraud-checker-service -p 9001:9001 dhavaldalal/fraud-checker-service:1.0.0

# INSPECTING IMAGE/CONTAINERS
# To get inside the image for inspecting, use
# $> docker run -it --rm --entrypoint /bin/bash dhavaldalal/fraud-checker-service:1.0.0
# To get inside the running container for inspecting, use
# $> docker exec it <container_id> bash
# You may find out the container_id using
# $> docker ps

# REMOVING
# To remove the image
# $> docker rmi dhavaldalal/fraud-checker-service:1.0.0
# To remove the named container
# $> docker rm fraud-checker-service

# PUSHING IMAGE TO DOCKER HUB
# To push images
# $> docker login
# $> docker push dhavaldalal/fraud-checker-service:1.0.0
