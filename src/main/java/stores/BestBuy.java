package stores;

import alerts.TextAlerts;
import loaders.Proxy.ProxyObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;



public class BestBuy extends Store{
    private static WebDriver driver;

    public static WebElement cartButton;




    public BestBuy(ProxyObject randomProxy, String url, String series, String brand, String model, String price) {
        super(randomProxy, url, series, brand, model, price);
        this.driver = getDriver();
    }

    public static void addToCart() {
        cartButton = driver.findElement(By.xpath("//button[@class=\"btn btn-primary btn-lg btn-block btn-leading-ficon add-to-cart-button\"]"));
        if (cartButton.isDisplayed()) {
            TextAlerts.alert(TextAlerts.addContacts());
            driver.close();
            Thread.currentThread().interrupt();
        }
    }

}
