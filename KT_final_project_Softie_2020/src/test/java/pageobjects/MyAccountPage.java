package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyAccountPage extends BasePage {

    // These web element should be visible only when user is not logged in

    @FindBy(id = "username")
    private WebElement loginUsernameOrEmailAddressField;

    @FindBy(id = "password")
    private WebElement loginPasswordField;

    //@FindBy(className = "show-password-input")
    //private WebElement loginPasswordViewer;

    @FindBy(id = "reg_username")
    private WebElement registerUsernameField;

    @FindBy(id = "reg_email")
    private WebElement registerEmailField;

    @FindBy(id = "reg_password")
    private WebElement registerPasswordField;

    @FindBy(id = "rememberme")
    private WebElement rememberMeCheckbox;

    @FindBy(name = "login")
    private WebElement loginButton;

    @FindBy(name = "register")
    private WebElement registerButton;

    @FindBy(linkText = "Lost your password?")
    private WebElement lostYourPasswordLink;

    @FindBy(linkText = "privacy policy")
    private WebElement privacyPolicyLink;

    // These web elements should be visible only when user is logged in

    @FindBy(linkText = "Dashboard")
    private WebElement dashboardButton;

    @FindBy(linkText = "Orders")
    private WebElement ordersButton;

    @FindBy(linkText = "Downloads")
    private WebElement downloadsButton;

    @FindBy(linkText = "Addresses")
    private WebElement addressesButton;

    @FindBy(linkText = "Account details")
    private WebElement accountDetailsButton;

    @FindBy(linkText = "Logout")
    private WebElement logoutButton;

    @FindBy(css = "#content > div > div.woocommerce > ul")
    private WebElement alertMessage;

    public boolean isLoggedIn() {
        return logoutButton.isDisplayed();
    }

    public boolean isLoggedOut() {
        return loginButton.isDisplayed();
    }

    public boolean isAlertMessageCorrect(String expectedAlertMessage) {
        return (alertMessage.getText().equals(expectedAlertMessage));
    }

    public MyAccountPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public void login(String username, String password) {
        loginUsernameOrEmailAddressField.clear();
        loginUsernameOrEmailAddressField.sendKeys(username);
        loginPasswordField.clear();
        loginPasswordField.sendKeys(password);
        loginButton.click();
    }

    public void logout() {
        logoutButton.click();
    }

    public void register(String username, String email, String password) {
        registerUsernameField.clear();
        registerUsernameField.sendKeys(username);
        registerEmailField.clear();
        registerEmailField.sendKeys(email);
        registerPasswordField.clear();
        registerPasswordField.sendKeys(password);
        registerButton.click();
    }

    public MyAccountPage clearCartBeforeTest(MyAccountPage previousPage) {
        // function used to clear possible garbage left from previous test(s)
        // e.g. products in cart from earlier test which failed on assertion and had not emptied the cart afterwards
        CartPage cartPage = previousPage.goToCartPage();
        cartPage = cartPage.emptyCart();
        cartPage = cartPage.resetCoupon();

        MyAccountPage newPage = cartPage.goToMyAccountPage();
        return newPage;
    }


}
