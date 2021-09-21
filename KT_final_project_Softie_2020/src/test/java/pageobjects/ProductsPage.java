package pageobjects;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;

public class ProductsPage extends BasePage {

    @FindBy(name = "orderby")
    private WebElement sortingSelector;

    @FindBy(linkText = "Add to cart")
    private List<WebElement> addToCartButtons;

    @FindBy(css = ".added_to_cart")
    private List<WebElement> viewCartButtons;

    @FindBy(className = "price")
    private List<WebElement> productPriceStrings;
    // Price strings have format like €50.586.234,50, they need to be converted to float values

    @FindBy(css = ".woocommerce-info")
    private WebElement info;

    public ProductsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public boolean productListIsEmpty() {
        if (addToCartButtons.size() > 0) return false;

        Assertions.assertTrue(info.isDisplayed()
                && info.getText().equals("No products were found matching your selection."));
        return true;
    }

    public double convertPriceStringToDouble(String initialString) {
        // convert value extracted from cart page as price (e.g. €1.055.300,70) to a float (1055300.70)
        // and get the real price, e.g. after discount, starting from string like '€25.000,00 €23.000,00'
        String resultString = initialString;
        while (resultString.indexOf('€') > -1)
            resultString = resultString.substring(resultString.indexOf('€')+1,resultString.length());

        while (resultString.indexOf('.') > -1) {

            if (resultString.indexOf('.')==0) resultString = resultString.substring(1,resultString.length());
            else resultString = resultString.substring(0,resultString.indexOf('.'))+
                    resultString.substring(resultString.indexOf('.')+1,resultString.length());

        }
        resultString = resultString.replace(',','.');

        return Double.parseDouble(resultString);
    }

    double getProductPrice(int productIndex) {
        return convertPriceStringToDouble(productPriceStrings.get(productIndex).getText());
    }

    public String getCssSelectorOfProductImage(int productIndex) {
        //li.product:nth-child(i = 1,2,...) > a:nth-child(1)
        return "li.product:nth-child(" + String.valueOf(productIndex+1) + ") > a:nth-child(1)";
    }

    public String getCssSelectorOfProductAddButton(int productIndex) {
        //li.product:nth-child(i = 1,2,...) > a:nth-child(2)
        return "li.product:nth-child(" + String.valueOf(productIndex+1) + ") > a:nth-child(2)";
    }

    public void addProductToCart(int productIndex) {
        increaseTotalCartValue(getProductPrice(productIndex));
        addToCartButtons.get(productIndex).click();
    }
    public void addRandomProductToCart() {
        Random rand = new Random();
        addProductToCart(rand.nextInt(addToCartButtons.size()));
    }

    public CartPage clickViewCartButton() {
        viewCartButtons.get(0).click();
        CartPage cartPage = new CartPage(driver, wait);
        return cartPage;
    }

}
