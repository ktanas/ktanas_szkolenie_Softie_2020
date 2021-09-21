package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class CheckoutPage extends BasePage {

    @FindBy(className = "woocommerce-info")
    private WebElement infoMessage;

    @FindBy(css = ".cart-empty")
    private WebElement cartIsEmptyMessage;

    @FindBy(className = "showcoupon")
    private WebElement couponAskingButton;

    @FindBy(name = "coupon_code")
    private WebElement couponCodeField;

    @FindBy(name = "apply_coupon")
    private WebElement applyCouponButton;

    @FindBy(className = "woocommerce-remove-coupon")
    private WebElement removeCouponButton;

    @FindBy(className = "woocommerce-message") // a non-error message, e.g. "Coupon code applied successfully."
    private WebElement message;

    @FindBy(className = "woocommerce-error")
    private WebElement errorMessage;

    @FindBy(id = "billing_first_name")
    private WebElement firstNameField;

    @FindBy(id = "billing_last_name")
    private WebElement lastNameField;

    @FindBy(id = "billing_company") // optional field
    private WebElement companyNameField;

    @FindBy(id = "select2-billing_country-container")
    private WebElement countrySelector;

    @FindBy(className = "select2-search__field")
    private WebElement countrySelectorTextField;

    @FindBy(id = "select2-billing_state-container")
    private WebElement stateSelector;

    @FindBy(className = "select2-search__field")
    private WebElement stateSelectorTextField;

    @FindBy(className = "select2-selection__rendered")
    private WebElement stateSelectorValues;

    @FindBy(id = "billing_address_1")
    private WebElement streetAddressField;

    @FindBy(id = "billing_address_2") // optional field
    private WebElement streetAddress2Field;

    @FindBy(id = "billing_postcode")
    private WebElement postcodeField;

    @FindBy(id = "billing_city")
    private WebElement cityField;

    @FindBy(id = "billing_phone")
    private WebElement phoneField;

    @FindBy(id = "billing_email")
    private WebElement emailField;

    @FindBy(id = "order_comments")
    private WebElement orderCommentsField;

    @FindBy(className = "product-name")
    private List<WebElement> purchasedProductNames;

    @FindBy(className = "product-quantity")
    private List<WebElement> purchasedProductQuantities;

    @FindBy(className = "product-total")
    private List<WebElement> productSubtotalPrices;

    @FindBy(className = "cart-subtotal")
    private WebElement grandSubtotal;

    @FindBy(className = "cart-discount") // used if you have a coupon
    private WebElement cartDiscount;

    @FindBy(className = "order-total")
    private WebElement grandTotal;

    @FindBy(id = "place_order")
    private WebElement placeOrderButton;

    @FindBy(className = "return-to-shop")
    private WebElement returnToShopButton;

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public String getInfoMessage() {
        return infoMessage.getText();
    }
    public String getMessage() {
        return message.getText();
    }
    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public boolean cartIsEmpty() {

        return (getInfoMessage().equals("Checkout is not available whilst your cart is empty.")
            && cartIsEmptyMessage.getText().equals("Your cart is currently empty."));
    }

    public void fillOutBilling(String firstName,
                               String lastName,
                               String companyName, // optional
                               String country,
                               String streetAddress,
                               String streetAddress2, // optional
                               String postcode,
                               String city,
                               String phone,
                               String email,
                               String orderComments) // optional
    {
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
        companyNameField.clear();
        companyNameField.sendKeys(companyName);

        countrySelector.click();
        countrySelectorTextField.sendKeys(country);
        countrySelector.click();

        streetAddressField.clear();
        streetAddressField.sendKeys(streetAddress);
        streetAddress2Field.clear();
        streetAddress2Field.sendKeys(streetAddress2);
        postcodeField.clear();
        postcodeField.sendKeys(postcode);
        cityField.clear();
        cityField.sendKeys(city);
        phoneField.clear();
        phoneField.sendKeys(phone);
        emailField.clear();
        emailField.sendKeys(email);
        orderCommentsField.clear();
        orderCommentsField.sendKeys(orderComments);
    }

    public void waitUntilBlockUIOverlayDisappears(int timeInSeconds) {
        WebDriverWait waitForBlockUIOverlay = new WebDriverWait(driver,timeInSeconds);
        waitForBlockUIOverlay.until(ExpectedConditions.invisibilityOfElementLocated(By.className("blockUI blockOverlay")));
    }

    public void waitUntilCouponApplyMessageIsVisible(int timeInSeconds) {
        WebDriverWait waitForCouponApplyMessage = new WebDriverWait(driver,timeInSeconds);
        waitForCouponApplyMessage.until(ExpectedConditions.visibilityOf(message));
    }

    public void waitUntilCouponAlreadyAppliedErrorMessageIsVisible(int timeInSeconds) {
        WebDriverWait waitForCouponAlreadyAppliedErrorMessage = new WebDriverWait(driver,timeInSeconds);
        waitForCouponAlreadyAppliedErrorMessage.until(ExpectedConditions.visibilityOf(errorMessage));
    }

    public void waitUntilCouponCodeFieldIsVisible(int timeInSeconds) {
        WebDriverWait waitForCouponCodeField = new WebDriverWait(driver,timeInSeconds);
        waitForCouponCodeField.until(ExpectedConditions.visibilityOf(couponCodeField));
    }

    public OrderPage placeOrder(boolean couponIsUsed) {

        placeOrderButton.click();
        OrderPage orderPage = new OrderPage(driver, wait);

        orderPage.setDiscountExists(couponIsUsed);

        return orderPage;
    }

    public String placeOrder_Fail() { // for negative test cases, where order page is not expected to be entered

        placeOrderButton.click();

        return getErrorMessage(); // a failed order should always generate an error message
    }

    public List<String> getPurchasedProductNames() {
        List<String> resultList = new ArrayList<String>();

        for (int i=1; i<purchasedProductNames.size(); i++) { // start from i=1, because element 0 = 'Product'
            resultList.add((purchasedProductNames.get(i).getText()).
                    substring(0,(purchasedProductNames.get(i).getText()).indexOf('×')-2));
            // example: convert "500g Gold Bar  × 1" to "500g Gold Bar"
        }

        return resultList;
    }

    public List<Integer> getPurchasedProductQuantities() {
        List<Integer> resultList = new ArrayList<Integer>();

        for (int i=0; i<purchasedProductQuantities.size(); i++) {
            resultList.add(Integer.parseInt(purchasedProductQuantities.get(i).getText().
                    substring(2,(purchasedProductQuantities.get(i).getText()).length())));
            // example: convert "× 15" (string) to 15 (integer value)
        }

        return resultList;
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

    public List<Double> getPurchasedProductPrices() {
        List<Double> resultList = new ArrayList<Double>();

        for (int i=1; i<productSubtotalPrices.size(); i++) // start from i=1, because element 0 = 'subtotal'
            resultList.add(convertPriceStringToDouble(productSubtotalPrices.get(i).getText()));

        return resultList;
    }

    public double sumPurchasedProductPrices() {

        double sum = 0;

        for (Double i: getPurchasedProductPrices())
            sum += i;

        sum = Math.round(sum * 100.0) / 100.0;

        return sum;
    }

    public Double getGrandSubtotal() {
        return convertPriceStringToDouble(grandSubtotal.getText());
    }

    public Double getDiscount() {
        // cut "-€number [Remove] to the number itself
        String correctedDiscountString = cartDiscount.getText().substring(2,cartDiscount.getText().indexOf('[')-1);

        return convertPriceStringToDouble(correctedDiscountString);
    }

    public Double getGrandTotal() {
        return convertPriceStringToDouble(grandTotal.getText());
    }

    public boolean verifyDiscount() { // check that discount is computed correctly (total = subtotal - discount)
        Double result = (getGrandSubtotal() - getDiscount());
        return (Math.round(result * 100.0) / 100.0) == getGrandTotal();
    }

    public void applyCoupon(String couponCode) {

        couponAskingButton.click();

        waitUntilCouponCodeFieldIsVisible(5);
        couponCodeField.clear();
        couponCodeField.sendKeys(couponCode);

        applyCouponButton.click();
    }

    public void removeCoupon() {
        removeCouponButton.click();
    }

    public boolean wrongCouponMessageIsShown(String couponCode) {
        return getErrorMessage().equals("Coupon \""+couponCode+"\" does not exist!");
    }

    public boolean couponAlreadyAppliedMessageIsShown() {
        return getErrorMessage().equals("Coupon code already applied!");
    }

    public boolean couponAppliedMessageIsShown() {
        return getMessage().equals("Coupon code applied successfully.");
    }

}
