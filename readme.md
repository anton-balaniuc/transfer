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

Unit test has the following format: 1_2_3() throws Exception, where
1 - is method to be tested
2 - method condition
3 - expected result.
For example,
create_emailExists_AccountAlreadyExistsException
will test `create` method with account whose email already exists and expects `AccountAlreadyExistsException` to be thrown.
