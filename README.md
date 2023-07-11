# BTS CASA

Bts casa API

## Requirements
For building and running the application you need:

- JDK 1.8
- Maven 3

## Build Action


## How to Run
Build the project using maven clean and install
```sh
mvn clean install
```
Run the application without Tomcat embedded 
```sh
java -jar -Dspring.profiles.active=dev artifact-file-name.jar 
```

## Documentation
### Swagger:
SIT:
https://172.30.2.107:8443/casa/swagger-ui.html

UAT:
https://172.30.2.10:8443/casa/swagger-ui.html

### Postman Collection: