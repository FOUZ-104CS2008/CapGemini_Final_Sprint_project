package testcases;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseClass;
import pages.CheckOutPage;
import utilities.ExcelUtils;

public class CheckOutTest extends BaseClass {

    @DataProvider(name = "checkoutData")
    public Object[][] checkoutData() {

        List<Object[]> rows = ExcelUtils.getData("Checkout");

        Assert.assertNotNull(
                rows,
                "Checkout Excel sheet returned null");

        Assert.assertFalse(
                rows.isEmpty(),
                "Checkout Excel sheet has no data rows");

        List<Object[]> validRows = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {

            Object[] row = rows.get(i);

            if (row == null || row.length < 10) {
                throw new IllegalArgumentException(
                        "Checkout Excel row " + (i + 2)
                        + " has fewer than 10 columns. Found: "
                        + (row == null ? 0 : row.length));
            }

            Object[] values = new Object[10];

            for (int j = 0; j < 10; j++) {
                values[j] = row[j] == null
                        ? ""
                        : row[j].toString().trim();
            }

            validRows.add(values);
        }

        return validRows.toArray(new Object[0][]);
    }

    private void performCheckout(
            String email,
            String password,
            String firstName,
            String lastName,
            String billingEmail,
            String country,
            String city,
            String address,
            String zip,
            String phone,
            String paymentMethod) {

        CheckOutPage checkout = new CheckOutPage(driver);

        checkout.login(email, password);
        checkout.addBookToCart();
        checkout.openCartAndCheckout();

        checkout.enterBillingDetails(
                firstName,
                lastName,
                billingEmail,
                country,
                city,
                address,
                zip,
                phone);

        checkout.continueBilling();
        checkout.continueShippingAddress();
        checkout.continueShippingMethod();

        checkout.selectPaymentMethod(paymentMethod);
        checkout.clickPaymentMethodContinue();

        if (paymentMethod.equalsIgnoreCase("Credit Card")) {
            checkout.enterCreditCardDetails(
                    "Test User",
                    "4111111111111111",
                    "01",
                    "2028",
                    "123");
        }

        checkout.continuePaymentInfo();
        checkout.confirmOrder();

        Assert.assertTrue(
                checkout.isOrderCompleted(),
                "Checkout failed using " + paymentMethod);

        System.out.println(
                "PASS | Checkout successful using " + paymentMethod);
    }

    @Test(dataProvider = "checkoutData",
          groups = {"ST", "FT", "IT", "SYS"})
    public void checkoutUsingCashOnDelivery(
            String email,
            String password,
            String firstName,
            String lastName,
            String billingEmail,
            String country,
            String city,
            String address,
            String zip,
            String phone) {

        performCheckout(
                email, password,
                firstName, lastName,
                billingEmail, country,
                city, address, zip, phone,
                "Cash On Delivery");
    }

    @Test(dataProvider = "checkoutData",
          groups = {"FT", "IT", "SYS"})
    public void checkoutUsingCheckMoneyOrder(
            String email,
            String password,
            String firstName,
            String lastName,
            String billingEmail,
            String country,
            String city,
            String address,
            String zip,
            String phone) {

        performCheckout(
                email, password,
                firstName, lastName,
                billingEmail, country,
                city, address, zip, phone,
                "Check / Money Order");
    }

    @Test(dataProvider = "checkoutData",
          groups = {"FT", "IT", "SYS"})
    public void checkoutUsingCreditCard(
            String email,
            String password,
            String firstName,
            String lastName,
            String billingEmail,
            String country,
            String city,
            String address,
            String zip,
            String phone) {

        performCheckout(
                email, password,
                firstName, lastName,
                billingEmail, country,
                city, address, zip, phone,
                "Credit Card");
    }

    @Test(dataProvider = "checkoutData",
          groups = {"FT", "IT", "SYS"})
    public void checkoutUsingPurchaseOrder(
            String email,
            String password,
            String firstName,
            String lastName,
            String billingEmail,
            String country,
            String city,
            String address,
            String zip,
            String phone) {

        performCheckout(
                email, password,
                firstName, lastName,
                billingEmail, country,
                city, address, zip, phone,
                "Purchase Order");
    }
}