package testcases;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.BaseClass;
import utilities.ExcelUtils;

public class ShoppingCartTest extends BaseClass {

    private List<Object[]> data;
    private Object[] bookRow;
    private Object[] jewelryRow;

    @BeforeClass(alwaysRun = true)
    public void loadShoppingCartData() {

        data = ExcelUtils.getData("ShoppingCart");

        Assert.assertNotNull(
                data,
                "ShoppingCart Excel sheet returned null");

        Assert.assertTrue(
                data.size() >= 2,
                "ShoppingCart sheet must contain at least 2 data rows");

        bookRow = data.get(0);
        jewelryRow = data.get(1);

        Assert.assertTrue(
                bookRow != null && bookRow.length >= 5,
                "CART01 row must contain at least 5 columns");

        Assert.assertTrue(
                jewelryRow != null && jewelryRow.length >= 5,
                "CART02 row must contain at least 5 columns");

        System.out.println("Book row loaded: "
                + Arrays.toString(bookRow));

        System.out.println("Jewelry row loaded: "
                + Arrays.toString(jewelryRow));
    }

    private void login(String email, String password) {

        driver.get("https://demowebshop.tricentis.com/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("Email"))).sendKeys(email);

        driver.findElement(By.id("Password")).sendKeys(password);

        driver.findElement(
                By.cssSelector("input.login-button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a.ico-logout")));
    }

    private void selectOption(By locator, String option) {

        driver.get("https://demowebshop.tricentis.com/books");

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(locator));

        new Select(dropdown).selectByVisibleText(option);

        wait.until(d -> {
            Select current = new Select(d.findElement(locator));
            return current.getFirstSelectedOption()
                    .getText().trim().equals(option);
        });

        System.out.println("PASS | Selected option: " + option);
    }

    @Test(priority = 1, groups = {"FT"})
    public void sortByPosition() {
        selectOption(By.id("products-orderby"), "Position");
    }

    @Test(priority = 2)
    public void sortNameAToZ() {
        selectOption(By.id("products-orderby"), "Name: A to Z");
    }

    @Test(priority = 3)
    public void sortNameZToA() {
        selectOption(By.id("products-orderby"), "Name: Z to A");
    }

    @Test(priority = 4)
    public void sortPriceLowToHigh() {
        selectOption(By.id("products-orderby"), "Price: Low to High");
    }

    @Test(priority = 5)
    public void sortPriceHighToLow() {
        selectOption(By.id("products-orderby"), "Price: High to Low");
    }

    @Test(priority = 6)
    public void sortByCreatedOn() {
        selectOption(By.id("products-orderby"), "Created on");
    }

    @Test(priority = 7)
    public void displayFourProducts() {
        selectOption(By.id("products-pagesize"), "4");
    }

    @Test(priority = 8)
    public void displayEightProducts() {
        selectOption(By.id("products-pagesize"), "8");
    }

    @Test(priority = 9)
    public void displayTwelveProducts() {
        selectOption(By.id("products-pagesize"), "12");
    }

    @Test(priority = 10)
    public void viewAsGrid() {
        selectOption(By.id("products-viewmode"), "Grid");
    }

    @Test(priority = 11)
    public void viewAsList() {
        selectOption(By.id("products-viewmode"), "List");
    }

    @Test(priority = 12, groups = {"ST", "FT", "IT", "SYS"})
    public void addBookToCart() {

        Assert.assertNotNull(bookRow, "bookRow was not loaded");

        String tcId = bookRow[0].toString();
        String email = bookRow[2].toString();
        String password = bookRow[3].toString();
        String expected = bookRow[4].toString();

        login(email, password);

        driver.get(
                "https://demowebshop.tricentis.com/computing-and-internet");

        String product = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.product-name h1")))
                .getText().trim();

        Assert.assertTrue(
                product.contains("Computing and Internet"),
                "Unexpected book product: " + product);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input.add-to-cart-button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".bar-notification")));

        driver.get("https://demowebshop.tricentis.com/cart");

        boolean found = wait.until(d -> {
            List<WebElement> rows = d.findElements(
                    By.cssSelector("table.cart tbody tr"));

            return rows.stream().anyMatch(row ->
                    row.getText().toLowerCase()
                            .contains("computing and internet"));
        });

        Assert.assertTrue(
                found,
                "Book not found in cart. Expected: " + expected);

        System.out.println("PASS | " + tcId + " | Book added to cart");
    }

    @Test(priority = 13, groups = {"ST", "FT", "IT", "SYS"})
    public void addJewelryToCart() {

        Assert.assertNotNull(jewelryRow, "jewelryRow was not loaded");

        String tcId = jewelryRow[0].toString();
        String email = jewelryRow[2].toString();
        String password = jewelryRow[3].toString();
        String expected = jewelryRow[4].toString();

        // Each test gets its own browser from BaseClass.
        // Log in directly using the second account.
        login(email, password);

        driver.get(
                "https://demowebshop.tricentis.com/black-white-diamond-heart");

        String product = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.product-name h1")))
                .getText().trim();

        Assert.assertTrue(
                product.contains("Black & White Diamond Heart"),
                "Unexpected jewelry product: " + product);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-button-14"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".bar-notification")));

        driver.get("https://demowebshop.tricentis.com/cart");

        boolean found = wait.until(d -> {
            List<WebElement> rows = d.findElements(
                    By.cssSelector("table.cart tbody tr"));

            return rows.stream().anyMatch(row ->
                    row.getText().toLowerCase().contains(
                            "black & white diamond heart"));
        });

        Assert.assertTrue(
                found,
                "Jewelry not found in cart. Expected: " + expected);

        System.out.println(
                "PASS | " + tcId + " | Jewelry added to cart");
    }
}