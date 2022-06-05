FROM node:lts-slim AS build-cli
WORKDIR /build

COPY httpfs-cli/package*.json .
RUN npm install

COPY httpfs-cli .
RUN npm run build && \
    npm cache verify --force && \
    rm -rf node_modules


FROM openjdk:11-slim AS build-api
WORKDIR /build

COPY httpfs-api/.mvn .mvn
COPY httpfs-api/mvnw .
COPY httpfs-api/pom.xml .
RUN ./mvnw -B dependency:resolve -DincludeScope=compile

COPY ./httpfs-api/src src
COPY --from=build-cli /build/dist ./target/classes/public
RUN ./mvnw clean package -DskipTests


FROM openjdk:11-jre-slim

ARG UID=1001
ARG GID=1001

WORKDIR /home/app
RUN addgroup --system --gid ${GID} app && \
    adduser  --system --uid ${UID} --group app && \
    mkdir data && chown app:app data

VOLUME data

COPY --from=build-api /build/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
