package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageobjects.*;

public class CheckoutTest extends BaseTest {

    @Test
    void TestCase_TryCheckoutWithEmptyCart() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        CheckoutPage checkoutPage = myAccountPage.goToCheckoutPage();

        Assertions.assertTrue(checkoutPage.cartIsEmpty());

        myAccountPage = checkoutPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_OrderSomeProductsAndThenTryCheckoutWithEmptyCart() {

        // Order some products as in CartTest, but then remove them so that the cart will be empty

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        for (int i = 1; i <= 5; i++) {
            productsPage = myAccountPage.chooseRandomProductCategory();
            if (productsPage.productListIsEmpty() == false) productsPage.addRandomProductToCart();
        }

        CartPage cartPage = productsPage.goToCartPage();

        while (cartPage.numberOfDistinctProductTypesInCart() > 0) {
            // empty the whole cart
            cartPage.removeProductFromCart(0);

            cartPage = productsPage.goToCartPage(); // extra page reset to avoid 'StaleElementReferenceException'
        }

        CheckoutPage checkoutPage = cartPage.goToCheckoutPage();

        Assertions.assertTrue(checkoutPage.cartIsEmpty());

        myAccountPage = cartPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_OrderSomeProductsAndFillOutCompleteBilling() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        double checkoutSubtotal = checkoutPage.getGrandSubtotal();
        double checkoutTotal = checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(false); // false = no coupon is used

        double orderSubtotal = orderPage.getGrandSubtotal();
        double orderTotal = orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we have no coupon in this test case, so subtotal=total
        Assertions.assertTrue(orderTotal == orderSubtotal);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage
        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }

