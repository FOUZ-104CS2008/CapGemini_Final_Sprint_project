
package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ShoppingCartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By productTitle =
            By.cssSelector("div.product-name h1");

    private final By addButton =
            By.cssSelector("input.add-to-cart-button");

    private final By cartRows =
            By.cssSelector("table.cart tbody tr");

    private final By notification =
            By.cssSelector(".bar-notification");

    public ShoppingCartPage(WebDriver driver, long timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(timeout));
    }

    public void openBooksCategory() {
        driver.get("https://demowebshop.tricentis.com/books");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".product-grid, .product-list")));
    }

    public void testBooksDropdowns() {
        Select sort = new Select(
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("products-orderby"))));

        sort.selectByVisibleText("Name: A to Z");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("products-pagesize")));

        Select display = new Select(
                driver.findElement(By.id("products-pagesize")));

        display.selectByVisibleText("8");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("products-viewmode")));

        Select view = new Select(
                driver.findElement(By.id("products-viewmode")));

        view.selectByVisibleText("List");

        System.out.println(
                "BOOKS UI PASS | Sort=Name: A to Z | Display=8 | View=List");
    }

    public void openCategory(String category) {
        if (category.equalsIgnoreCase("Books")) {
            driver.get(
                    "https://demowebshop.tricentis.com/computing-and-internet");

        } else if (category.equalsIgnoreCase("Jewelry")) {
            driver.get(
                    "https://demowebshop.tricentis.com/black-white-diamond-heart");

        } else {
            throw new IllegalArgumentException(
                    "Unknown category: " + category);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                productTitle));
    }

    public String addFirstProductToCart() {
        String name = wait.until(
                ExpectedConditions.visibilityOfElementLocated(productTitle))
                .getText().trim();

        By button;

        if (driver.getCurrentUrl().contains(
                "black-white-diamond-heart")) {
            button = By.id("add-to-cart-button-14");
        } else {
            button = addButton;
        }

        wait.until(ExpectedConditions.elementToBeClickable(button)).click();

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(
                            notification),
                    ExpectedConditions.urlContains("/cart")));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Add to cart did not complete for: " + name
                    + ". Current URL: " + driver.getCurrentUrl(), e);
        }

        System.out.println("ADD TO CART CLICKED | " + name);

        return name;
    }

    public boolean cartContains(String productName) {
        driver.get("https://demowebshop.tricentis.com/cart");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(cartRows),
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".order-summary-content"))));

        List<WebElement> rows = driver.findElements(cartRows);

        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(
                    productName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
