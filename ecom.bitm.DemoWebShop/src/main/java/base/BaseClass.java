
package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseClass {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Properties prop;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("") String browserFromXml) {

        prop = new Properties();

        String path = System.getProperty("user.dir")
                + "/src/test/resources/testdata/common.properties";

        try (FileInputStream fis = new FileInputStream(path)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot load properties file: " + path, e);
        }

        // XML browser parameter takes priority over properties file.
        String browser;

        if (browserFromXml != null && !browserFromXml.trim().isEmpty()) {
            browser = browserFromXml.trim().toLowerCase();
        } else {
            browser = prop.getProperty("browser", "chrome")
                    .trim().toLowerCase();
        }

        // Launch browser.
        switch (browser) {

            case "chrome": {
                ChromeOptions options = new ChromeOptions();

                options.addArguments("--start-maximized");

                options.setExperimentalOption("prefs", Map.of(
                        "autofill.profile_enabled", false,
                        "autofill.payment_methods_enabled", false,
                        "autofill.credit_card_enabled", false,
                        "profile.password_manager_enabled", false
                ));

                driver = new ChromeDriver(options);
                break;
            }

            case "edge":
                driver = new EdgeDriver();
                break;

            case "firefox":
                driver = new FirefoxDriver();
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();

        // Implicit wait.
        long implicitWait = Long.parseLong(
                prop.getProperty("implicitWait", "2"));

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(implicitWait));

        // Explicit wait.
        long explicitWait = Long.parseLong(
                prop.getProperty("explicitWait", "10"));

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(explicitWait));

        // Application URL.
        String url = prop.getProperty("url");

        if (url == null || url.trim().isEmpty()) {
            driver.quit();
            driver = null;

            throw new IllegalStateException(
                    "URL is missing in common.properties");
        }

        driver.get(url.trim());

        System.out.println("=================================");
        System.out.println("Browser launched: " + browser);
        System.out.println("Application URL: " + url);
        System.out.println("=================================");
    }

    // Take screenshot.
    public void takeScreenshot(String fileName) throws IOException {

        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialized");
        }

        Path folder = Path.of("screenshots");
        Files.createDirectories(folder);

        File source = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

        Path destination = folder.resolve(fileName + ".png");

        Files.copy(
                source.toPath(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println(
                "Screenshot saved: " + destination.toAbsolutePath());
    }

    // Screenshot after every test and close browser.
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if (driver == null) {
            return;
        }

        try {
            String status;

            if (result.getStatus() == ITestResult.FAILURE) {
                status = "FAIL";
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                status = "PASS";
            } else {
                status = "SKIP";
            }

            String fileName =
                    result.getMethod().getMethodName()
                    + "_" + status
                    + "_" + System.currentTimeMillis();

            takeScreenshot(fileName);

        } catch (Exception e) {
            System.out.println(
                    "Screenshot error: " + e.getMessage());

        } finally {
            driver.quit();
            driver = null;
            wait = null;

            System.out.println("Browser closed");
        }
    }
}
