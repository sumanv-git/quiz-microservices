Feature: Submit a Quiz
  As a student
  I want to submit my quiz answers
  So that I can see my score

  Scenario: Submit answers for an existing quiz
    Given a quiz with ID 1 exists in the system with question IDs [1, 2]
    When I submit answers for quiz ID 1
    Then the response status should be 200
    And the score returned should be 0

  Scenario: Submit answers for a non-existent quiz
    Given no quiz exists with ID 99
    When I submit answers for quiz ID 99
    Then a "Quiz not found" runtime exception should be thrown
