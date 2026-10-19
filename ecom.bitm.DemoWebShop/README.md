# Demo Web Shop Registration and Login Maven Project

This project follows the uploaded registration test-data layout and includes a valid login and one negative login test.

## Registration sheet
- REG01: Invalid email format
- REG02: Password and confirm-password mismatch
- REG03: Required fields left blank
- REG04: Valid registration (male)
- REG05: Valid registration (female)
- REG06: Existing/duplicate email

## Login sheet
- LOG01: Valid login
- LOG02: Existing email with incorrect password

## Important
1. To pass REG04 and REG05, use email addresses that are not already registered on Demo Web Shop. The values shown are examples and may already exist.
2. REG06 is intended to use an email that already exists.
3. LOG01 succeeds only when `Fouziya@gmail.com` and `Fouz@123` are the actual credentials for an existing account. If the password differs, edit the Login sheet with the correct password.
4. Import into Eclipse using File > Import > Maven > Existing Maven Projects, then Maven > Update Project, then run `testng.xml` as a TestNG Suite.


## Shopping cart test (Book then Jewelry)
`testcases.ShoppingCartTest` reads the `ShoppingCart` sheet in `src/test/resources/testdata/TestData.xlsx`.
It runs twice in sequence:
1. Logs in with the first account, opens **Books**, adds the first listed book, and verifies the product in the cart.
2. Starts a fresh browser session, logs in with the second account, opens **Jewelry**, adds the first listed jewelry product, and verifies the product in the cart.

Before running, replace `YOUR_SECOND_REGISTERED_EMAIL` and `YOUR_SECOND_ACCOUNT_PASSWORD` in the Excel `ShoppingCart` sheet with a real, already-registered account. Confirm the first account credentials are also valid. Run `mvn clean test` or run `testng.xml` in Eclipse.
