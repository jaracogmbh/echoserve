# Example usage of echoserve with SUT and Playwright Tests

This example demonstrates how to use `echoserve`, a customizable mock service based on WireMock, to simulate authentication scenarios for a Service Under Test (SUT). 
We will run the SUT alongside echoserve and use Playwright tests to verify the SUT’s behavior under different authentication configurations.

## Overview

This example showcases how echoserve can mock external services—in this case, an authentication service—and dynamically change its behavior at runtime. The SUT interacts with echoserve for authentication, and Playwright tests validate the SUT’s responses under different authentication scenarios.

### Directory Structure
```
/example
├── config                      <-- Configuration for echoserve
│   ├── __files
│   │   └── authenticate.json   <-- Initial authentication response body
│   └── config.properties       <-- Initial echoserve configuration
├── sut                         <-- Service Under Test using echoserve for authentication
│   ├── Dockerfile
│   ├── package.json
│   └── server.js               <-- Main application logic
└── tests
└── playwright
├── package.json
├── playwright.config.ts
└── tests
└── auth.test.ts    <-- Playwright tests verifying SUT behavior with echoserve
```

### Prerequisites
 - Docker and Docker Compose installed on your machine.

## Setup and Execution
1. Clone the Repository
```bash
git clone https://github.com/jaracogmbh/echoserve.git
cd echoserve
```

2. Start the Services
```bash
docker-compose -f docker-compose-example.yml up --build
```

## What Happens:
1. `echoserve`:
    - Starts on ports `8081` (mock service) and `19991` (configuration API).
    - Initially configured to return successful authentication responses.

2. `SUT`:
    - Starts on port `3000`.
    - Contains endpoints that require authentication, which it verifies by calling echoserve.

3. `Playwright`:
    - Begin execution after echoserve and SUT are up.
    - Perform tests under different authentication scenarios.

3. Monitor the Execution:
   As the services start, you can observe the logs in your terminal. The Playwright tests will execute automatically and display their progress.

### Sample Output:

```
sut-1        | Service Under Test running on port 3000
playwright   | Running 4 tests using 1 worker
playwright   |
playwright   |   ✓ Add Employee - Expect Success (200)
playwright   |   ✓ Edit Employee - Expect Success (200)
playwright   |   ✓ Delete Something - Expect Success (200)
playwright   | === Resetting echoserve ===
echoserve-1  | INFO: Resetting WireMock server
playwright   | === Configuring echoserve to return 401 Unauthorized ===
echoserve-1  | INFO: Configuring POST stub for /authenticate to return 401
playwright   |   ✓ Add Employee - Expect Unauthorized (401)
playwright   |
playwright   |   4 passed (1.1s)
```

### Analyze the Results:
   
The tests perform the following actions:

- Initial Authentication and operations:
    - The SUT logs in using `/login`, receiving a successful authentication token from echoserve.
    - Performs operations (`/addEmployee`, `/editEmployee`, `/deleteSomething`) which require authentication.
    - All operations succeed because echoserve returns a `200 OK` response for authentication checks.

- Reconfiguring echoserve:
    - Playwright resets echoserve using its `/reset` endpoint on port 19991.
    - Reconfigures echoserve to return a `401 Unauthorized` response for authentication requests.

- Testing Unauthorized Access:
    - The SUT attempts to perform the `/addEmployee` operation again.
    - The operation fails with a `401 Unauthorized` error because echoserve now denies authentication.

## Under the Hood

### `echoserve` configuration

#### Initial Configuration:

`echoserve` is set up to respond with a `200 OK` for `POST` requests to `/authenticate`, returning a success message and token. This simulates a successful authentication service.

#### Dynamic Reconfiguration:
During the tests, echoserve is reconfigured via its configuration API (on port `19991`) to simulate different authentication responses without restarting the service. This demonstrates how echoserve can dynamically change its behavior at runtime.

### Service Under Test (SUT)

#### Functionality:
The SUT is a simple Node.js application with endpoints that require authentication. It uses an authentication middleware that verifies tokens by calling echoserve’s /authenticate endpoint.

#### Protected Endpoints:
 - `/addEmployee`
 - `/editEmployee`
 - `/deleteSomething`

These endpoints simulate operations that should only be accessible to authenticated users.

### Playwright Tests

#### Purpose:
The tests simulate user interactions with the SUT and validate its behavior under different authentication scenarios by controlling echoserve’s responses.

#### Flow:
1. **Successful Authentication:**
    - Log in to obtain an authentication token from echoserve.
    - Access protected endpoints successfully, verifying that the SUT allows operations when authentication is successful.

2. **Reset and Reconfigure echoserve:**
    - Reset echoserve to clear previous configurations.
    - Reconfigure it to return a 401 Unauthorized response for authentication requests, simulating an authentication failure.

3. **Failed Authentication:**
    - Attempt to access protected endpoints with the same token.
    - Expect operations to fail with a 401 Unauthorized error, verifying that the SUT correctly handles authentication failures.

## Purpose and Opportunities

This example illustrates how **echoserve** can:

- **Simulate external services:**  
  Allow developers to mock external APIs and services, enabling consistent and controlled testing environments without relying on live services.

- **Dynamically change behavior:**  
  echoserve can be reconfigured on-the-fly to return different responses, which is valuable for testing various scenarios without redeploying or restarting services.

- **Facilitate robust testing:**  
  By controlling external dependencies, developers can focus on testing the application’s logic and error handling more effectively, including edge cases and failure modes.

### Potential Use Cases:

- **API development:**  
  Mock backend services during frontend development to enable independent development and testing.

- **Microservices testing:**  
  Test interactions between services without deploying all dependencies, allowing for isolated and controlled testing environments.

- **Error handling verification:**  
  Ensure the application behaves correctly under failure conditions of external services, improving the resilience and reliability of the application.


### Additional presets

Preset configurations to mock common REST APIs using WireMock are available under `presets` directory. 

Each preset is organized by service, containing:
 - `config.properties` which defines requests (type, URL, status, etc.).
 - `__files/` dir with JSON response files for endpoints.

#### Included examples
 1. GitHub – Get repo details, create issues, list/close issues.
 2. Stripe – Payment intent creation and response handling.
 3. Twilio – SMS sending, success/failure scenarios.
 4. Keycloak – Token validation, user info retrieval.
 5. AWS S3 – File upload success/failure.

#### Usage
 1. Set up the mock service and place the preset files in `/data` (eg. copying via docker-compose).
 2. Start the mock server and it will handle requests as defined in the configs.

Feel free to explore the repository for the full code examples and further details. 