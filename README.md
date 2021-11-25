
# customs-duty-deferment-frontend
A frontend component for the CDS Financials project which aims to allow the user to view duty deferment statements.

| Path                                                                   | Description                                                                                       |
| ---------------------------------------------------------------------  | ------------------------------------------------------------------------------------------------- |
| GET  /:linkId/account                                                  | Retrieves duty deferment statement                                                                |                
| GET  /:linkId/contact-details                                          | Retrieves user contact details                                                                    |                
| GET  /contact-details/edit                                             | Edit user contact details                                                                         |                


## Running the application locally

The application has the following runtime dependencies:

* `ASSETS_FRONTEND`
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

Once these services are running, you should be able to do `sbt "run 9397"` to start in `DEV` mode or
`sbt "start -Dhttp.port=9397"` to run in `PROD` mode.

The application should be run as part of the CUSTOMS_FINANCIALS_ALL profile due to it being an integral part of service.

## Running tests

There is just one test source tree in the `test` folder. Use `sbt test` to run them.

To get a unit test coverage report, you can run `sbt clean coverage test coverageReport`,
then open the resulting coverage report `target/scala-2.11/scoverage-report/index.html` in a web browser.

Test coverage threshold is set at 75% - so if you commit any significant amount of implementation code without writing tests, you can expect the build to fail.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").