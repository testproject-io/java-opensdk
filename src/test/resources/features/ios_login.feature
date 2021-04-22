Feature: TestProject iOS Test with Cucumber Framework
  Perform a login scenario with Cucumber in an iOS Application

  Scenario: Run a Simple BDD iOS test with TestProject
    Given I open the iOS App
    When I login using my credentials
    Then I will see a logout button
    And I close the app
