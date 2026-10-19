
package testcases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseClass;
import pages.RegistrationPage;
import utilities.ExcelUtils;

public class RegistrationTest extends BaseClass {

    // Store generated emails so the duplicate test can reuse them.
    private static final Map<String, String> generatedEmails =
            new HashMap<>();

    @DataProvider(name = "registrationData")
    public Object[][] data() {
        return ExcelUtils.getData("Registration")
                .toArray(new Object[0][]);
    }

    @Test(dataProvider = "registrationData", groups= {"ST", "FT", "IT", "SYS"})
    public void registrationTest(
            String id,
            String type,
            String gender,
            String first,
            String last,
            String email,
            String password,
            String confirm,
            String expected) {

        RegistrationPage page = new RegistrationPage(
                driver,
                Long.parseLong(
                        prop.getProperty("explicitWait", "10"))
        );

        // Generate a unique email for each valid registration.
        if ("Valid".equalsIgnoreCase(type)) {

            email = "fouz"
                    + LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "yyyyMMddHHmmssSSS"))
                    + "@example.com";

            generatedEmails.put(id, email);

            System.out.println(
                    id + " generated email: " + email);
        }

        // Reuse the email registered by REG04 for REG05.
        if ("Duplicate".equalsIgnoreCase(type)
                && "REG05".equalsIgnoreCase(id)) {

            String registeredEmail = generatedEmails.get("REG04");

            if (registeredEmail != null) {
                email = registeredEmail;
            }
        }

        page.open();

        page.fill(
                gender,
                first,
                last,
                email,
                password,
                confirm
        );

        page.submit();

        String actualMessage = page.messages();
        String message = actualMessage.toLowerCase();

        System.out.println(
                id + " | Type: " + type
                + " | Email: " + email
                + " | Actual message: " + actualMessage
        );

        // VALID REGISTRATION
        if ("Valid".equalsIgnoreCase(type)) {

            Assert.assertTrue(
                    page.isSuccess(),
                    id + " expected successful registration. Actual: "
                            + actualMessage
            );
        }

        // DUPLICATE EMAIL REGISTRATION
        else if ("Duplicate".equalsIgnoreCase(type)) {

            Assert.assertFalse(
                    page.isSuccess(),
                    id + " should reject duplicate registration"
            );

            Assert.assertTrue(
                    message.contains("exist")
                            || message.contains("already"),
                    id + " expected duplicate-email message. Actual: "
                            + actualMessage
            );
        }

        // NEGATIVE REGISTRATION
        else if ("Negative".equalsIgnoreCase(type)) {

            Assert.assertFalse(
                    page.isSuccess(),
                    id + " should not register successfully"
            );

            Assert.assertFalse(
                    actualMessage.trim().isEmpty(),
                    id + " expected a validation message"
            );

            // Invalid email
            if (email != null
                    && !email.trim().isEmpty()
                    && !email.contains("@")) {

                Assert.assertTrue(
                        message.contains("email")
                                || message.contains("valid"),
                        id + " expected invalid-email validation. Actual: "
                                + actualMessage
                );
            }

            // Password mismatch
            if (password != null
                    && confirm != null
                    && !password.equals(confirm)) {

                Assert.assertTrue(
                        message.contains("password")
                                && (message.contains("match")
                                || message.contains("confirm")),
                        id + " expected password-mismatch validation. Actual: "
                                + actualMessage
                );
            }
        }

        else {
            Assert.fail(
                    id + " has an unknown test type: " + type
            );
        }
    }
}
