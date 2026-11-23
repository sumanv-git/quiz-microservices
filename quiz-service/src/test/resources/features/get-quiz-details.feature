Feature: Get Quiz Details
  As a student
  I want to retrieve the questions for a quiz
  So that I can attempt it

  Scenario: Successfully retrieve questions for an existing quiz
    Given a quiz with ID 1 exists in the system with question IDs [1, 2]
    And the question service returns question details for IDs [1, 2]
    When I request quiz details for quiz ID 1
    Then the response status should be 200
    And the response should contain 2 questions

  Scenario: Retrieve details for a non-existent quiz
    Given no quiz exists with ID 99
    When I request quiz details for quiz ID 99
    Then a "Quiz not found" runtime exception should be thrown
