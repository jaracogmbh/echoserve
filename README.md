<img src="images/logo.png" alt="Custom WireMock Project Logo" width="128">

# Custom WireMock Project

## Overview

This project creates a custom WireMock server for mocking RESTful services. It’s designed to run as a standalone service and can be configured via a simple properties file. Ideal for testing and developing against mock APIs.

> **Note:** Currently, the master branch contains a very simple version of the Custom WireMock.

## Features

- **Request Mapping**: Supports GET, POST, PUT, DELETE requests with customizable response mappings.
- **Configurable**: Define all behavior via the `config.properties` file.
- **Flexible URL Matching**: Use regex to match URLs and request bodies.
- **Docker Support**: Run seamlessly in Docker with minimal setup.

## Prerequisites

- **Docker**: Required for running the server in a containerized environment.
- **Java**: Required if running locally from an IDE.
- **Configuration Files**: Ensure the `config.properties` and response files are correctly placed.

## Setup Instructions

### Local Setup (IDE)

1. Place `config.properties` and `__files` in `src/main/resources/`.
2. Run the `main` function with the argument `local`:

   ```bash
   java -jar application.jar local
   ```

### Docker Setup

1. **Directory Structure**: Organize your project as follows:

   ```plaintext
   ├── docker-compose.yml
   └── resources
       ├── __files
       │   ├── addEmployee.json
       │   ├── deleteEmployee.json
       │   ├── helloWorld.json
       │   └── updateEmployee.json
       └── config.properties
   ```

2. **Docker Compose File**: Create a `docker-compose.yml` file:

   ```yaml
   version: "3.8"

   services:
     echoserve:
       image: docker.io/jaracogmbh/echoserve:1.0.0
       platform: linux/amd64
       volumes:
         - ./resources:/data:ro
       ports:
         - "8089:8089"
   ```

3. **Running the Server**: From the directory containing `docker-compose.yml`, run:

   ```bash
   docker-compose up
   ```

## Configuration

### Request Mapping Configuration

Each request configuration in `config.properties` must follow a specific format:

```properties
request1.requestType=GET
request1.statusCode=200
request1.url=/helloWorld
request1.contentType=application/json
request1.response=helloWorld.json
```

- **Request Configuration**:
    - **Prefix**: Each configuration starts with `request` followed by a unique identifier.
    - **Properties**:
        - `requestType`: The HTTP method (GET, POST, PUT, DELETE).
        - `statusCode`: The HTTP status code to return.
        - `url`: The URL path to map.
        - `contentType`: The response content type.
        - `response`: The file containing the response body.
        - `requestBody`: (For POST and PUT) The expected request body.

### URL and Request Body Matching

Utilize WireMock's `urlMatching` functionality for regex-based matching:

```properties
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

- **Examples**:
    - The first request matches the URL `/getEmployee?id=1`.
    - The second request matches any non-empty POST body.

### Response Files

- Response files should be placed in a `__files` directory.
- By default, this directory is located at `src/main/resources/`.
- The location can be customized in the configuration file.

### Non-Request Configuration Properties

The server's behavior can be configured via `config.properties` with the following properties:

- **port**: The port on which the server runs.
- **hostname**: The hostname for the server.
- **fileLocation**: The location of the response files and configuration file.
    - The `__files` directory is located at `src/test/resources/` by default.
    - When running the server from an IDE, the default location is `src/main/resources/`.
    - When using Docker Compose, set this to `/data` inside the container.


## Dynamic Configuration via API

This feature lets you dynamically configure mock endpoints without restarting the server or changing any config files.
You can now add or update mock endpoints by sending a POST request to `<host>:19991/configure`.

### Basic information
1. Once a request is sent to `/configure`, the **mock is available immediately** without needing to restart the server.
2. The `/configure` endpoint listens on port `19991` by default.
3. You can use regex for both `url` and `requestBody` fields. Set `isRequestBodyRegex` to `"true"` if you’re using regex in the body.
4. When sending JSON in `responseBody`, remember to escape double quotes (`\"`).

### How to add or update endpoint

To configure a mock endpoint, send a `POST` request to the `<host>:19991/configure` endpoint with a JSON payload that defines the behavior.

#### Example Request

```bash
curl -X POST "http://localhost:19991/configure" \
     -H "Content-Type: application/json" \
     -d '{
           "requestType": "POST",
           "url": "/get-endpoint4",
           "hostname": "localhost",
           "responseBody": "{\"message\": \"POST request successful\"}",
           "contentType": "application/json",
           "statusCode": 200,
           "requestBody": ".*",
           "isRequestBodyRegex": "true"
         }'
```

#### JSON Payload Breakdown

