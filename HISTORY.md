# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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