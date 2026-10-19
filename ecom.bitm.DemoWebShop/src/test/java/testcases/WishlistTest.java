
package testcases;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.WishlistPage;
import utilities.ExcelUtils;

public class WishlistTest {

    // Login using the jewelry account from Excel
    public void login(WebDriver driver, WebDriverWait wait) {

        List<Object[]> data = ExcelUtils.getData("ShoppingCart");

        // Second data row, as in your existing code
        Object[] row = data.get(1);

        String email = row[2].toString();
        String password = row[3].toString();

        driver.get("https://demowebshop.tricentis.com/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("Email"))).sendKeys(email);

        driver.findElement(By.id("Password")).sendKeys(password);

        driver.findElement(By.cssSelector("input.login-button")).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("a.ico-logout")));
    }

    // TEST CASE 1: Add jewelry to Wishlist
    @Test(priority = 1,groups={"ST", "FT", "IT", "SYS"})
    public void addJewelryToWishlist() {

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            login(driver, wait);

            WishlistPage wishlistPage = new WishlistPage(driver);

            wishlistPage.openJewelryProduct();

            String name = wishlistPage.getProductName();

            Assert.assertTrue(
                    name.contains("Black & White Diamond Heart"),
                    "Incorrect jewelry product opened");

            String message = wishlistPage.addJewelryToWishlist();

            Assert.assertTrue(
                    message.toLowerCase().contains("wishlist"),
                    "Wishlist confirmation message was not displayed: "
                            + message);

            System.out.println("PASS | Jewelry added to Wishlist");
            System.out.println("Product: " + name);
            System.out.println("Message: " + message);

        } finally {
            driver.quit();
        }
    }

    // TEST CASE 2: Remove jewelry from Wishlist
    @Test(
        priority = 2,
        dependsOnMethods = "addJewelryToWishlist",groups={"ST", "FT", "SYS"}
    )
    public void removeJewelryFromWishlist() {

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Log in using the same account
            login(driver, wait);

            WishlistPage wishlistPage = new WishlistPage(driver);

            // Open Wishlist
            wishlistPage.openWishlist();

            // Verify jewelry exists before removing it
            Assert.assertTrue(
                    wishlistPage.isJewelryPresent(
                            "Black & White Diamond Heart"),
                    "Jewelry was not found in Wishlist");

            System.out.println("PASS | Jewelry found in Wishlist");

            // Remove jewelry
            wishlistPage.removeJewelryFromWishlist();

            // Verify product is no longer listed
            Assert.assertFalse(
                    wishlistPage.isJewelryPresent(
                            "Black & White Diamond Heart"),
                    "Jewelry was not removed from Wishlist");

            System.out.println("PASS | Jewelry removed from Wishlist");

        } finally {
            driver.quit();
        }
    }
}
