package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class OrderPage extends BasePage {

    @FindBy(className = "entry-title")
    private WebElement entryTitle;

    @FindBy(className = "woocommerce-order-overview__order order")
    private WebElement orderNumber;

    @FindBy(className = "woocommerce-order-overview__date date")
    private WebElement orderDate;

    @FindBy(className = "woocommerce-order-overview__email email")
    private WebElement orderEmail;

    @FindBy(className = "woocommerce-order-overview__total total")
    private WebElement orderTotal;

    @FindBy(className = "woocommerce-order-overview__payment-method method")
    private WebElement orderPaymentMethod;

    @FindBy(className = "woocommerce-order-details__title")
    private WebElement orderDetailsTitle;

    @FindBy(className = "product-name")
    private List<WebElement> productNames;

    @FindBy(className = "product-quantity")
    private List<WebElement> productQuantities;

    @FindBy(className = "product-total")
    private List<WebElement> productTotalPrices;

    @FindBy(xpath = "//*[@id=\"post-8\"]/div/div/div/section[1]/table/tfoot/tr[1]/td/span")
    private WebElement grandSubtotal;

    @FindBy(xpath = "//*[@id=\"post-8\"]/div/div/div/section[1]/table/tfoot/tr[2]/td/span")
    private WebElement discount;

    @FindBy(xpath = "//*[@id=\"post-8\"]/div/div/div/section[1]/table/tfoot/tr[3]/td/span")
    private WebElement grandTotalWhenNoDiscountExists;

    @FindBy(xpath = "//*[@id=\"post-8\"]/div/div/div/section[1]/table/tfoot/tr[4]/td/span")
    private WebElement grandTotalWhenDiscountExists;

    // When there is no discount (no coupon is used) :
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[1]/td/span - subtotal
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[2]/td - paymentMethod
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[3]/td/span - total

    // When discount exists (a coupon is used):
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[1]/td/span - subtotal (no changes)
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[2]/td/span - discount
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[3]/td - paymentMethod
    //*[@id="post-8"]/div/div/div/section[1]/table/tfoot/tr[4]/td/span - total

    @FindBy(className = "woocommerce-error")
    private WebElement errorMessage;

    public OrderPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    private boolean discountExists; // a technical variable used to determine xpath value of grand total

    public boolean getDiscountExists() {
        return discountExists;
    }

    public void setDiscountExists(boolean value) {
        discountExists = value;
    }

    public String getErrorMessageText() {
        return errorMessage.getText();
    }

    public List<String> getProductNames() {
        List<String> resultList = new ArrayList<String>();

        for (int i=1; i<productNames.size(); i++) { // start from i=1, because element 0 = 'Product'
            resultList.add((productNames.get(i).getText()).
                    substring(0,(productNames.get(i).getText()).indexOf('×')-2));
            // example: convert "500g Gold Bar  × 1" to "500g Gold Bar"
        }

        return resultList;
    }

    public List<Integer> getProductQuantities() {
        List<Integer> resultList = new ArrayList<Integer>();

        for (int i=0; i<productQuantities.size(); i++) {
            resultList.add(Integer.parseInt(productQuantities.get(i).getText().
                    substring(2,(productQuantities.get(i).getText()).length())));
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

    public List<Double> getProductTotalPrices() {
        List<Double> resultList = new ArrayList<Double>();

        for (int i=1; i<productTotalPrices.size(); i++) // start from i=1, because element 0 = 'subtotal'
            resultList.add(convertPriceStringToDouble(productTotalPrices.get(i).getText()));

        return resultList;
    }

    public double sumProductTotalPrices() {

        double sum = 0;

        for (Double i: getProductTotalPrices())
            sum += i;

        sum = Math.round(sum * 100.0) / 100.0;

        return sum;
    }


    public Double getGrandSubtotal() {
        return convertPriceStringToDouble(grandSubtotal.getText());
    }

    public Double getDiscount() {
        return convertPriceStringToDouble(discount.getText());
    }

    public Double getGrandTotal() {

        if (discountExists)
            return convertPriceStringToDouble(grandTotalWhenDiscountExists.getText());
        else
            return convertPriceStringToDouble(grandTotalWhenNoDiscountExists.getText());
    }

}
