import { test, request, expect } from "@playwright/test";

const SUT_URL = process.env.SUT_URL || "http://sut:3000";
const ECHOSERVE_URL = process.env.ECHOSERVE_URL || "http://echoserve:19991";

test.describe("SUT Login Tests with WireMock Reset and Reconfiguration", () => {
  test("Initial Login Test - Expect Success (200)", async ({ request }) => {
    console.log("=== Running Initial Login Test (200 OK) ===");
    const response = await request.post(`${SUT_URL}/login`, {
      data: {
        username: "testuser",
        password: "testpass",
      },
      headers: {
        "Content-Type": "application/json",
      },
    });

    expect(response.status()).toBe(200);
    const responseBody = await response.json();
    expect(responseBody).toEqual({
      success: "true",
      token: "12345",
    });
  });
});

test.describe("SUT reset and check", () => {
  test.beforeEach(async () => {
    const apiContext = await request.newContext();

    console.log("=== Resetting WireMock ===");
    const resetResponse = await apiContext.post(`${ECHOSERVE_URL}/reset`);
    expect(resetResponse.ok()).toBeTruthy();
    console.log("WireMock reset successful.");

    console.log("=== Configuring WireMock to return 401 Unauthorized ===");
    const configureResponse = await apiContext.post(
      `${ECHOSERVE_URL}/configure`,
      {
        data: {
          requestType: "POST",
          url: "/authenticate",
          hostname: "localhost",
          responseBody: JSON.stringify({ error: "User unauthenticated" }),
          contentType: "application/json",
          statusCode: 401,
          requestBody: ".*",
          isRequestBodyRegex: "true",
        },
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    expect(configureResponse.ok()).toBeTruthy();
    console.log("WireMock configured to return 401 Unauthorized.");

    await apiContext.dispose();
  });

  test("Secondary Login Test - Expect Unauthorized (401)", async ({
    request,
  }) => {
    console.log("=== Running Secondary Login Test (401 Unauthorized) ===");
    const response = await request.post(`${SUT_URL}/login`, {
      data: {
        username: "testuser",
        password: "wrongpass",
      },
      headers: {
        "Content-Type": "application/json",
      },
    });

    expect(response.status()).toBe(401);
    const responseBody = await response.json();
    expect(responseBody).toEqual({
      error: "Authentication failed",
    });
  });
});
