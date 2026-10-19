
package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WishlistPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By productName =
            By.cssSelector("div.product-name h1");

    private By addToWishlistButton =
            By.id("add-to-wishlist-button-14");

    private By notification =
            By.id("bar-notification");

    private By wishlistLink =
            By.cssSelector("a.ico-wishlist");

    private By wishlistProductRow =
            By.cssSelector("table.cart tbody tr");

    private By removeCheckbox =
            By.cssSelector(
                "table.cart tbody tr .remove-from-cart input");

    private By updateWishlistButton =
            By.name("updatecart");

    public WishlistPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Open jewelry product
    public void openJewelryProduct() {
        driver.get(
            "https://demowebshop.tricentis.com/black-white-diamond-heart");
    }

    // Get product name
    public String getProductName() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(productName))
                .getText().trim();
    }

    // Add jewelry to Wishlist
    public String addJewelryToWishlist() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addToWishlistButton)).click();

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(notification));

        return message.getText().trim();
    }

    // Open Wishlist
    public void openWishlist() {
        wait.until(
                ExpectedConditions.elementToBeClickable(wishlistLink)).click();

        wait.until(ExpectedConditions.urlContains("wishlist"));
    }

    // Check whether jewelry exists in Wishlist
    public boolean isJewelryPresent(String expectedName) {
        try {
            WebElement row = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            wishlistProductRow));

            return row.getText().contains(expectedName);
        } catch (Exception e) {
            return false;
        }
    }

    // Remove jewelry from Wishlist
    public void removeJewelryFromWishlist() {
        wait.until(
                ExpectedConditions.elementToBeClickable(removeCheckbox)).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        updateWishlistButton)).click();

        // Wait until the product row is removed
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        wishlistProductRow));
    }
}
