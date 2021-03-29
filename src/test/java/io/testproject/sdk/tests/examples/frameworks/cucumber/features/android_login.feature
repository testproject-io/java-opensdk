Feature: TestProject Android Test with Cucumber Framework
  Perform a login scenario with Cucumber in an Android Application

  Scenario: Run a Simple BDD Android test with TestProject
    Given I open the Android App
    When I login using my credentials
    Then I will see a logout button
    And I close the app
