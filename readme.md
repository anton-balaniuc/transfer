# Transfer restful service

## Introduction

This application uses the following:

* [Microprofile](https://microprofile.io/) as microservice framework 
* [Apache Derby](https://db.apache.org/derby/) as in-memory DB
* [Open Liberty](https://openliberty.io/) as web-application server 
* [Junit 5](https://junit.org/junit5/) as testing framework 
* [Mockito](https://site.mockito.org/) as mocking framework 

The application can be started by executing the following command

    mvn clean install liberty:start-server

To launch the openapi ui  page, open your browser at the following URL

    http://localhost:8181/openapi/ui
     
or for the text representation. 

    http://localhost:8181/openapi


