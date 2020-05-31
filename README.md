# Planning poker
Simple implementation using Java and Spring Boot

### Compile and test

```
./gradlew build
```

### Create docker image

```
./gradlew dockerBuild
```

### Run docker container

Should only be executed after image was already built. The container will be started with `--rm` mode
so it will de automatically deleted when `docker stop` is called on it.

Name of the container will be `poker` and port `8080` will be used.

```
./gradlew dockerRun
```

To stop the container:

```
./gradlew dockerStop
```
