# QA Automation Homework – Problem 1

Automation solution for the SauceDemo portion of the QA homework.

## Assignment coverage

This project covers:

- Login automation using Selenium for Java.
- Positive and negative login cases.
- Automatic screenshots when a test fails.
- Add multiple products to the cart.
- Checkout flow and validation of `total = item total + tax`.
- Parallel execution on Chrome and Firefox.
- GitHub Actions integration.
- Allure reports.

Target application:

`https://www.saucedemo.com/`

---

## Tech stack

- Java 17
- Maven
- Selenium WebDriver
- TestNG
- Page Object Model
- `ThreadLocal<WebDriver>`
- Selenium Manager
- Maven Surefire test runner
- Allure report
- GitHub Actions

### Why ThreadLocal WebDriver?

The cross-browser suite runs Chrome and Firefox at the same time.

A shared static WebDriver can cause one parallel test thread to overwrite another
browser session. `ThreadLocal<WebDriver>` gives each test thread an isolated driver:

```text
Chrome thread  -> ChromeDriver
Firefox thread -> FirefoxDriver
```

---

## Project structure

```text
qa-automation-problem1/
├── .github/
│   └── workflows/
│       └── ui-tests.yml
├── src/
│   ├── main/java/com/qa/homework/
│   │   ├── config/
│   │   │   └── Config.java
│   │   ├── driver/
│   │   │   └── DriverFactory.java
│   │   └── pages/
│   │       ├── BasePage.java
│   │       ├── LoginPage.java
│   │       ├── InventoryPage.java
│   │       ├── CartPage.java
│   │       └── CheckoutPage.java
│   └── test/
│       ├── java/com/qa/homework/
│       │   ├── base/
│       │   │   └── BaseTest.java
│       │   ├── listeners/
│       │   │   └── ScreenshotListener.java
│       │   └── tests/
│       │       ├── LoginTest.java
│       │       └── CheckoutTest.java
│       └── resources/
│           ├── META-INF/services/
│           │   └── io.qameta.allure.listener.TestLifecycleListener
│           ├── allure.properties
│           └── config.properties
├── pom.xml
├── testng-cross-browser.xml
├── testng-single-browser.xml
└── README.md
```

---

# 1. Prerequisites

Install:

1. JDK 17+
2. Maven 3.6.3+
3. Google Chrome
4. Mozilla Firefox
5. Git

Check versions:

```bash
java -version
mvn -version
git --version
```

You do **not** need to manually download ChromeDriver or GeckoDriver.
Selenium Manager resolves the compatible driver automatically.

---

# 2. Clone and open the project

```bash
git clone <your-repository-url>
cd qa-automation-problem1
```

If you are using the ZIP version, extract it and open the extracted folder:

```bash
cd qa-automation-problem1
```

---

# 3. Run all tests in parallel on Chrome + Firefox

This is the default project profile.

```bash
mvn test -Dheadless=true
```

Equivalent explicit command:

```bash
mvn test -Pcross-browser -Dheadless=true
```

`testng-cross-browser.xml` uses:

```xml
parallel="tests"
thread-count="2"
```

Therefore Chrome and Firefox execute concurrently.

Do not pass `-Dbrowser=chrome` or `-Dbrowser=firefox` with the
`cross-browser` profile. That system property overrides the browser parameter
from the TestNG XML and can make both parallel suites run the same browser.

---

# 4. Run in headless mode

Useful for CI or local execution without opening browser windows:

```bash
mvn test -Dheadless=true
```

---

# 5. Run only Chrome

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=chrome
```

Headless:

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=chrome \
  -Dheadless=true
```

---

# 6. Run only Firefox

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=firefox
```

Headless:

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=firefox \
  -Dheadless=true
```

---

# 7. Run selected test groups

Run only regression tests on the default cross-browser suite:

```bash
mvn test \
  -Pcross-browser \
  -Dgroups=regression \
  -Dheadless=true
```

Run only regression tests on Chrome:

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=chrome \
  -Dgroups=regression \
  -Dheadless=true
```

Run only regression tests on Firefox:

```bash
mvn test \
  -Psingle-browser \
  -Dbrowser=firefox \
  -Dgroups=regression \
  -Dheadless=true
```

Other useful groups include:

```text
smoke
login
inventory
checkout
performance
visual
known-issue
e2e
```

---

# 8. Override the base URL

The default URL is configured in:

```text
src/test/resources/config.properties
```

You can override it without modifying code:

```bash
mvn test \
  -Dbase.url=https://www.saucedemo.com/
