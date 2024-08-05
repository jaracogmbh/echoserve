# Custom WireMock Project: ReadMe
This project aims to create a custom WireMock server that can be used to mock RESTful services. The project is based on the WireMock project and is intended to be used as a standalone server. The project is meant to be configurable via a properties file.
## Master Branch

Currently, the master branch contains a very simple version of the Custom WireMock. 

### Features
#### Request Mapping
- currently it is only possible to define POST and two type of GET mappings:
  - POST mapping without a request body
  - GET mappings without parameters
  - GET mappings with one path parameters
- the mapping configuration is defined in the configuration file ```config.properties```
- the mapping configuration is read at the start of the server and needs to be of a certain format:
```
request1.requestType=GET
request1.statusCode=200
request1.url= /helloWorld
request1.contentType=application/json
request1.response= helloWorld.json
request1.withParameter=false
``` 
- every request need to start with the word "request" followed by a unique identifier and a period. In this example we used a number. 
- every request needs to have the following properties:
  - requestType: the type of the request (currently just GET or POST)
  - statusCode: the status code that should be returned
  - url: the url that should be mapped
  - contentType: the content type of the response
  - response: the file that contains the response
  - withParameter: a boolean that indicates if the request has a parameter
  - if the request has a parameter, the parameter needs to be defined in the configuration file as well:
```
request2.requestType=GET
request2.statusCode=200
request2.url= /getWorld
request2.paramName=id
request2.param=1
request2.contentType=application/json
request2.response= getWorld.json
request2.withParameter=true
```
- the parameter needs to be defined with the following properties:
    - paramName: the name of the parameter
    - param: the value of the parameter
- the response file needs to be in the resources folder of the project, and it needs to match the name that is defined in the configuration file and content type.
- POST mapping example:
```
request3.requestType=POST
request2.statusCode=200
request3.url= /newWorld
request3.contentType=application/json
request3.response= {"response": "New World"}
```

#### Non Request Configuration Properties
- the server can currently be configured with the following properties:
  - ``port``: the port that the server should run on
  - ``hostname``: the hostname that the server should run on
  - ``mode``: the mode that should be used for the configuration
    - we use this property to distinguish between the different types of configurations that can be used for running the server from the IDE and from docker compose.
      - ``mode=docker``: configuration used for the docker compose container
      - ``mode=local``: configuration used for running the server from the IDE
  - ``fileLocation``: the location that is used for the response files for the requests
    - WireMock will look for the response files in a folder named ``__files``, which is per default located at ``src/test/resources/``
    - WireMock allows us to change the location of the ``__files`` folder
    - when we run the server from the IDE, we change the default location to ``src/main/resources/`` so that the response files are in the resources folder of the project, and we do not use this property.
    - when we use the docker compose configuration, we use this property to change the location of the ``__files`` folder to ``/data`` and copy the contents of the resources folder to the ``/data`` folder in the docker container.

### What should be defined

- the configuration file should be defined in the resources folder of the project and should be named ``config.properties``
- the response files should be defined in the resources folder of the project and should be named as defined in the configuration file

### Running the Server

- the server can be run from the IDE or from docker compose
- when running the server from the IDE, the ``mode`` should be set to ``local`` in the configuration file
- when running the server from docker compose, the ``mode`` should be set to ``docker`` in the configuration file