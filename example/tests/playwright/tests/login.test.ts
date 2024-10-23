/**
    for release you need to change the polish comments to english
 */

import { test, expect, request } from "@playwright/test";

const SUT_URL = process.env.SUT_URL || "http://sut:3000";
const ECHOSERVE_MOCK_URL = process.env.ECHOSERVE_MOCK_URL || "http://echoserve:8081"; // for mock serving
const ECHOSERVE_CONFIG_URL = process.env.ECHOSERVE_CONFIG_URL || "http://echoserve:19991"; // for configuration

test.describe("SUT Operation Tests with WireMock Authorization", () => {
  let authToken;
  let apiContext;

  test.beforeAll(async ({ playwright }) => {
    // Utwórz kontekst API
    apiContext = await playwright.request.newContext();

    // Uzyskaj token autoryzacyjny poprzez mockowany serwer na porcie 8081
    const loginResponse = await apiContext.post(`${SUT_URL}/login`, {
      data: {
        username: "testuser",
        password: "testpass",
      },
      headers: {
        "Content-Type": "application/json",
      },
    });

    expect(loginResponse.status()).toBe(200);
    const loginData = await loginResponse.json();
    authToken = loginData.token;
  });

  test("Add Employee - Expect Success (200)", async () => {
    const response = await apiContext.post(`${SUT_URL}/addEmployee`, {
      headers: {
        "Authorization": `Bearer ${authToken}`,
        "Content-Type": "application/json",
      },
    });

    expect(response.status()).toBe(200);
    const responseBody = await response.json();
    expect(responseBody).toHaveProperty("message", "Employee added successfully");
  });

  test("Edit Employee - Expect Success (200)", async () => {
    const response = await apiContext.put(`${SUT_URL}/editEmployee`, {
      headers: {
        "Authorization": `Bearer ${authToken}`,
        "Content-Type": "application/json",
      },
    });

    expect(response.status()).toBe(200);
    const responseBody = await response.json();
    expect(responseBody).toHaveProperty("message", "Employee edited successfully");
  });

  test("Delete Something - Expect Success (200)", async () => {
    const response = await apiContext.delete(`${SUT_URL}/deleteSomething`, {
      headers: {
        "Authorization": `Bearer ${authToken}`,
        "Content-Type": "application/json",
      },
    });

    expect(response.status()).toBe(200);
    const responseBody = await response.json();
    expect(responseBody).toHaveProperty("message", "Item deleted successfully");
  });

  test.describe("SUT reset and check failed authorization", () => {
    test.beforeEach(async () => {
      console.log("=== Resetting WireMock ===");
      const resetResponse = await apiContext.post(`${ECHOSERVE_CONFIG_URL}/reset`);
      expect(resetResponse.ok()).toBeTruthy();

      console.log("=== Configuring WireMock to return 401 Unauthorized ===");
      const configureResponse = await apiContext.post(
        `${ECHOSERVE_CONFIG_URL}/configure`,
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
    });

    test("Add Employee - Expect Unauthorized (401)", async () => {
      const response = await apiContext.post(`${SUT_URL}/addEmployee`, {
        headers: {
          "Authorization": `Bearer ${authToken}`,
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

  test.afterAll(async () => {
    await apiContext.dispose();
  });
});