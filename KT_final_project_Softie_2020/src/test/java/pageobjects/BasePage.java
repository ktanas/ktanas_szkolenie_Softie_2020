package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Random;

public class BasePage {
    WebDriver driver;
    WebDriverWait wait;

    @FindBy(linkText = "Home")
    private WebElement homeButton;

    @FindBy(linkText = "About us")
    private WebElement aboutUsButton;

    @FindBy(linkText = "Cart")
    private WebElement cartButton;

    @FindBy(linkText = "Checkout")
    private WebElement checkoutButton;

    @FindBy(linkText = "My account")
    private WebElement myAccountButton;

    @FindBy(className = "search-field")
    private WebElement searchField;

    private static double totalCartValue;

    public void clearTotalCartValue() {
        totalCartValue = 0;
    }

    public double getTotalCartValue() {
        return Math.round(totalCartValue * 100.0) / 100.0;
    }

    public void increaseTotalCartValue(double price) {
        totalCartValue += Math.round((price) * 100.0) / 100.0;
    }

    public void decreaseTotalCartValue(double price) {
        totalCartValue -= Math.round((price) * 100.0) / 100.0;
    }

    public ProductsPage chooseGivenProductCategory(int index) {
        String xpathOfGivenCategory = "//*[@id=\"woocommerce_product_categories-3\"]/ul/li["
                                      + String.valueOf(index+1) + "]/a";

        driver.findElement(By.xpath(xpathOfGivenCategory)).click();

        ProductsPage productsPage = new ProductsPage(driver, wait);
        return productsPage;
    }

    public ProductsPage chooseRandomProductCategory() {
        Random rand = new Random();
        return chooseGivenProductCategory(rand.nextInt(5));
        // for now, let us assume there are 5 categories:
        // 'Gold bars','Gold coins','Silver bars','Silver coins','Uncategorized'
    }

    public MyAccountPage goToMyAccountPage() {
        myAccountButton.click();
        MyAccountPage myAccountPage = new MyAccountPage(driver, wait);
        return myAccountPage;
    }

    public CartPage goToCartPage() {
        cartButton.click();
        CartPage cartPage = new CartPage(driver, wait);
        return cartPage;
    }

    public CheckoutPage goToCheckoutPage() {
        checkoutButton.click();
        CheckoutPage checkoutPage = new CheckoutPage(driver, wait);
        return checkoutPage;
    }

    public SearchPage searchForProduct(String productName) {
        searchField.clear();
        searchField.sendKeys(productName);
        searchField.submit();

        SearchPage searchPage = new SearchPage(driver, wait);
        return searchPage;
    }

}
