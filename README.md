# customs-duty-deferment-frontend

## About

A frontend microservice for the CDS Financials project which aims to allow the user to view duty deferment statements.

This service is built following GDS standards to [WCAG 2.2 AA](https://www.gov.uk/service-manual/helping-people-to-use-your-service/understanding-wcag)
We use the [GOV.UK design system](https://design-system.service.gov.uk/) to ensure consistency and compliance through the project
This application lives in the "public" zone. It integrates with:

Secure Document Exchange Service (SDES) bulk data API via the [SDES proxy](https://github.com/hmrc/secure-data-exchange-proxy)

Strategic Direct Debit Service (SDDS) 

### Prerequisites

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
The installation guide for the Service Manager is [here](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/set-up-service-manager.html)

| Command                                      | Description                                      |
|----------------------------------------------|--------------------------------------------------|
| `sm2 --start CUSTOMS_FINANCIALS_ALL -f`      | Runs all dependencies                            |
| `sm2 -s`                                     | Shows running services                           |
| `sm2 --stop CUSTOMS_DUTY_DEFERMENT_FRONTEND` | Stop the micro service                           |
| `sbt run` or `sbt "run 9397"`                | (from root dir) starts the service on port  9397 |
| `sbt "start -Dhttp.port=9397"`               | Run service in 'PROD mode'                       |

To start or stop the service in service manager:

    sm2 --stop CUSTOMS_DUTY_DEFERMENT_FRONTEND
    sm2 --start CUSTOMS_DUTY_DEFERMENT_FRONTEND

### Running the service locally

To run the service locally, you can use:
```bash
 sbt run
```

## How to Test this Service

There is just one test source tree in the `test` folder. Use `sbt test` to run them.

To get a unit test coverage report, you can run `sbt clean coverage test coverageReport`,
then open the resulting coverage report `target/scala-3.x.x/scoverage-report/index.html` in a web browser.

The minimum requirement for test coverage is 90%. Builds will fail when the project drops below this threshold.

#### Acceptance Tests (Smoke and Regression)
The acceptance tests for this service are in [customs-duty-deferment-acceptance-tests](https://github.com/hmrc/customs-duty-deferment-acceptance-tests).
For information about how to run them, and which environments they run against, see [README](https://github.com/hmrc/customs-duty-deferment-acceptance-tests/blob/b7dcc4f0e371fccb878da2d3e893cdd6da2999e5/README.md).

### Manual Testing in QA and Staging
After the successful [deployment pipeline](https://build.tax.service.gov.uk/job/BordersAndTradeLiveServices/job/CDSFinancials/job/customs-duty-deferment-frontend-pipeline/), 
manually verify the service in the QA and Staging environments.

| Environment | Service URL                                                                                      | 
|-------------|--------------------------------------------------------------------------------------------------|
| Local       | [Open the service in Local](http://localhost:9949/auth-login-stub/gg-sign-in)                    |
| QA          | [Open the service in QA](https://www.qa.tax.service.gov.uk/auth-login-stub/gg-sign-in)           |
| Staging     | [Open the service in Staging](https://www.staging.tax.service.gov.uk/auth-login-stub/gg-sign-in) |

## Service Catalogue
- You can find this repo in the MDTP catalogue here: [customs-duty-deferment-frontend](https://catalogue.tax.service.gov.uk/repositories/customs-duty-deferment-frontend)

## Jenkins Pipeline
- [customs-duty-deferment-frontend build](https://build.tax.service.gov.uk/job/BordersAndTradeLiveServices/job/CDSFinancials/job/customs-duty-deferment-frontend/)
- [customs-duty-deferment-frontend pipeline](https://build.tax.service.gov.uk/job/BordersAndTradeLiveServices/job/CDSFinancials/job/customs-duty-deferment-frontend-pipeline/)
- [customs-duty-deferment-frontend PR Builder](https://build.tax.service.gov.uk/job/BordersAndTradeLiveServices/job/CDSFinancials/job/customs-duty-deferment-frontend-pr-builder/)

## Service Manager Profiles
These profiles are defined in [service-manager-config](https://github.com/hmrc/service-manager-config).
Depending on what you wish to run, you can use one of these profiles (which include the CUSTOMS_DUTY_DEFERMENT_FRONTEND service):

- CUSTOMS_FINANCIALS_ALL
- CUSTOMS_FINANCIALS_TESTS
- CUSTOMS_FINANCIALS_ACCOUNT_CONTACT_FRONTEND_ALL
- CUSTOMS_FINANCIALS_ATS
- CUSTOMS_DUTY_DEFERMENT_ATS
- CUSTOMS_HISTORIC_STATEMENT_ATS

## Endpoints

You can find a list of microservice specific routes here - `/conf/app.routes`

Microservice entrypoint:  `/customs/duty-deferment`

| Path                          | Description                        |
|-------------------------------|------------------------------------|
| GET  /:linkId/account         | Retrieves duty deferment statement |                
| GET  /:linkId/contact-details | Retrieves user contact details     |                
| GET  /contact-details/edit    | Edit user contact details          |  

### Enrolment Required

The service can be accessed via MIDVA home page (CUSTOMS FINANCIALS FRONTEND microservice) using 'View statements' link under 'Duty deferment accounts'

Access CUSTOMS FINANCIALS FRONTEND using below enrolments and with below sample EORI numbers
via http://localhost:9949/auth-login-stub/gg-sign-in (on local) or https://<host:port>/auth-login-stub/gg-sign-in on DEV/QA/STAGING

Redirect URL - `/customs/payment-records`

| Enrolment Key	 | Identifier Name | Identifier Value |
|----------------|-----------------|------------------|
| `HMRC-CUS-ORG` | `EORINumber`    | `GB744638982000` |
| `HMRC-CUS-ORG` | `EORINumber`    | `GB744638982001` |

## Developer Notes

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

### Feature Flags
| Flag                          | Description                                                                                                                                         |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `fixed-systemdate-for-tests`  | Enable the fixed datetime for DateTimeService by enabling the stub data for current and requested statements. It is only enabled in Development env |
| `historic-statements-enabled` | Enable historic statements                                                                                                                          |

> #### Caution!
> There's a risk of WIP features being exposed in production!
> **Don't** enable features in `application.conf`, as this will apply globally by default

Feature flags are used appropriately on different envs and could be updated in customs-duty-deferment-frontend.yaml on pertinent env

#### Enable features
| Command                                       | Description                                        |
|-----------------------------------------------|----------------------------------------------------|
| `sbt "run -Dfeatures.some-feature-name=true"` | enables a feature locally without risking exposure |

Feature switches can be enabled per-environment via the `app-config-<env>` project in customs-duty-deferment-frontend.conf/.yaml

### Scalastyle
| Command               | Description                                                                |
|-----------------------|----------------------------------------------------------------------------|
| `sbt scalastyle`      | Runs scala style checks based on scalastyle-config.xml                     |                                                     |
| `sbt Test/scalastyle` | Runs scala style checks for unit tests based on test-scalastyle-config.xml |

### Run all checks
This is an sbt command alias specific to this project. It will run a scala style check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

### Scalafmt

The code is formatted with [sbt-scalafmt](https://scalameta.org/scalafmt/docs/installation.html#sbt), using the rules in `.scalafmt.conf`.
To check all project files are formatted as expected use the following commands:

```bash
 sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
 sbt scalafmtSbt
```

Format all project files as follows:

```bash
 sbt scalafmtAll
```

## License

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This code is open source software licensed under
the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).****