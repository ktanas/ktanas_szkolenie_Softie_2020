package pageobjects;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(className = "button wc-backward")
    private WebElement returnToShopButton;

    @FindBy(css = ".cart-empty")
    private WebElement cartIsEmptyMessage;

    @FindBy(css = ".woocommerce-message")
    private WebElement displayedMessage;

    @FindBy(className = "restore-item")
    private WebElement undoButton;

    @FindBy(className = "product-name")
    private List<WebElement> productsInCart;

    @FindBy(className = "remove")
    private List<WebElement> productRemoveButtons;

    @FindBy(className = "product-price")
    private List<WebElement> singleProductPrices;

    @FindBy(className = "quantity")
    private List<WebElement> productQuantities;

    @FindBy(className = "product-quantity")
    private List<WebElement> productQuantities2;

    @FindBy(className = "input-text")
    private List<WebElement> inputTextFields;

    @FindBy(className = "screen-reader-text")
    private List<WebElement> quantityTextFields;

    @FindBy(className = "product-subtotal")
    private List<WebElement> productSubtotalPrices;

    @FindBy(id = "coupon_code")
    private WebElement couponCodeField;

    @FindBy(name = "apply_coupon")
    private WebElement applyCouponButton;

    @FindBy(name = "update_cart")
    private WebElement updateCartButton;

    @FindBy(className = "cart-subtotal")
    private WebElement cartSubtotal;

    @FindBy(className = "order-total")
    private WebElement orderTotal;

    @FindBy(className = "wc-proceed-to-checkout")
    private WebElement proceedToCheckoutButton;

    @FindBy(className = "woocommerce-Price-amount")
    private List<WebElement> priceStrings;

    @FindBy(className = "woocommerce-remove-coupon")
    private WebElement removeCouponButton;

    public CartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    // Indexes of priceStrings: (0,1,...,size-1)
    // Price string format is like €30.202,60
    // 0 - price listed in upper right part of the screen, not used in our tests (at least for now)
    // 1 - price of single instance of 1st product
    // 2 - total price of products of 1st purchased type
    // .................................................
    // If no coupon is used:
    // priceStrings.size()-2 - grand subtotal
    // priceStrings.size()-1 = grand total
    //
    // If coupon is used (and there is a discount):
    // priceStrings.size()-3 - grand subtotal
    // priceStrings.size()-2 - discount
    // priceStrings.size()-1 = grand total
    //
    // Easy way to check whether the coupon is used (no need to define extra variables/functions)
    // if a coupon is used, priceStrings.size() == 2 * numberOfDistinctProductTypesInCart() + 4
    // if no coupon,        priceStrings.size() == 2 * numberOfDistinctProductTypesInCart() + 3

    public boolean cartIsCurrentlyEmpty() {

        return cartIsEmptyMessage.getText().equals("Your cart is currently empty.");
    }

    public double convertPriceStringToDouble(String initialString) {
        // convert value extracted from checkout page as price (e.g. €1.055.300,70) to a double (1055300.70)

        String resultString = initialString.substring(initialString.indexOf('€')+1,initialString.length());
        // shift past the euro symbol to obtain the actual price string

        while (resultString.indexOf('.') > -1) {

            if (resultString.indexOf('.')==0) resultString = resultString.substring(1,resultString.length());
            else resultString = resultString.substring(0,resultString.indexOf('.'))+
                    resultString.substring(resultString.indexOf('.')+1,resultString.length());

        }
        resultString = resultString.replace(',','.');

        return Double.parseDouble(resultString);
    }

    public String getDisplayedMessageText() {
        return displayedMessage.getText();
    }

    public int getSingleProductQuantity(int productIndex) {
        return Integer.parseInt((inputTextFields.get(productIndex)).getAttribute("value"));
    }

    public double getSingleProductPrice(int productIndex) {
        // correct index value on priceString list for price of single unit of product indexed i is 1+2*i
        return convertPriceStringToDouble(priceStrings.get(1+2*productIndex).getText());
    }

    public double getSingleProductSubtotal(int productIndex) {
        // correct index value on priceString list for price of subtotal for product indexed i is 2+2*i
        return convertPriceStringToDouble(priceStrings.get(2+2*productIndex).getText());
    }

    public double getGrandSubtotal() {
        if (priceStrings.size() == (2 * numberOfDistinctProductTypesInCart() + 4)) // true when coupon is used, and only then
             return convertPriceStringToDouble(priceStrings.get(priceStrings.size()-3).getText());
        else return convertPriceStringToDouble(priceStrings.get(priceStrings.size()-2).getText());
    }

    public double getDiscount() {
        if (priceStrings.size() == (2 * numberOfDistinctProductTypesInCart() + 4)) // true when coupon is used, and only then
                return convertPriceStringToDouble(priceStrings.get(priceStrings.size()-2).getText());
        else return 0.0;
    }

    public double getGrandTotal() {
        return convertPriceStringToDouble(priceStrings.get(priceStrings.size()-1).getText());
    }

    public void removeProductFromCart(int productIndex) {
        decreaseTotalCartValue(getSingleProductQuantity(productIndex) * getSingleProductPrice(productIndex));
        productRemoveButtons.get(productIndex).click();
    }

    public int numberOfDistinctProductTypesInCart() {
        if (productsInCart.size() == 0) return 0;
        else return productsInCart.size()-1;
        // The cart page contains on additional element of 'product-name' class, so '-1' is needed here
    }

    public void undoLastOperation() {
        undoButton.click();
    }

    public CheckoutPage proceedToCheckout() {
        proceedToCheckoutButton.click();
        CheckoutPage checkoutPage = new CheckoutPage(driver, wait);
        return checkoutPage;

    }

    public CartPage emptyCart() {

        CartPage cartPage = goToCartPage();

        while (numberOfDistinctProductTypesInCart() > 0) {
            removeProductFromCart(0);

            cartPage = goToCartPage(); // extra page reset to avoid 'StaleElementReferenceException'
        }
        cartPage.clearTotalCartValue();
        return cartPage;
    }

    public CartPage resetCoupon() {

        CartPage cartPage = goToCartPage();

        // if cart is empty, there is no way (at least I don't know it) to remove a coupon!
        // So let us add one fixed product, then refresh page and click on remove coupon button, and then remove
        // that product!
        if (numberOfDistinctProductTypesInCart() == 0) {
            ProductsPage productsPage = cartPage.chooseGivenProductCategory(3);
            productsPage.addProductToCart(2);
            cartPage = productsPage.clickViewCartButton();

            if (priceStrings.size() == 2 * numberOfDistinctProductTypesInCart() + 4) // condition for a coupon existence
                removeCouponButton.click();

            cartPage.removeProductFromCart(0);
        }
        else { // the cart was not empty before
            if (priceStrings.size() == 2 * numberOfDistinctProductTypesInCart() + 4) // condition for a coupon existence
                removeCouponButton.click();
        };

        return cartPage;
    }

    public void verifyCartPrice() {
        // Verify if the total cart price is equal to the sum of prices listed in the respective products' pages
        // and if product subtotals equal single product prices multiplied by product quantity,
        // for each purchased product type

        // Check that product subtotals are computed correctly, i.e. single price * quantity for each product

        for (int productIndex=0; productIndex<numberOfDistinctProductTypesInCart(); productIndex++)
            Assertions.assertTrue(Math.round(
                    (getSingleProductPrice(productIndex) * getSingleProductQuantity(productIndex)) * 100.0) / 100.0
                    == getSingleProductSubtotal(productIndex));

        // Check that product subtotals add up to the grand subtotal
        double sumOfSubtotals = 0.0;
        for (int productIndex=0; productIndex<numberOfDistinctProductTypesInCart(); productIndex++)
           sumOfSubtotals += getSingleProductSubtotal(productIndex);

        Assertions.assertTrue((Math.round(sumOfSubtotals * 100.0)) / 100.0 == getGrandSubtotal());

        // Check that grand total equals the grand subtotal minus discount (when no coupon is used, discount is zero)
        Assertions.assertTrue((Math.round((getGrandSubtotal() - getDiscount()) * 100.0)) / 100.0
                == getGrandTotal());

        // Check that total cart value (computed by adding/removing products) equals the grand subtotal
        // (product prices listed on the tested page do not include possible discount)
        Assertions.assertTrue(getTotalCartValue() == getGrandSubtotal());
    }

}
