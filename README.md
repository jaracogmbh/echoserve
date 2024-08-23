---
output: 
  html_document:
    includes:
      in_header: logo.html
---
# Custom WireMock Project: ReadMe
This project aims to create a custom WireMock server that can be used to mock RESTful services. The project is based on the WireMock project and is intended to be used as a standalone server. The project is meant to be configurable via a properties file.
## Master Branch

Currently, the master branch contains a very simple version of the Custom WireMock. 

### Features
#### Request Mapping
- GET, POST, PUT and DELETE mappings are supported.
- POST and PUT mappings require a request body.
- the mapping configuration is defined in the configuration file ```config.properties```
- the mapping configuration is read at the start of the server and needs to be of a certain format:
```
request1.requestType=GET
request1.statusCode=200
request1.url=/helloWorld
request1.contentType=application/json
request1.response= helloWorld.json
``` 
- every request need to start with the word "request" followed by a unique identifier and a period. In this example we used a number. 
- every request needs to have the following properties:
  - ``requestType``: the type of the request (GET, POST, PUT, DELETE)
  - ``statusCode``: the status code that should be returned
  - ``url``: the url that should be mapped
  - ``contentType``: the content type of the response
  - ``response``: the file that contains the response
  - ``requestBody``: (for POST and PUT requests) the request body
- for the ``url`` and the ``requestBody`` we use WireMock's ``urlMatching`` functionality. This means that we can use regular expressions to match the url and the request body:
```
request1.requestType=GET
request1.statusCode=200
request1.url=/getEmployee\\?([a-z]*)=([0-9]*)
request1.contentType=application/json
request1.response=employee.json
request2.requestType=POST
request2.statusCode=200
request2.requestBody=.*
request2.url=/addEmployee
request2.contentType=application/json
request2.response=addEmployee.json
```
- in the example above, the first request can be matched to the url ``/getEmployee?id=1`` and int the second request the request body can be anything but empty.
- the response files should be located in a folder called ``__files``. That folder needs to be in a directory that is defined in the configuration file. The default location is ``src/main/resources/``. The location can be changed in the configuration file (later more).

#### Non Request Configuration Properties
- the server can currently be configured with the following properties:
  - ``port``: the port that the server should run on
  - ``hostname``: the hostname that the server should run on
  - ``fileLocation``: the location that is used for the response files for the requests and the configuration file.
    - WireMock will look for the response files in a folder named ``__files``, which is per default located at ``src/test/resources/``
    - WireMock allows us to change the location of the ``__files`` folder
    - when we run the server from the IDE, we change the default location to ``src/main/resources/`` so that the response files are in the resources folder of the project, and we do not use this property.
    - when we use the docker compose configuration, we use this property to change the location of the ``__files`` folder to ``/data`` and copy the contents of the resources folder to the ``/data`` folder in the docker container.

### What should be defined

- the configuration file should be defined in the resources folder of the project and should be named ``config.properties``
- the response files should be defined in the resources folder of the project and should be named as defined in the configuration file
- the volume mount in the docker compose file should be defined. We mount a local directory to the ``/data`` directory in the docker container. The local directory should contain the response files and the configuration file.

### Running the Server

- the server can be run from the IDE or from docker compose
- when we run the ``main`` function it expects at least one Parameter in the args Array. That parameter is the ``mode`` the mode that should be used for the configuration:
  - ``local``: the server is run from the IDE
    -  when we run the server from the IDE, we use the default configuration (``config.properties``), which is located in the resources folder of the project
  - ``docker``: the server is run from docker compose
    - when we run the server from docker compose, we use the configuration file that is located in the ``/data`` folder in the docker container
    - also, we need to give a second parameter in the args array. This parameter is the name of the configuration file that we want to use. The configuration file should be located in the ``/data`` folder in the docker container.
- run command for the command line for local configuration: ``java -jar application.jar local``
- run command used in the dockerfile: ``java -jar application.jar docker config.properties``

### Docker Compose Example

In this section we will show an example of how to use Echoserve with docker compose.

#### Example Docker Compose File

```
services:
  customwiremocktest:
    image:   jaracogmbh/echoserve:1.0.0
    volumes:
      - /path/to/your/mappings:/data:ro
    ports:
      - "8089:8089"
```
- It is required to use a volume mount to mount the mappings to the ``/data`` directory in the docker container. The mappings should contain the response files and the configuration file. 
- The response files should be in a folder named ``__files`` in the mappings directory. This is required by WireMock.
- The configuration file should be named ``config.properties``.
---
<img alt="FirmenLogo" height="100" src="/images/jaraco_logo_software_engineer.png" width="300"/>