```

---

# 9. Test scenarios

## Login

The test suite currently covers:

- `standard_user`: baseline inventory behavior.
  - Login succeeds.
  - Product catalog contains six products.
  - Each product has a unique product image.
- `locked_out_user`: negative login behavior.
  - Login is blocked.
  - Error message contains `Sorry, this user has been locked out`.
- `problem_user`: known broken inventory behavior.
  - Product catalog loads.
  - Product images are broken/reused.
  - Add-to-cart behavior is partial.
- `performance_glitch_user`: performance behavior.
  - Login succeeds.
  - Login time must be within `performance.login.max.seconds`.
- `error_user`: error handling behavior.
  - Product sorting remains stuck on the default option.
- `visual_user`: visual regression behavior.
  - Low-to-high price sorting exposes the known visual sorting issue.
- Wrong password.
- Unknown username.
- Empty username.
- Empty password.
- Both username and password empty.
- Username with unexpected leading/trailing spaces.
- Password with unexpected leading/trailing spaces.
- Username and password case-sensitivity.
- Email-style username input.
- Username with special characters.
- Injection-like username input.
- Overly long username and password values.

The negative login validation cases are data-driven with a TestNG
`@DataProvider`.

## Cart and checkout

The suite covers:

- Log in.
- Add two different products.
- Verify cart badge count.
- Verify both products exist in cart.
- Enter checkout information.
- Verify:
  `total = item total + tax`.
- Finish checkout.
- Verify final order confirmation.
- Negative validation for missing customer information.

---

# 10. Failure screenshots

`ScreenshotListener` implements Allure's `TestLifecycleListener`.

When a test fails, a screenshot is automatically saved under:

```text
target/screenshots/
```

Example:

```text
target/screenshots/
invalidLoginShowsUsefulError-firefox-20260917-103000-001.png
```

The same screenshot is also attached to the failed test in Allure.

The Allure lifecycle listener is registered through Java SPI under
`src/test/resources/META-INF/services`, while `BaseTest` registers Allure's
TestNG listener. Screenshots are captured for both suite XML runs and direct
class/method runs.

Use `mvn test` when you want to keep existing screenshots. The Maven test
lifecycle cleans only the previous Allure output before each run, so
`target/screenshots/` is preserved while the generated report stays fresh.
`mvn clean test` deletes the whole `target/` directory before running tests,
including previous screenshots.

---

# 11. Allure reports

Run tests first. The Allure TestNG listener writes raw report data under:

```text
target/allure-results/
```

Generate a static HTML report:

```bash
mvn allure:report
```

Serve the generated report:

```bash
mvn allure:serve
```

Or serve the generated static report with a local HTTP server:

```bash
cd target/site/allure-maven-plugin
python3 -m http.server 8080
```

Then open `http://localhost:8080`.

Do not double-click `target/site/allure-maven-plugin/index.html` directly for
the multi-file report. Browsers can block Allure's local `file://` requests for
`data/*.json`, which leaves the page stuck on a loader or an empty result view.

If you need a report that can be opened directly as a downloaded file, generate
the single-file report:

```bash
mvn allure:report
target/.allure/bin/allure awesome target/allure-results \
  --output target/allure-single-file-report \
  --single-file \
  --report-name "SauceDemo UI Tests"
```

Then open `target/allure-single-file-report/index.html`.

Maven Surefire is still used as the test runner, but the reviewable report for
local and CI runs is Allure.

The Maven test lifecycle removes stale Allure output before running tests, while
keeping failure screenshots. This prevents old local runs from appearing as
retries in the report.

If you want a completely fresh workspace and do not need to preserve old
screenshots, run:

```bash
mvn clean test -Dheadless=true
mvn allure:report
```

If Allure shows only one browser while the total count is `28`, check whether
the `Retry` filter is enabled in the report UI. In the cross-browser run, Chrome
and Firefox should appear as separate groups with `14` tests each.

---

# 12. GitHub Actions

Workflow file:

```text
.github/workflows/ui-tests.yml
```

The workflow runs on:

- Push to `main`
- Push to `develop`
- Pull request
- Manual execution using `workflow_dispatch`

## CI parallelism

GitHub Actions uses a matrix:

```yaml
matrix:
  browser: [chrome, firefox]
```

This creates two independent jobs:

```text
Chrome job  ─┐
             ├─ run in parallel
Firefox job ─┘
```

Each job executes:

```bash
mvn -B clean test \
  -Psingle-browser \
  -Dbrowser=<chrome|firefox> \
  -Dheadless=true
```

This provides browser isolation at the CI worker level.

---

# 13. How to run GitHub Actions manually

1. Create a GitHub repository.
2. Push this project to the repository.
3. Open the repository on GitHub.
4. Open the **Actions** tab.
5. Select **SauceDemo UI Tests**.
6. Click **Run workflow**.
7. Select the branch.
8. Click **Run workflow**.

GitHub will start Chrome and Firefox matrix jobs.

---

# 14. How to push the project to GitHub

Create a new empty GitHub repository, then run:

