FROM frolvlad/alpine-oraclejdk8
ADD storageWebService-0.0.1-SNAPSHOT.jar storageService.jar
EXPOSE 8080
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/storageService.jar"]