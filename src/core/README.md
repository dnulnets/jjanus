# Janus-Core
This is the core image for the janus application.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw clean
./mvnw compile quarkus:dev
```

## Building docker image

```shell script
quarkus build -Dquarkus.container-image.build=true
```
