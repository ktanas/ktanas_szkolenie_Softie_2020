package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageobjects.*;

public class SearchTest extends BaseTest {

    @Test
    void TestCase_SearchForNonexistentProductAndThenLogout() {

        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com","Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        SearchPage searchPage = myAccountPage.searchForProduct("aaa"); // product named "aaa" does not exist

        Assertions.assertTrue(searchPage.searchResultIsEmpty());

        myAccountPage = searchPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_SearchForProductAndPurchaseThreeRandomCoins() {

        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com","Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        SearchPage searchPage = myAccountPage.searchForProduct("coin");

        for (int i = 1; i <= 3; i++) searchPage.addRandomProductToCart();

        CartPage cartPage = searchPage.clickViewCartButton();

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


        myAccountPage = searchPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

}
