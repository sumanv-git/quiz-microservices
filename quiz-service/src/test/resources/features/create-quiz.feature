Feature: Create a Quiz
  As a user
  I want to create a quiz for a specific category
  So that students can attempt it

  Scenario: Successfully create a quiz
    Given the question service returns question IDs [1, 2, 3] for category "Java" with 3 questions
    When I create a quiz with title "Java Basics" category "Java" and 3 questions
    Then the response status should be 201
    And a valid quiz ID should be returned

  Scenario: Create a quiz with a different category
    Given the question service returns question IDs [4, 5] for category "Python" with 2 questions
    When I create a quiz with title "Python Intro" category "Python" and 2 questions
    Then the response status should be 201
    And a valid quiz ID should be returned
