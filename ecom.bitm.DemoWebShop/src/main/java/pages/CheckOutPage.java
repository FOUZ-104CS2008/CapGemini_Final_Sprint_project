
package pages;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckOutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Login
    private final By emailField = By.id("Email");
    private final By passwordField = By.id("Password");
    private final By loginButton =
            By.cssSelector("input.button-1.login-button");
    private final By accountLink =
            By.cssSelector(".header-links .account");
    private final By loginError =
            By.cssSelector(".validation-summary-errors, .message-error");

    // Billing
    private final By firstName = By.id("BillingNewAddress_FirstName");
    private final By lastName = By.id("BillingNewAddress_LastName");
    private final By billingEmail = By.id("BillingNewAddress_Email");
    private final By country = By.id("BillingNewAddress_CountryId");
    private final By city = By.id("BillingNewAddress_City");
    private final By address = By.id("BillingNewAddress_Address1");
    private final By zip = By.id("BillingNewAddress_ZipPostalCode");
    private final By phone = By.id("BillingNewAddress_PhoneNumber");

    // Continue buttons
    private final By billingContinue =
            By.cssSelector("#billing-buttons-container input.button-1");

    private final By shippingAddressContinue =
            By.cssSelector("#shipping-buttons-container input.button-1");

    private final By shippingMethodContinue =
            By.cssSelector("#shipping-method-buttons-container input.button-1");

    private final By paymentMethodContinue =
            By.cssSelector("#payment-method-buttons-container input.button-1");

    private final By paymentInfoContinue =
            By.cssSelector("#payment-info-buttons-container input.button-1");

    private final By confirmOrderButton =
            By.cssSelector(
                "#confirm-order-buttons-container "
                + "input.confirm-order-next-step-button, "
                + "#confirm-order-buttons-container "
                + "button.confirm-order-next-step-button, "
                + "#confirm-order-buttons-container input.button-1"
            );

    // Other elements
    private final By orderCompleted =
            By.cssSelector(".section.order-completed");

    private final By confirmOrderStep =
            By.id("checkout-step-confirm-order");

    private final By termsCheckbox = By.id("termsofservice");
    private final By checkoutButton = By.id("checkout");
    private final By cartItems = By.cssSelector(".cart tbody tr");

    public CheckOutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // 1. LOGIN
    public void login(String userEmail, String userPassword) {

        driver.get("https://demowebshop.tricentis.com/login");

        fill(emailField, userEmail);
        fill(passwordField, userPassword);

        wait.until(ExpectedConditions.elementToBeClickable(loginButton))
                .click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(accountLink),
                ExpectedConditions.visibilityOfElementLocated(loginError)
        ));

        if (!isVisible(accountLink)) {
            throw new RuntimeException(
                    "Login failed. Check email and password.");
        }

        System.out.println("Login successful.");
    }

    // 2. ADD BOOK TO CART
    public void addBookToCart() {

        driver.get("https://demowebshop.tricentis.com/books");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Computing and Internet"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input.button-1.add-to-cart-button")))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".bar-notification")));

        System.out.println("Book added to cart.");
    }

    // 3. OPEN CART AND CHECKOUT
    public void openCartAndCheckout() {

        driver.get("https://demowebshop.tricentis.com/cart");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".cart")));

        wait.until(d -> {
            List<WebElement> rows = d.findElements(cartItems);

            for (WebElement row : rows) {
                if (row.isDisplayed()
                        && !row.getText().toLowerCase().contains("no items")) {
                    return true;
                }
            }
            return false;
        });

        List<WebElement> terms = driver.findElements(termsCheckbox);

        if (!terms.isEmpty() && terms.get(0).isDisplayed()) {
            WebElement checkbox = terms.get(0);

            if (!checkbox.isSelected()) {
                try {
                    checkbox.click();
                } catch (ElementClickInterceptedException e) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", checkbox);
                }
            }
        }

        WebElement checkout = wait.until(d -> {
            for (WebElement button : d.findElements(checkoutButton)) {
                if (button.isDisplayed() && button.isEnabled()) {
                    return button;
                }
            }
            return null;
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", checkout);

        try {
            checkout.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", checkout);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(firstName),
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("billing-address-select"))
        ));

        System.out.println("Checkout page loaded.");
    }

    // 4. ENTER BILLING DETAILS
    public void enterBillingDetails(
            String fname,
            String lname,
            String emailValue,
            String countryValue,
            String cityValue,
            String addressValue,
            String zipValue,
            String phoneValue) {

        List<WebElement> dropdowns =
                driver.findElements(By.id("billing-address-select"));

        if (!dropdowns.isEmpty() && dropdowns.get(0).isDisplayed()) {
            new Select(dropdowns.get(0)).selectByVisibleText("New Address");
        }

        fill(firstName, fname);
        fill(lastName, lname);
        fill(billingEmail, emailValue);
        fill(city, cityValue);
        fill(address, addressValue);
        fill(zip, zipValue);
        fill(phone, phoneValue);

        new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(country)))
                .selectByVisibleText(countryValue);

        System.out.println("Billing details entered.");
    }

    // COMMON FIELD ENTRY
    private void fill(By locator, String value) {

        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));

        element.clear();

        if (value != null && !value.trim().isEmpty()) {
            element.sendKeys(value);
        }
    }

    // WAIT FOR ONE EXPECTED STEP
    private void waitForStep(By locator) {

        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException(
                    "Checkout step not displayed: " + locator, e);
        }
    }

    // 5. CONTINUE BILLING
    public void continueBilling() {

        clickSafely(billingContinue);

        waitForStep(shippingAddressContinue);

        System.out.println("Billing step completed.");
    }

    // 6. CONTINUE SHIPPING ADDRESS
    public void continueShippingAddress() {

        if (isVisible(shippingAddressContinue)) {
            clickSafely(shippingAddressContinue);
            waitForStep(shippingMethodContinue);
        }

        System.out.println("Shipping address step completed.");
    }

    // 7. CONTINUE SHIPPING METHOD
    public void continueShippingMethod() {

        By groundOption = By.id("shippingoption_0");

        if (isVisible(groundOption)) {
            WebElement ground = wait.until(
                    ExpectedConditions.elementToBeClickable(groundOption));

            if (!ground.isSelected()) {
                ground.click();
            }

            clickSafely(shippingMethodContinue);
            waitForStep(paymentMethodContinue);
        }

        System.out.println("Shipping method step completed.");
    }

    // 8. SELECT PAYMENT METHOD
    public void selectPaymentMethod(String method) {

        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is empty.");
        }

        String value;

        switch (method.trim().toLowerCase(Locale.ROOT)) {
            case "cash on delivery":
            case "cod":
                value = "Payments.CashOnDelivery";
                break;

            case "check / money order":
            case "check":
            case "money order":
                value = "Payments.CheckMoneyOrder";
                break;

            case "credit card":
                value = "Payments.Manual";
                break;

            case "purchase order":
                value = "Payments.PurchaseOrder";
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported payment method: " + method);
        }

        By radioLocator = By.cssSelector(
                "input[name='paymentmethod'][value='" + value + "']");

        WebElement radio = wait.until(
                ExpectedConditions.elementToBeClickable(radioLocator));

        if (!radio.isSelected()) {
            radio.click();
        }

        System.out.println("Payment method selected: " + method);
    }

    // 9. CONTINUE PAYMENT METHOD
    public void clickPaymentMethodContinue() {

        clickSafely(paymentMethodContinue);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("payment-info-form")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("CardholderName")),
                ExpectedConditions.visibilityOfElementLocated(
                        paymentInfoContinue),
                ExpectedConditions.visibilityOfElementLocated(
                        confirmOrderButton),
                ExpectedConditions.visibilityOfElementLocated(
                        orderCompleted)
        ));

        System.out.println("Payment method step completed.");
    }

    // 10. ENTER CREDIT CARD DETAILS
    public void enterCreditCardDetails(
            String cardholderName,
            String cardNumber,
            String expirationMonth,
            String expirationYear,
            String cardCode) {

        fill(By.id("CardholderName"), cardholderName);
        fill(By.id("CardNumber"), cardNumber);

        new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("ExpireMonth"))))
                .selectByVisibleText(expirationMonth);

        new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("ExpireYear"))))
                .selectByVisibleText(expirationYear);

        fill(By.id("CardCode"), cardCode);

        System.out.println("Credit card details entered.");
    }

    // 11. PURCHASE ORDER FIELD IF PRESENT
    public void enterPurchaseOrderDetailsIfPresent() {

        By purchaseOrderNumber = By.id("PurchaseOrderNumber");

        List<WebElement> fields =
                driver.findElements(purchaseOrderNumber);

        if (!fields.isEmpty() && fields.get(0).isDisplayed()) {
            fields.get(0).clear();
            fields.get(0).sendKeys("PO-TEST-1001");

            System.out.println("Purchase Order number entered.");
        } else {
            System.out.println(
                    "Purchase Order number field is not present.");
        }
    }

    // 12. CONTINUE PAYMENT INFORMATION
    public void continuePaymentInfo() {

        if (isVisible(paymentInfoContinue)) {
            clickSafely(paymentInfoContinue);
            waitForStep(confirmOrderStep);
        }

        System.out.println("Payment information step completed.");
    }

    // 13. CONFIRM ORDER
    public void confirmOrder() {

        if (isOrderCompleted()) {
            return;
        }

        waitForStep(confirmOrderStep);
        clickSafely(confirmOrderButton);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("checkout/completed"),
                ExpectedConditions.visibilityOfElementLocated(orderCompleted)
        ));

        System.out.println("Order placed successfully.");
    }

    // 14. VERIFY ORDER
    public boolean isOrderCompleted() {

        return driver.getCurrentUrl().contains("checkout/completed")
                || isVisible(orderCompleted);
    }

    // 15. SAFE CLICK
    private void clickSafely(By locator) {

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element);

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
        }
    }

    // 16. CHECK VISIBILITY
    private boolean isVisible(By locator) {

        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (NoSuchElementException
                    | StaleElementReferenceException ignored) {
                // Element may disappear during a page update.
            }
        }

        return false;
    }
}
