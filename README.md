![Header](https://i.ibb.co/C9mXvC0/To-DO-API.png)

<a href = https://openjdk.java.net/projects/jdk/16>
    <img src= https://img.shields.io/badge/Java-Open_JDK_16-informational?style=flat&logo=java&logoColor=white&color=49464d>
</a>
<a href = https://start.spring.io>
    <img src= https://img.shields.io/badge/Spring_Boot-informational?style=flat&logo=springboot&logoColor=white&color=49464d>
</a>
<a href = https://go.postman.co/workspace/To-Do-List-Service~b9b9da37-69c2-4a2c-be89-d3837dec6726/collection/13586779-3b9dc06f-cab0-4267-ad19-a38a0abc470e?action=share&creator=13586779>
    <img src= https://img.shields.io/badge/Postman-informational?style=flat&logo=postman&logoColor=white&color=49464d>
</a>
<a href = https://app.swaggerhub.com/apis-docs/ChrisHilborne/TO_DO_LIST/1.0>
    <img src= https://img.shields.io/badge/Swagger-informational?style=flat&logo=swagger&logoColor=white&color=49464d>
</a>


## Table of contents
* [General info](#general-info)
* [Word In Progress](#wip)
* [Technologies](#technologies)
* [Setup](#setup)
* [Using the API](#consuming-the-api)
* [Licence](#licence)


## General Info
This project is a simple To-Do REST API built with Spring Boot and Spring Security. 

A live version of the API is currently running for testing purposes. It can be found [here](https://to-do-api-test.herokuapp.com/).

## WIP 
This is a work in progress. The following features and improvements are in the process of being implemented:
- Cache local data
- Add HTTP `Cache-Control` headers to all HTTP responses
- Add ETags to responses to `GET` requests
- Log all incoming connections in separate `.log` file [(done)](https://github.com/ChrisHilborne/to-do-api/commit/7c45a80d2e6596bd027c66663fb3da59e557b23e)
- Cache session tokens in a local Redis cache
- Replace H2 in-memory database with SQLite [(done)](https://github.com/ChrisHilborne/to-do-api/commit/ec1cbd790d66ece1aaeecf75788e863f3ea69e2d)
- Log all incoming connections in separate `.log` file
- Cache session tokens in a local Redis cache
- Replace H2 in-memory database with SQLite [done](https://github.com/ChrisHilborne/to-do-api/commit/ec1cbd790d66ece1aaeecf75788e863f3ea69e2d)
- Implement authentication with OAuth2     

You can find more information in the [issues](https://github.com/ChrisHilborne/to-do-api/issues) section.

## Technologies
This project uses:
* Java - Open JDK 16
* Spring Boot
* Spring Security
* SpringFoX
* Hibernate Validator
* MapStruct

## Setup
To run the project yourself, navigate into the project folder and use the command:
```
$ mvn install 
``` 
If you do not have the [Maven](https://maven.apache.org/) build tool installed, you will need to install it. 

Once Maven has completed installing, run the program with:
```
$ java -jar to-do-api-0.0.1-SNAPSHOT.jar
```

## Using the API 
API documentation is available on [SwaggerHub](https://app.swaggerhub.com/apis-docs/ChrisHilborne/TO_DO_LIST/1.0) and as part of a publicly available [Postman Collection](https://chilborne.postman.co/workspace/To-Do-List-Service~b9b9da37-69c2-4a2c-be89-d3837dec6726/documentation/13586779-ba6129a8-72ad-4c57-988c-3d550c04ef70). You will find everything you need to use TO-DO-API here. 

## Licence
[Apache 2.0](https://github.com/ChrisHilborne/to-do-api/blob/main/LICENSE)