    @Test
    void TestCase_OrderSomeProductsAndFillOutBillingWithOnlyMandatoryFields() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "",
                "Poland",
                "ul. Kwiatowa 7/125",
                "",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                ""
        );

        double checkoutSubtotal =  checkoutPage.getGrandSubtotal();
        double checkoutTotal =  checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(false); // false = no coupon is used

        double orderSubtotal =  orderPage.getGrandSubtotal();
        double orderTotal =  orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we have no coupon in this test case, so subtotal=total
        Assertions.assertTrue(orderTotal == orderSubtotal);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage
        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }

    @Test
    void TestCase_Error_NoFirstName() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "", // Error: firstName is a mandatory field
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing First name is a required field."));
    }

    @Test
    void TestCase_Error_NoLastName() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "", // Error: lastName is a mandatory field
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Last name is a required field."));
    }

    @Test
    void TestCase_Error_NoStreetAddress() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "",  // Error: streetAddress is a mandatory field
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Street address is a required field."));
    }

    @Test
    void TestCase_Error_NoPostcode() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "", // Error: postcode is a mandatory field
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Postcode / ZIP is not a valid postcode / ZIP."));
    }

    @Test
    void TestCase_Error_InvalidPostcode() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-00", // Error: this postcode is not valid
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Postcode / ZIP is not a valid postcode / ZIP."));
    }

    @Test
    void TestCase_Error_NoCity() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "", // Error: city is a mandatory field
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Town / City is a required field."));
    }

    @Test
    void TestCase_Error_NoPhone() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);


        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "", // Error: phone is a mandatory field
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Phone is a required field."));
    }

    @Test
    void TestCase_Error_InvalidPhone() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "a", // Error: this phone number is not valid
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Phone is not a valid phone number."));
    }

    @Test
    void TestCase_Error_NoEmail() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "", // Error: email is a mandatory field
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Billing Email address is a required field."));
    }

    @Test
    void TestCase_Error_InvalidEmail() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4.abc.com", // Error: this email address is not valid
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        Assertions.assertTrue(checkoutPage.placeOrder_Fail().equals
                ("Invalid billing email address"));
    }

    @Test
    void TestCase_OrderWithWrongCouponName() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon("aaa"); // this coupon name is invalid

        Assertions.assertTrue(checkoutPage.wrongCouponMessageIsShown("aaa"));

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        double checkoutSubtotal = checkoutPage.getGrandSubtotal();
        double checkoutTotal = checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(false); // false = coupon name was invalid

        double orderSubtotal = orderPage.getGrandSubtotal();
        double orderTotal = orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we have no coupon in this test case, so subtotal=total
        Assertions.assertTrue(orderTotal == orderSubtotal);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage
        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }

    @Test
    void TestCase_OrderWithCorrectCouponName() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.applyCoupon("softie"); // valid coupon name
        Assertions.assertTrue(checkoutPage.couponAppliedMessageIsShown());

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        double checkoutSubtotal = checkoutPage.getGrandSubtotal();
        double checkoutDiscount = checkoutPage.getDiscount();
        double checkoutTotal = checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(true); // true = we use a coupon in this test case

        double orderSubtotal = orderPage.getGrandSubtotal();
        double orderDiscount = orderPage.getDiscount();
        double orderTotal = orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderDiscount == checkoutDiscount);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we use a valid coupon name in this test case, so a discount is present
        Assertions.assertTrue(orderTotal == Math.round((orderSubtotal - orderDiscount) * 100.0) / 100.0);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage
        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }

    @Test
    void TestCase_OrderWithCorrectCouponNameAndTryToApplyCouponAgain() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.applyCoupon("softie"); // valid coupon name
        checkoutPage.waitUntilCouponApplyMessageIsVisible(5);

        Assertions.assertTrue(checkoutPage.couponAppliedMessageIsShown());

        checkoutPage.waitUntilCouponCodeFieldIsVisible(5);

        checkoutPage.applyCoupon("softie"); // try to apply the coupon again
        checkoutPage.waitUntilCouponAlreadyAppliedErrorMessageIsVisible(5);

        Assertions.assertTrue(checkoutPage.couponAlreadyAppliedMessageIsShown());

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        double checkoutSubtotal = checkoutPage.getGrandSubtotal();
        double checkoutDiscount = checkoutPage.getDiscount();
        double checkoutTotal = checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(true); // true = we use a coupon in this test case

        double orderSubtotal = orderPage.getGrandSubtotal();
        double orderDiscount = orderPage.getDiscount();
        double orderTotal = orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderDiscount == checkoutDiscount);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we use a valid coupon name in this test case, so a discount is present
        Assertions.assertTrue(orderTotal == Math.round((orderSubtotal - orderDiscount) * 100.0) / 100.0);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage
        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }

    @Test
    void TestCase_OrderWithRemovedCoupon() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());

        // Make sure that cart is empty before starting test case
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        // order some sample products
        productsPage = myAccountPage.chooseGivenProductCategory(2);
        productsPage.addProductToCart(0);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);
        productsPage = myAccountPage.chooseGivenProductCategory(3);
        productsPage.addProductToCart(3);

        CartPage cartPage = productsPage.goToCartPage();

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon("softie"); // this is a valid coupon name
        Assertions.assertTrue(checkoutPage.couponAppliedMessageIsShown());

        checkoutPage.removeCoupon();

        checkoutPage.fillOutBilling(
                "Anna",
                "Kowalska",
                "Zineb S.A.",
                "Poland",
                "ul. Kwiatowa 7/125",
                "Coś tu muszę napisać...",
                "00-000",
                "Pikutkowo Podlaskie",
                "(+48)123456789",
                "qee4@abc.com",
                "Roboty wykończeniowe. Ja wykańczam projekt, a projekt wykańcza mnie. Równoważność."
        );

        double checkoutSubtotal = checkoutPage.getGrandSubtotal();
        double checkoutTotal = checkoutPage.getGrandTotal();

        checkoutPage.waitUntilBlockUIOverlayDisappears(5);

        OrderPage orderPage = checkoutPage.placeOrder(false); // false = coupon was applied, but then removed

        double orderSubtotal = orderPage.getGrandSubtotal();
        double orderTotal = orderPage.getGrandTotal();

        // check that orderPage lists the same set of products, quantities and prices as checkoutPage
        Assertions.assertTrue(checkoutPage.getPurchasedProductNames().equals(orderPage.getProductNames()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductQuantities().equals(orderPage.getProductQuantities()));
        Assertions.assertTrue(checkoutPage.getPurchasedProductPrices().equals(orderPage.getProductTotalPrices()));

        Assertions.assertTrue(orderSubtotal == checkoutSubtotal);
        Assertions.assertTrue(orderTotal == checkoutTotal);

        // we have removed coupon in this test case, so subtotal=total
        Assertions.assertTrue(orderTotal == orderSubtotal);

        // check that product price sums are computed correctly, both for checkoutPage and orderPage

        Assertions.assertTrue(checkoutPage.sumPurchasedProductPrices() == checkoutSubtotal);
        Assertions.assertTrue(orderPage.sumProductTotalPrices() == orderSubtotal);
    }
}