| Field                | Type    | Required | Description                                                            |
|----------------------|---------|----------|------------------------------------------------------------------------|
| `requestType`        | String  | Yes      | The HTTP method for the mock (`GET`, `POST`, `PUT`, `DELETE`).         |
| `url`                | String  | Yes      | The endpoint path to mock (regex patterns are supported).              |
| `hostname`           | String  | Yes      | The hostname where the mock server is running.                         |
| `responseBody`       | String  | Yes      | The response body to return (escape double quotes for JSON).           |
| `contentType`        | String  | Yes      | The `Content-Type` header for the response (e.g., `application/json`). |
| `statusCode`         | Integer | Yes      | The HTTP status code to return (e.g., `200` for OK).                   |
| `requestBody`        | String  | No       | The expected request body (use regex patterns if needed).              |
| `isRequestBodyRegex` | Boolean | No       | Set to `"true"` if `requestBody` is a regex; otherwise, `"false"`.     |

### HTTP Method-Specific Configurations

Depending on the `requestType`, certain fields are required or optional. Here’s a quick guide:

| HTTP Method | Required Fields                                                                                     |
|-------------|-----------------------------------------------------------------------------------------------------|
| GET         | `url`, `hostname`, `responseBody`, `contentType`, `statusCode`                                      |
| POST        | `url`, `hostname`, `responseBody`, `contentType`, `statusCode`, `requestBody`, `isRequestBodyRegex` |
| PUT         | `url`, `hostname`, `responseBody`, `contentType`, `statusCode`, `requestBody`, `isRequestBodyRegex` |
| DELETE      | `url`, `hostname`, `responseBody`, `contentType`, `statusCode`                                      |

### Example Requests

#### Configuring a GET Endpoint

```bash
curl -X POST "http://localhost:19991/configure" \
     -H "Content-Type: application/json" \
     -d '{
           "requestType": "GET",
           "url": "/helloWorld",
           "hostname": "localhost",
           "responseBody": "{\"message\": \"Hello World\"}",
           "contentType": "application/json",
           "statusCode": 200
         }'
```

#### Configuring a POST Endpoint with Any Body

```bash
curl -X POST "http://localhost:19991/configure" \
     -H "Content-Type: application/json" \
     -d '{
           "requestType": "POST",
           "url": "/addEmployee",
           "hostname": "localhost",
           "responseBody": "{\"message\": \"Employee added successfully\"}",
           "contentType": "application/json",
           "statusCode": 201,
           "requestBody": ".*",
           "isRequestBodyRegex": "true"
         }'
```

#### Configuring a PUT Endpoint with Specific Body

```bash
curl -X POST "http://localhost:19991/configure" \
     -H "Content-Type: application/json" \
     -d '{
           "requestType": "PUT",
           "url": "/updateEmployee",
           "hostname": "localhost",
           "responseBody": "{\"message\": \"Employee updated successfully\"}",
           "contentType": "application/json",
           "statusCode": 200,
           "requestBody": "{\"id\": \"123\", \"name\": \"John Doe\"}",
           "isRequestBodyRegex": "false"
         }'
```

#### Configuring a DELETE Endpoint

```bash
curl -X POST "http://localhost:19991/configure" \
     -H "Content-Type: application/json" \
     -d '{
           "requestType": "DELETE",
           "url": "/deleteEmployee",
           "hostname": "localhost",
           "responseBody": "{\"message\": \"Employee deleted successfully\"}",
           "contentType": "application/json",
           "statusCode": 200
         }'
```

> **Note**: Because the `/configure` endpoint can alter server behavior, make sure it's secured in production. You might want to restrict access to this endpoint using firewall rules or authentication methods.

## Running the Server

- **Local**: Run the server with the command:

  ```bash
  java -jar application.jar local
  ```

- **Docker**: Run the server using Docker Compose:

  ```bash
  docker-compose up
  ```

## Example Configuration and Response Files

This example demonstrates how to set up your configuration file and response file for the Custom WireMock Project.

#### `config.properties`

Place this file in the `/resources` directory. It defines the configuration for a GET request to the `/helloWorld` endpoint, returning a JSON response.

```properties
fileLocation=/data
hostname=localhost
port=8089
request1.requestType=GET
request1.statusCode=200
request1.url=/helloWorld
request1.contentType=application/json
request1.response=helloWorld.json
```

#### `helloWorld.json`

This file contains the JSON response for the configured endpoint. It should be placed in the `/resources/__files` directory.

```json
{
  "message": "Hello World"
}
```

### File Locations

Ensure the following file structure is in place:

```
.
└── resources
    ├── config.properties
    └── __files
        └── helloWorld.json
```

- `config.properties` should be located in the `/resources` directory.
- `helloWorld.json` should be located in the `/resources/__files` directory.

### Response

```json
{
  "message": "Hello World"
}
```
## Troubleshooting

### Common Issues

- **No Config Directory Given**: Ensure the `config.properties` file is correctly placed and accessible.
- **File Permissions**: Verify that the files have the correct permissions for the Docker container.

<div><img src="images/jaraco_logo_software_engineer.png" width="200px" align="right"></div>
