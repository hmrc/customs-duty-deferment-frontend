# customs-duty-deferment-frontend

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Coverage](https://img.shields.io/badge/test_coverage-90-green.svg)](/target/scala-2.11/scoverage-report/index.html) [![Accessibility](https://img.shields.io/badge/WCAG2.2-AA-purple.svg)](https://www.gov.uk/service-manual/helping-people-to-use-your-service/understanding-wcag)

A frontend microservice for the CDS Financials project which aims to allow the user to view duty deferment statements.

This service is built following GDS standards to [WCAG 2.2 AA](https://www.gov.uk/service-manual/helping-people-to-use-your-service/understanding-wcag)

We use the [GOV.UK design system](https://design-system.service.gov.uk/) to ensure consistency and compliance through the project

This application lives in the "public" zone. It integrates with:

Secure Document Exchange Service (SDES) bulk data API via the [SDES proxy](https://github.com/hmrc/secure-data-exchange-proxy)

Strategic Direct Debit Service (SDDS)

## Running the service locally

### Runtime dependencies

The application has the following runtime dependencies:

* `AUTH`
* `AUTH_LOGIN_STUB`
* `AUTH_LOGIN_API`
* `BAS_GATEWAY`
* `CA_FRONTEND`
* `SSO`
* `USER_DETAILS`
* `CUSTOMS_FINANCIALS_API`
* `CUSTOMS_FINANCIALS_HODS_STUB`
* `CUSTOMS_FINANCIALS_SDES_STUB`
* `CONTACT_FRONTEND`
* `CUSTOMS_DATA_STORE`
* `CUSTOMS_FINANCIALS_SESSION_CACHE`
* `CUSTOMS-FINANCIALS_ACCOUNT_CONTACT_FRONTEND`

Default service port on local - 9397

The easiest way to get started with these is via the service manager CLI - you can find the installation guide [here](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/set-up-service-manager.html)

| Command                                      | Description                                      |
|----------------------------------------------|--------------------------------------------------|
| `sm2 --start CUSTOMS_FINANCIALS_ALL`         | Runs all dependencies                            |
| `sm2 -s`                                     | Shows running services                           |
| `sm2 --stop CUSTOMS_DUTY_DEFERMENT_FRONTEND` | Stop the micro service                           |
| `sbt run` or `sbt "run 9397"`                | (from root dir) starts the service on port  9397 |
| `sbt "start -Dhttp.port=9397"`               | Run service in 'PROD mode'                       |

### Login enrolments

The service can be accessed via MIDVA home page (CUSTOMS FINANCIALS FRONTEND microservice) using below enrolments and with below sample EORI numbers

| Enrolment Key	| Identifier Name | Identifier Value |
| -------- | ------- | ------- |
| `HMRC-CUS-ORG` | `EORINumber`| `GB744638982000` |
| `HMRC-CUS-ORG` | `EORINumber`| `GB744638982001` |

## Running tests and test coverage

There is just one test source tree in the `test` folder. Use `sbt test` to run them.

To get a unit test coverage report, you can run `sbt clean coverage test coverageReport`,
then open the resulting coverage report `target/scala-3.3.4/scoverage-report/index.html` in a web browser.

The minimum requirement for test coverage is 90%. Builds will fail when the project drops below this threshold.

## Available Routes

You can find a list of microservice specific routes here - `/conf/app.routes`

Application entrypoint:  `/customs/duty-deferment`

| Path                          | Description                        |
|-------------------------------|------------------------------------|
| GET  /:linkId/account         | Retrieves duty deferment statement |                
| GET  /:linkId/contact-details | Retrieves user contact details     |                
| GET  /contact-details/edit    | Edit user contact details          |  

## Feature Switches

> ### Caution!
> There's a risk of WIP features being exposed in production!
> **Don't** enable features in `application.conf`, as this will apply globally by default

Feature flags are used appropriately on different envs and could be updated in customs-duty-deferment-frontend.yaml on pertinent env

### Enable features
| Command                                       | Description                                        |
|-----------------------------------------------|----------------------------------------------------|
| `sbt "run -Dfeatures.some-feature-name=true"` | enables a feature locally without risking exposure |

Feature switches can be enabled per-environment via the `app-config-<env>` project in customs-duty-deferment-frontend.conf/.yaml:

### Available feature flags
| Flag                          | Description                                                                                                                                         |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `fixed-systemdate-for-tests`  | Enable the fixed datetime for DateTimeService by enabling the stub data for current and requested statements. It is only enabled in Development env |
| `historic-statements-enabled` | Enable historic statements                                                                                                                          |

## Scalastyle, scala formatting and run all checks

### Scalastyle checks
| Command               | Description                                                                |
|-----------------------|----------------------------------------------------------------------------|
| `sbt scalastyle`      | Runs scala style checks based on scalastyle-config.xml                     |                                                     |
| `sbt Test/scalastyle` | Runs scala style checks for unit tests based on test-scalastyle-config.xml |

### Scala format
| Command                | Description                                 |
|------------------------|---------------------------------------------|
| `sbt scalafmtCheckAll` | Scala Format checks based on .scalafmt.conf |                                                     |
| `sbt scalafmtAll`      | Formats the code based on .scalafmt.conf    |
| `sbt scalafmtOnly`     | Formats specified files listed              |

### Run all checks
This is a sbt command alias specific to this project. It will run a scala style check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

## Helpful commands

| Command                                       | Description                                                                                                                                 |
|-----------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `sbt runAllChecks`                            | Runs all standard code checks                                                                                                               |
| `sbt clean`                                   | Cleans code                                                                                                                                 |
| `sbt compile`                                 | Compiles the code                                                                                                                           |
| `sbt test`                                    | Runs unit tests                                                                                                                             |
| `sbt it/test`                                 | Runs integration tests                                                                                                                      |
| `sbt scalafmtCheckAll`                        | Runs code formatting checks based on .scalafmt.conf                                                                                         |
| `sbt scalastyle`                              | Runs scala style checks based on scalastyle-config.xml                                                                                      |
| `sbt Test/scalastyle`                         | Runs scala style checks for unit tests based on test-scalastyle-config.xml                                                                  |
| `sbt coverageReport`                          | Produces a code coverage report                                                                                                             |
| `sbt "test:testOnly *TEST_FILE_NAME*"`        | Runs tests for a single file                                                                                                                |
| `sbt clean coverage test coverageReport`      | Runs the unit tests with enabled coverage and generates coverage report that you can find in target/scala-3.x.x/scoverage-report/index.html |
| `sbt "run -Dfeatures.some-feature-name=true"` | Enables a feature locally without risking exposure                                                                                          |
