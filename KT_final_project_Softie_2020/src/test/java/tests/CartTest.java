package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.HomePage;
import pageobjects.MyAccountPage;
import pageobjects.ProductsPage;

public class CartTest extends BaseTest {

    @Test
    void TestCase_CheckThatCartIsEmptyAndLogout() {

        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4","Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        CartPage cartPage = myAccountPage.goToCartPage();
        Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());

        myAccountPage = cartPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_AddOneRandomProductToCartThenRemoveAndLogout() {

        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4","Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = myAccountPage.chooseRandomProductCategory();

        if (productsPage.productListIsEmpty()==false) {

            productsPage.addRandomProductToCart();
            CartPage cartPage = productsPage.clickViewCartButton();
            cartPage.removeProductFromCart(0);
            Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());

            myAccountPage = cartPage.goToMyAccountPage();
        }
        else { // "Uncategorized" product page was randomly drawn. Do not add anything to cart in this case.
            CartPage cartPage = productsPage.goToCartPage();
            Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());
            myAccountPage = cartPage.goToMyAccountPage();
        }

        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_AddFiveRandomProductsToCartThenRemoveAndLogout() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        for (int i = 1; i <= 5; i++) {
            productsPage = myAccountPage.chooseRandomProductCategory();
            if (productsPage.productListIsEmpty() == false) productsPage.addRandomProductToCart();
        }

        CartPage cartPage = productsPage.goToCartPage();

        cartPage.verifyCartPrice();

        while (cartPage.numberOfDistinctProductTypesInCart() > 0) {
            // empty the whole cart
            cartPage.removeProductFromCart(0);

            cartPage = productsPage.goToCartPage(); // extra page reset to avoid 'StaleElementReferenceException'
        }

        Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());

        myAccountPage = cartPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_AddTwoSameProductsThenUndoAndRemoveAgain() {

        HomePage homePage = new HomePage(driver, wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com", "Alamakota9");
        Assertions.assertTrue(myAccountPage.isLoggedIn());
        myAccountPage = myAccountPage.clearCartBeforeTest(myAccountPage);

        ProductsPage productsPage = null;

        for (int i=1; i<=2; i++) {
            productsPage = myAccountPage.chooseGivenProductCategory(3);
            productsPage.addProductToCart(2);
            // Purchase two silver coins with Darth Vader's portrait
        }

        CartPage cartPage = productsPage.goToCartPage();

        Assertions.assertTrue(cartPage.numberOfDistinctProductTypesInCart() == 1);
        cartPage.removeProductFromCart(0);

        Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());
        Assertions.assertTrue(cartPage.getDisplayedMessageText().equals
                ("“1 oz STAR WARS Darth Vader Silver Coin (2020)” removed. Undo?"));

        cartPage.undoLastOperation();

        Assertions.assertTrue(cartPage.numberOfDistinctProductTypesInCart() == 1);
        cartPage.removeProductFromCart(0);

        Assertions.assertTrue(cartPage.cartIsCurrentlyEmpty());
        Assertions.assertTrue(cartPage.getDisplayedMessageText().equals
                ("“1 oz STAR WARS Darth Vader Silver Coin (2020)” removed. Undo?"));

        myAccountPage = cartPage.goToMyAccountPage();
        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

}