```bash
git init
git add .
git commit -m "Add SauceDemo QA automation homework"
git branch -M main
git remote add origin <your-repository-url>
git push -u origin main
```

The `push` to `main` automatically triggers the GitHub Actions workflow.

---

# 15. Download CI reports and screenshots

After a GitHub Actions run:

1. Open **Actions**.
2. Select the workflow run.
3. Scroll to **Artifacts**.
4. Download:
   - `allure-report-chrome`
   - `allure-report-firefox`
5. Extract the artifact ZIP.
6. Open `index.html`.
7. If a job fails and a screenshot exists, download:
   - `screenshots-chrome`
   - or `screenshots-firefox`

The CI report artifact is generated with Allure's `--single-file` option, so
its `index.html` can be opened directly after download. If you generate the
default multi-file report locally, serve it through `mvn allure:serve` or
`python3 -m http.server` instead of opening the file directly.

---

# 16. Configuration

Default values:

```properties
base.url=https://www.saucedemo.com/
explicit.wait.seconds=10
page.load.timeout.seconds=30
performance.login.max.seconds=10
headless=false
```

They can be overridden with JVM system properties.

Examples:

```bash
mvn test -Dheadless=true
mvn test -Dbase.url=https://www.saucedemo.com/
mvn test -Dgroups=regression -Dheadless=true
mvn test -Psingle-browser -Dbrowser=firefox
```

---

# 17. Design decisions

## Page Object Model

Selectors and page actions are kept outside tests.

This makes the tests easier to read and reduces maintenance when the UI changes.

## Explicit waits

The project uses `WebDriverWait` instead of `Thread.sleep()`.

Benefits:

- Faster tests.
- Less unnecessary waiting.
- Reduced flakiness.
- Better synchronization with the UI.

## Thread-safe WebDriver

`ThreadLocal<WebDriver>` is used so parallel browser sessions do not share the same
driver instance.

## Selenium Manager

The project does not hardcode driver executables.

Selenium Manager can resolve browser drivers automatically for supported local
browser installations.

## Data-driven negative login testing

TestNG `@DataProvider` keeps multiple validation scenarios maintainable and avoids
duplicating test methods.

## User-specific SauceDemo coverage

Each provided SauceDemo user is tested against the behavior it is designed to
represent. `standard_user` is the baseline, while the other users validate
locked, problem, performance, error, and visual scenarios separately.

## CI browser matrix

Local execution demonstrates TestNG parallel execution.

GitHub Actions uses matrix parallelization for stronger isolation and easier
troubleshooting per browser.

---

# 18. Possible improvements for a production framework

For a larger real-world project I would consider adding:

- API utilities for test setup/teardown.
- Environment-specific configuration.
- Secrets management.
- Docker/Selenium Grid for distributed execution.
- Test retry only for proven infrastructure failures.
- Browser console/network evidence.
- Video recording for failed CI tests.
- Test sharding based on historical execution duration.
- Centralized test-data management.
- Flaky-test tracking and quarantine policy.

These are intentionally not required for this small homework because the goal is
to keep the solution easy to review and run.

---

# 19. Troubleshooting

## Chrome or Firefox is not installed

Install the browser before executing the corresponding suite.

## Driver cannot be downloaded

Selenium Manager may require network access the first time it resolves a driver.
Check network/proxy restrictions.

## CI test is slow

Use headless mode:

```bash
mvn test -Dheadless=true
```

## A test fails

Check:

```text
target/allure-results/
target/site/allure-maven-plugin/
target/screenshots/
```

The screenshot filename contains the test method and browser. The same
screenshot is attached inside the Allure test result.

---

# 20. Quick review commands

Cross-browser parallel:

```bash
mvn test -Pcross-browser -Dheadless=true
```

Chrome only:

```bash
mvn test -Psingle-browser -Dbrowser=chrome -Dheadless=true
```

Firefox only:

```bash
mvn test -Psingle-browser -Dbrowser=firefox -Dheadless=true
```

Regression parallel:

```bash
mvn test -Pcross-browser -Dgroups=regression -Dheadless=true
```

Regression Chrome only:

```bash
mvn test -Psingle-browser -Dbrowser=chrome -Dgroups=regression -Dheadless=true
```

Regression Firefox only:

```bash
mvn test -Psingle-browser -Dbrowser=firefox -Dgroups=regression -Dheadless=true
```

Generate Allure report after a test run:

```bash
mvn allure:report
```

Serve Allure report locally:

```bash
mvn allure:serve
```

---

## Notes for the reviewer

This implementation intentionally separates:

- browser lifecycle,
- configuration,
- page objects,
- test logic,
- failure evidence,
- local parallelism,
- and CI parallelism.

The goal is to keep the homework small enough to review while demonstrating a
framework structure that can scale beyond two UI test classes.
