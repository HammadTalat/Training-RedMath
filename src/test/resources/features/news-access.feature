Feature: News API access

  As a user of the News Management System
  I want protected news endpoints to require authentication
  So that unauthenticated users cannot access private information

  Scenario: Unauthenticated user cannot view news
    Given the request has no authentication
    When the user requests all news
    Then the response status should be 401

  Scenario: Authenticated user can view news
    Given the request has an authenticated JWT user
    When the user requests all news
    Then the response status should be 200