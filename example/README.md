## Example Usage

This project includes an **example setup** that demonstrates how to run some **Service Under Test (SUT)** alongside **WireMock (echoserve)** and execute **Playwright** tests to verify the SUT's behavior under different configurations.

### Directory Structure

```
/example
├── config                      <-- this is config for the echoserve
│   ├── __files
│   │   └── authenticate.json   <-- response body file
│   └── config.properties       <-- echoserve configuration 
├── sut                         <-- some Service Under Test which uses echoserve to echoserve as authorization service  
│   ├── Dockerfile
│   ├── package-lock.json
│   ├── package.json
│   └── server.js
└── tests
    └── playwright
        ├── package.json
        ├── playwright.config.ts
        ├── tests
        │   └── login.test.ts    <-- Playwright test, which check SUT behavior when Echoserve "auth the user" and "user not authorized"
        └── test-results
```

### Prerequisites

- **Docker** and **Docker Compose** installed on your machine.

### Steps to Run the Example

1. **Navigate to the Example Directory**

   Open your terminal, clone repo and navigate to the `example` directory of the project:

   ```bash
   git clone https://github.com/jaracogmbh/echoserve.git #or gh repo clone jaracogmbh/echoserve
   cd echoserve
   ```

2. **Start the Services with Docker Compose**

   Build and start all the services (`echoserve`, `sut`, and `playwright`) using Docker Compose:

   ```bash
   docker-compose -f docker-compose-example.yml up --build
   ```

    **What Happens:**
   - **echoserve**: Runs WireMock on ports `8081` (for mocking services) and `19991` (for configuration).
   - **sut**: Launches the Service Under Test on port `3000`, which uses WireMock for handling authentication.
   - **playwright**: Executes a series of Playwright tests:
       1. Tests `SUT`'s behavior of an authorized user (expecting successful login).
       2. Calls `echoserve` to "deauthorize" the user by resetting the authorization state.
       3. Tests the behavior of an unauthorized user (expecting failed login).


3. **Monitor the Test Execution**

   As the services start, you can observe the logs in your terminal. Look for Playwright's test execution logs to verify that the tests are running as expected.

   **Sample Output:**

   ```
   sut-1        | Service Under Test running on port 3000
   playwright  | Running 2 tests using 1 worker
   playwright  |
   playwright  |   ✓ SUT Login Tests with WireMock Reset and Reconfiguration › Initial Login Test - Expect Success (200) (16ms)
   playwright  |   ✓ SUT Login Tests with WireMock Reset and Reconfiguration › Secondary Login Test - Expect Unauthorized (401) (16ms)
   playwright  |
   playwright  |   2 passed (32ms)
   playwright  | === All Tests Completed ===
   ```

5. **Clean Up**

   Once you're done with the tests, you can stop and remove the containers by pressing `Ctrl + C` in the terminal where Docker Compose is running or by executing:

   ```bash
   docker-compose down
   ```

- **Connection Refused Errors:**

    - Verify that the health checks are correctly set up and that both `echoserve` and `sut` are marked as healthy before Playwright starts testing.
    - Ensure that the environment variables `SUT_URL` and `ECHOSERVE_URL` are correctly pointing to the respective services within the Docker network.

### Conclusion

This example demonstrates the versatility of **echoserve** as a configurable mock service. It can be incredibly useful in a variety of testing scenarios, especially when you need a mock service that can be configured dynamically via API and files. Here are some potential use cases:

- Simulate different responses from external services without relying on live endpoints, ensuring consistent testing.
- Dynamically change responses (e.g., switch between 200 and 500 error codes) to test how your service handles unexpected conditions.
- Easily configure different behaviors for external services during development phases, allowing teams to focus on specific test cases.