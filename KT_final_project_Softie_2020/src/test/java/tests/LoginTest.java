package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageobjects.HomePage;
import pageobjects.MyAccountPage;

public class LoginTest extends BaseTest {

    @Test
    void TestCase_SuccessfulLoginByEmail() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com","Alamakota9");

        Assertions.assertTrue(myAccountPage.isLoggedIn());

        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }
    @Test
    void TestCase_SuccessfulLoginByUsername() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4","Alamakota9");

        Assertions.assertTrue(myAccountPage.isLoggedIn());

        myAccountPage.logout();

        Assertions.assertTrue(myAccountPage.isLoggedOut());
    }

    @Test
    void TestCase_UnsuccessfulLogin_UnknownUsername() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee7","Alamakota9");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect("Unknown username. Check again or try your email address."));
    }

    @Test
    void TestCase_UnsuccessfulLogin_UnknownEmail() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee7@abc.com","Alamakota9");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect("Unknown email address. Check again or try your username."));
    }

    @Test
    void TestCase_UnsuccessfulLogin_EmptyPassword() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com","");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect("Error: The password field is empty."));
    }

    @Test
    void TestCase_UnsuccessfulLoginByEmail_WrongPassword() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4@abc.com","alamakota9");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect("Error: The password you entered for the email address qee4@abc.com is incorrect. Lost your password?"));
    }

    @Test
    void TestCase_UnsuccessfulLoginByUsername_WrongPassword() {
        HomePage homePage = new HomePage(driver,wait);
        MyAccountPage myAccountPage = homePage.goToMyAccountPage();

        myAccountPage.login("qee4","Alamakota9 ");

        Assertions.assertTrue(myAccountPage.isAlertMessageCorrect("Error: The password you entered for the username qee4 is incorrect. Lost your password?"));
    }

}
