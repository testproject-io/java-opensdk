Feature: TestProject with Cucumber Framework
  Perform a login scenario with Cucumber

  Scenario: Run a Simple BDD test with TestProject
    Given I navigate to the TestProject example page
    When I perform a login
    Then I should see a logout button
    And I should close the browser
