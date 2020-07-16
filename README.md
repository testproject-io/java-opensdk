# TestProject SDK For Java

![Build](https://github.com/testproject-io/java-sdk/workflows/Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.testproject/java-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.testproject/java-sdk)
[![javadoc](https://javadoc.io/badge2/io.testproject/java-sdk/javadoc.svg)](https://javadoc.io/doc/io.testproject/java-sdk)

[TestProject](https://testproject.io) is a **Free** Test Automation platform for Web, Mobile and API testing. \
To get familiar with the TestProject, visit our main [documentation](https://docs.testproject.io/) website.

TestProject SDK is a single, integrated interface to scripting with the most popular open source test automation frameworks.

From now on, you can effortlessly execute Selenium and Appium native tests using a single automation platform that already takes care of all the complex setup, maintenance and configs.

With one unified SDK available across multiple languages, developers and testers receive a go-to toolset, solving some of the greatest challenges in open source test automation.

With TestProject SDK, users save a bunch of time and enjoy the following benefits out of the box:

* 100% open source and available as a [Maven](https://mvnrepository.com/artifact/io.testproject/java-sdk) dependency.
* 5-minute simple Selenium and Appium setup with a single [Agent](https://docs.testproject.io/testproject-agents) deployment.
* Automatic test reports in HTML/PDF format (including screenshots). 
* Collaborative reporting dashboards with execution history and RESTful API support.
* Always up-to-date with the latest and stable Selenium driver version.
* A simplified, familiar syntax for both web and mobile applications.
* Complete test runner capabilities for both local and remote executions, anywhere.
* Cross platform support for Mac, Windows, Linux and Docker.
* Ability to store and execute tests locally on any source control tool, such as Git.

# Getting Started

To get started, you need to complete the following prerequisites checklist:

* Login to your account at https://app.testproject.io/ or [register](https://app.testproject.io/signup/) a new one.
* [Download](https://app.testproject.io/#/download) and install an Agent for your operating system or pull a container from [Docker Hub](https://hub.docker.com/r/testproject/agent).
* Run the Agent and [register](https://docs.testproject.io/getting-started/installation-and-setup#register-the-agent) it with your Account.
* Get a development token from [Integrations / SDK](https://app.testproject.io/#/integrations/sdk) page. 

> You must have Java Development Kit (JDK) 11 or newer installed.

## Installation

For a Maven project, add the following to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.testproject</groupId>
  <artifactId>java-sdk</artifactId>
  <version>0.63.3-RELEASE</version>
  <classifier>sources</classifier>
</dependency>
```

For a Gradle project, add the following to your `build.gradle` file:
```
implementation 'io.testproject:java-sdk:0.63.3-RELEASE'
```

# Test Development

Using a TestProject driver is exactly identical to using a Selenium driver.\
Changing the import statement is enough in most cases.

> Following examples are based on the `ChromeDriver`, however are applicable to any other supported drivers.

Here's an example of how to create a TestProject version of `ChromeDriver`:

```java
// import org.openqa.selenium.chrome.ChromeDriver; <-- Replaced
import io.testproject.sdk.drivers.web.ChromeDriver;

...

public class MyTest {
  ChromeDriver driver = new ChromeDriver(new ChromeOptions());
}

```

Here a complete test example:

> Make sure to [configure a development token](https://github.com/testproject-io/java-sdk#test-development) before running this example.

```java
package io.testproject.sdk.tests.examples.simple;

import io.testproject.sdk.drivers.web.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

public final class WebTest {

    public static void main(final String[] args) throws Exception {
        ChromeDriver driver = new ChromeDriver(new ChromeOptions());

        driver.navigate().to("https://example.testproject.io/web/");

        driver.findElement(By.cssSelector("#name")).sendKeys("John Smith");
        driver.findElement(By.cssSelector("#password")).sendKeys("12345");
        driver.findElement(By.cssSelector("#login")).click();

        boolean passed = driver.findElement(By.cssSelector("#logout")).isDisplayed();
        if (passed) {
            System.out.println("Test Passed");
        } else {
            System.out.println("Test Failed");
        }

        driver.quit();
    }
}
```

# Drivers

TestProject SDK overrides standard Selenium/Appium drivers with extended functionality. \
Below is the packages structure containing all supported drivers:

```ascii
io.testproject.sdk.drivers
├── web
│   ├── ChromeDriver
│   ├── EdgeDriver
│   ├── FirefoxDriver
│   ├── InternetExplorerDriver
│   ├── SafariDriver
│   └── RemoteWebDriver
├── android
│   └── AndroidDriver
└── ios
    └── IOSDriver
```

## Development Token

The SDK uses a development token for communication with the Agent and the TestProject platform.\
Drivers search the developer token in an environment variable `TP_DEV_TOKEN`.\
Token can be also provided explicitly using this constructor:

```java
public ChromeDriver(final String token, final ChromeOptions options)
```

## Remote Agent

By default, drivers communicate with the local Agent listening on http://localhost:8585.

Agent URL (host and port), can be also provided explicitly using this constructor:

```java
public ChromeDriver(final URL remoteAddress, final ChromeOptions options)
```

It can also be set using the `TP_AGENT_URL` environment variable.

## Driver Builder

The SDK provides a generic builder for the drivers - `DriverBuilder`, for example:

```java
ChromeDriver driver = new DriverBuilder<ChromeDriver>(new ChromeOptions())
  .withRemoteAddress(new URL("http://remote-agent-url:9999"))
  .withToken("***")
  .build(ChromeDriver.class);
```

# Reports

TestProject SDK reports all driver commands and their results to the TestProject Cloud.\
Doing so, allows us to present beautifully designed reports and statistics in it's dashboards.

Reports can be completely disabled using this constructor:

```java
public ChromeDriver(final ChromeOptions options, final boolean disableReports)
```

There are other constructor permutations, refer to method summaries of each specific driver class.

## Implicit Project and Job Names

The SDK will attempt to infer Project and Job names from JUnit / TestNG annotations.\
If found the following logic and priorities take place:

* _Package_ name of the class containing the method is used for **Project** name.
* JUnit 4 / 5
  * _Class_ name or the _@DisplayName_ annotation (JUnit 5 only) on the class is used for the **Job** name
  * _Method_ name or the _@DisplayName_ annotation (JUnit 5 only) on the method is used for the **Test** name(s)
* TestNG
  * _Class_ name or _description_ from _@BeforeSuite_ / _@BeforeClass_ annotations if found, are used for the **Job** name
  * _Method_ name or the _@Test_ annotation (and it's _testName_ / _description_ fields) on the method is used for the **Test** name(s)
  
Examples of implicit Project & Job names inferred from annotations:

* [JUnit 5 example](/src/test/java/io/testproject/sdk/tests/examples/frameworks/junit5/InferredReportTest.java)
* [TestNG example](/src/test/java/io/testproject/sdk/tests/examples/frameworks/testng/InferredReportTest.java)

## Explicit Names

Project and Job names can be also specified explicitly using this constructor:

```java
public ChromeDriver(final ChromeOptions options, final String projectName, final String jobName)
```

For example:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions(), "My First Project", "My First Job");
```

Same can be achieved using the `DriverBuilder`:

```java
ChromeDriver driver = new DriverBuilder<ChromeDriver>(new ChromeOptions())
  .withProjectName("My First Project")
  .withJobName("My First Job")
  .build(ChromeDriver.class);
```

Examples of explicit Project & Job names configuration:

* [JUnit 5 example](/src/test/java/io/testproject/sdk/tests/examples/frameworks/junit5/ExplicitReportTest.java)
* [TestNG example](/src/test/java/io/testproject/sdk/tests/examples/frameworks/testng/ExplicitReportTest.java)

## Tests Reports

### Automatic Tests Reporting

Tests are reported automatically when a test **ends** or when driver _quits_.\
This behavior can be overridden or disabled (see [Disabling Reports](#disabling-reports) section below).

In order to determine that a test ends, call stack is traversed searching for an annotated methods.\
When an annotated method execution starts and previously detected ends, test end is concluded.

Any unit testing framework annotations is reckoned, creating a _separate_ test in report for every annotated method.\
For example, following JUnit based code, will generate the following _six_ tests in the report:

```java
@BeforeEach
void beforeTestExample(TestInfo testInfo) {
    driver.report().step("Preparing Test: " + testInfo.getDisplayName());
}

@Test
@DisplayName(value = "Google")
void testGoogle() {
    driver.navigate().to("https://www.google.com/");
}

@Test
@DisplayName(value = "Yahoo!")
void testYahoo() {
    driver.navigate().to("https://yahoo.com/");
}

@AfterEach
void afterTestExample(TestInfo testInfo) {
    driver.report().step("Finishing Test: " + testInfo.getDisplayName());
}
```

Report:

```ascii
Report
├── beforeTestExample
│   ├── Preparing Test: Yahoo!
├── Yahoo! Test
│   ├── Navigate To https://yahoo.com/
├── afterTestExample
│   ├── Finishing Test: Yahoo!
├── beforeTestExample
│   ├── Preparing Test: Google
├── Google Test
│   ├── Navigate To https://google.com/
└── afterTestExample
    └── Finishing Test: Google
```

See a [complete example](/src/test/java/io/testproject/sdk/tests/examples/reports/AutomaticReporting.java) with automatic test reporting.

#### Limitations

JUnit5 dynamic test names cannot be inferred, and should be reported manually.\
These will be reported as _Dynamic Test_ when reported automatically.

### Manual Tests Reporting

To report tests manually, use `driver.report().tests()` method and it's overloads, for example:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().test("My First Test").submit();
```

> It's important to disable automatic tests reporting when using the manual option to avoid collision.

Note that `driver.report().test()` returns a `ClosableTestReport` object.\
An explicit call to `submit()` or closing hte object is required for the report to be sent.

Using this closable object can be beneficial in the following case:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
try (ClosableTestReport report = driver.report().test("Example Test with Exception")) {
  driver.findElement(By.id("NO_SUCH_ELEMENT")).click();
}
```

Assuming there is no element on the DOM with such an ID: `NO_SUCH_ELEMENT`, an exception will be thrown and test will fail, but before that, closable object will get closed and test will be reported.

See a [complete example](/src/test/java/io/testproject/sdk/tests/examples/reports/ManualReporting.java) with manual test reporting.

### Steps

Steps are reported automatically when driver commands are executed.\
If this feature is disabled, or in addition, manual reports can be performed, for example:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().step("User logged in successfully");
```

## Disabling Reports

If reports were **not** disabled when the driver was created, they can be disabled or enabled later.\
However, if reporting was explicitly disabled when the driver was created, it can **not** be enabled later.

### Disable all reports

Following will disable all types of reports:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().disableReports(true);
```

### Disable tests automatic reports

Following will disable tests automatic reporting.\
All steps will reside in a single test report, unless tests are reported manually using `driver.report().tests()`:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().disableTestAutoReports(true);
```

### Disable driver commands reports

Following will disable driver _commands_ reporting.\
Report will have no steps, unless reported manually using `driver.report().step()`:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().disableCommandReports(true);
```

### Disable commands redaction

When reporting driver commands, SDK performs a redaction of sensitive data (values) sent to secured elements.\
If the element is one of the following:

* Any element with `type` attribute set to `password`
* With XCUITest, on iOS an element type of `XCUIElementTypeSecureTextField`

Values sent to these elements will be converted to three asterisks - `***`.\
This behavior can be disabled as following:

```java
ChromeDriver driver = new ChromeDriver(new ChromeOptions());
driver.report().disableRedaction(true);
```

# Logging

TestProject SDK uses SLF4J API for logging.\
This means it only bind to a thin logger wrapper API, and itself does not provide a logging implementation.

Developers must choose a concrete implementation to use in order to control the logging output.\
There are many SLF4J logger implementation choices, such as the following:

* [Logback](http://logback.qos.ch/)
* [Log4j](https://logging.apache.org/log4j/2.x/)
* [Java Logging](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html)

Note that each logger implementation would have it's own configuration format.\
Consult specific logger documentation for on how to use it.

## Using slf4j-simple

This is the simplest option that requires no configuration at all.\
All needed is to add a [dependency](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple) to the project:

Maven:
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.30</version>
    <scope>test</scope>
</dependency>
```

Gradle:
```groovy
testCompile group: 'org.slf4j', name: 'slf4j-simple', version: '1.7.30'
```

By default, logging level is set to _INFO_.\
To see _TRACE_ verbose messages, add this System Property `-Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE`

## Using Logback

Logback is a very popular logger implementation that is production ready and packed with many features.\
To use it, add a [dependency](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic) to the project:

Maven:
```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
    <scope>test</scope>
</dependency>
```

Gradle:
```groovy
testCompile group: 'ch.qos.logback', name: 'logback-classic', version: '1.2.3'
```

Create a new file `src/main/resources/logback.xml` in your project and paste the following:

```xml
<configuration>

    <!--Silence initial configuration logs-->
    <statusListener class="ch.qos.logback.core.status.NopStatusListener" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-20logger{0} %message%n
            </Pattern>
        </layout>
    </appender>

    <logger name="io.testproject" level="ALL" />

    <root level="ERROR">
        <appender-ref ref="CONSOLE"/>
    </root>

</configuration>
```

# Examples

Here are more [examples](/src/test/java/io/testproject/sdk/tests/examples) for each driver:

* Simple Flows (without JUnit / TestNG)
  * [Web](src/test/java/io/testproject/sdk/tests/examples/simple/WebTest.java)
  * [Android](src/test/java/io/testproject/sdk/tests/examples/simple/AndroidTest.java)
  * [iOS](src/test/java/io/testproject/sdk/tests/examples/simple/IOSTest.java)
* Web
  * [Chrome Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/ChromeDriverTest.java)
  * [Edge Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/EdgeDriverTest.java)
  * [Firefox Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/FirefoxDriverTest.java)
  * [Internet Explorer Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/InternetExplorerDriverTest.java)
  * [Safari Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/SafariDriverTest.java)
  * [Remote Web Driver Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/RemoteWebDriverTest.java)
* Android
  * [Native App Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/AndroidDriverTest.java)
  * [Native App Source](https://github.com/testproject-io/android-demo-app)
  * [Web Test on Mobile Chrome](/src/test/java/io/testproject/sdk/tests/examples/drivers/AndroidDriverChromeTest.java)
* iOS
  * [Native App Test](/src/test/java/io/testproject/sdk/tests/examples/drivers/IOSDriverTest.java)
  * [Native App Source](https://github.com/testproject-io/ios-demo-app)
  * [Web Test on Mobile Safari](/src/test/java/io/testproject/sdk/tests/examples/drivers/IOSSafariDriverTest.java)
    

# License

TestProject SDK For Java is licensed under the LICENSE file in the root directory of this source tree.