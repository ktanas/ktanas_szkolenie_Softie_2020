package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import pageobjects.HomePage;
import pageobjects.MyAccountPage;

public class RegisterTest extends BaseTest {

    @Test
    void TestCase_SuccessfulRegistration() {

        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        // Change input data before applying this test next time - this user and email will already be registered
        myAccountPage.register("G887878aa","quejjkh@cef.cff.com","Alamakota7");

        Assertions.assertTrue(myAccountPage.isLoggedIn());

        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_UnsuccessfulRegistration_UsernameAlreadyExists() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.register("qee4","qee4@w.com","Alamakota8");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect(
                "Error: An account is already registered with that username. Please choose another."
        ));
    }

    @Test
    void TestCase_UnsuccessfulRegistration_EmailAlreadyExists() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.register("q5iik6a","qee4@abc.com","Alamakota9");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect(
                "Error: An account is already registered with your email address. Please log in."
        ));
    }
    @Test
    void TestCase_UnsuccessfulRegistration_EmptyPassword() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.register("aqf57ay","aku4y4@top.com","");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect(
                "Error: Please enter an account password."
        ));
    }

    @Test
    void TestCase_UnsuccessfulRegistration_TooWeakPassword() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.register("aayay","auauauau@abc.abc","xyz");

        Assertions.assertTrue(myAccountPage.isLoggedOut());
        Assertions.assertTrue(driver.findElement(By.className("woocommerce-password-strength-bad")).isDisplayed());

    }

}
