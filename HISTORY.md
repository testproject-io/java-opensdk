# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2021-04-27

### Added

- Added Control for Reports configuration, it is now possible to state the name and path of the generated report.
- Added Remote Execution Support, it is now possible to execute tests on remote agents in the same network.
- Added Cucumber Examples for uploading tests to TestProject platform.
- Added Session Reuse for Cucumber Tests who do not provide a Job Name.
- Added support for JUnit4 assertion reporting.
- Added Control for Reports configuration, it is now possible to state the name and path of the generated report.
- Added Remote Execution Support, it is now possible to execute tests on remote agents in the same network.
- Added data providers that allow uploading parameterized tests to TestProject platform.
- ([#104](https://github.com/testproject-io/java-opensdk/issues/104)) - Fix for Report Type being ignored on Chrome Driver.

### Fixed

- Fixed test name inferring in JUnit.
- Fixed multiple jobs reporting when running several tests without any given job name/project name.
- Fixed ReportType ignored in ChromeDriver.
- Fixed Cucumber tests reports which can sometimes be generated with steps out of order.

## [1.0.0] - 2021-04-01

### Added

- Added option to configure report type (cloud, local or both).
- Added Cucumber framework mobile example test.

## [0.65.4] - 2021-02-12

### Fixed

- Fixed exception thrown when reporting failed Cucumber steps.

## [0.65.3] - 2021-01-28

### Added

- Added package and upload instructions.

### Fixed

- Fixed Cucumber reporting plugin logic.
- Fixed CI tests.

## [0.65.2] - 2021-01-27

### Added

- Reporting plugin for the Cucumber framework.

### Fixed

- Fixed issue where test name wasn't reported without calling driver.quit().

## [0.65.1] - 2021-01-07

### Added

- Reporting extension for TestNG has been added.

### Fixed

- Fixed issue where TCP socket would close before all test steps were reported.

## [0.65.0] - 2020-12-16

### Added

- Environment variable that will be set to allow executing OpenSDK coded tests from within recorded tests.
- Resource file `testproject-opensdk.properties` that will be created during build, to indicate SDK version in dependant projects. 

### Changed

- Throw `DeviceNotConnectedException` and `MissingBrowserException` in relevant cases.
### Fixed

- Handled parsing errors when assigning non-string outputs from addon actions to it's proxy class members.
- Skip Selenium server address parsing when using a Generic driver.
## [0.64.5] - 2020-11-25

### Fixed

- Fixed logic assigning output values to proxy classes after addon execution.

## [0.64.4] - 2020-10-26

### Changed

- Improved error messages logged when initializing a development session fails.

## [0.64.3] - 2020-10-21

### Fixed

- Fixed a case when tets name was not reported after session ends.
- Fixed IE driver re-initialization logic.

## [0.64.2] - 2020-10-13

### Fixed

- Fixed compatibility issue with ChromeOptions class, upgrading Guava library to version 29.
- Fixed a scenario in which instantiation of a proxy class failed during test name inferring.

## [0.64.1] - 2020-10-08

### Added 

- Added Generic driver to execute non-UI tests.

### Fixed

- Close the agent client when the driver quits to allow the process to exit.

## [0.64.0] - 2020-09-08

### Added 

- SDK will keep a development session open and only restart the driver when reporting the same Job (only when working with Agent 0.64.20 or newer).

## [0.63.5] - 2020-08-28

### Added 

- Screenshots are now being reported when command execution fails.
- Reporting extension for JUnit5 has been added.

### Fixed

- Removed bundled logback.xml that can interfere with explicitly specified logback configuration.

## [0.63.4] - 2020-08-07

### Fixed 

- Fixed false-failure happening when using Actions class and automatically reporting commands.

### Changed

- Now Driver and AgentClient will be closed gracefully when process terminates

### Added

- Added custom capability and documentation for execution using a cloud (e.g SauceLabs) driver.
- Junit4 Example

## [0.63.3] - 2020-07-15

### Fixed 

- Fixed reports not created when there is no package to infer a project name from.

## [0.63.2] - 2020-07-06

Initial release.
