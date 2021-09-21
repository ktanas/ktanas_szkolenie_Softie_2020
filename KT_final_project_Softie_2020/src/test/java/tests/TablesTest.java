package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class TablesTest {

    static WebDriver driver;
    static WebDriverWait wait;

    String getCountryForGivenCompanyName(String company) {

        int i = 1;

        while (++i<=driver.findElements(By.xpath("//*[@id=\"customers\"]/tbody/tr")).size())
            if (driver.findElement(By.xpath("//*[@id=\"customers\"]/tbody/tr["+i+"]/td[1]")).getText().
                    equals(company)) break;

        if (i>driver.findElements(By.xpath("//*[@id=\"customers\"]/tbody/tr")).size())
            return ("ERROR: Company does not exist in the table");

        // i = numer wiersza, w którym 'company' jest równe nazwie danej jako parametr wejściowy

        return driver.findElement(By.xpath("//*[@id=\"customers\"]/tbody/tr["+i+"]/td[3]")).getText();
    }

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver\\geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver\\chromedriver.exe");

        //driver = new ChromeDriver();
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
    }

    @Test
    void Softie_egzamin_zad7_test() {

        // To jest prosty, krótki test zakodowany tylko na potrzeby egzaminu, tak więc szkoda mi czasu na
        // Page Object Model i przejrzystość

        driver.get("https://www.w3schools.com/html/html_tables.asp");

        Assertions.assertTrue(driver.findElements(By.xpath("//*[@id=\"customers\"]/tbody/tr")).size() == 7);
        Assertions.assertTrue(driver.findElement(By.xpath("//*[@id=\"customers\"]/tbody/tr[7]/td[2]")).getText().
                equals("Giovanni Rovelli"));

        Assertions.assertTrue(getCountryForGivenCompanyName("Centro comercial Moctezuma").equals("Mexico"));
        Assertions.assertTrue(getCountryForGivenCompanyName("Laughing Bacchus Winecellars").equals("Canada"));
    }

    @AfterAll
    static void tearDown() {
            driver.quit();
        }

        @AfterEach
        void clearCookies() {
            driver.manage().deleteAllCookies();
        }

}
