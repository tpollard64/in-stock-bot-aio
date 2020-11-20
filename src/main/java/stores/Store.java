package stores;

import loaders.Products;
import loaders.Proxy;
import loaders.Proxy.ProxyObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class Store implements Runnable, OutputColors{

    private String url;
    private ProxyObject proxy;
    private boolean inCart = false;
    private String model;
    private String brand;
    private String series;
    private String price;
    private ChromeDriver driver;
    private ChromeOptions options;

    public Store(ProxyObject proxy, String url, String series, String brand, String model, String price) {
        setUrl(url);
        setSeries(series);
        setBrand(brand);
        setModel(model);
        setPrice(price);
    }

    public void run() {
        driver = new ChromeDriver();
        driver.manage().timeouts().setScriptTimeout(300L,TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(300L, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(300L, TimeUnit.SECONDS);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverWait wait = new WebDriverWait(driver, 300L);
        try {
            searchProducts();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SecurityException e){
            e.printStackTrace();
            System.out.println("Waiting for " + getUrl() + " to load...");
        }
    }

    public void waitForLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 300);
        driver.get(getUrl());
        wait.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver d) {
                return String.valueOf(((JavascriptExecutor) d).executeScript("return document.readyState")).equals("complete");
            }
        });
    }

    public void searchProducts() throws InterruptedException {
        while(!inCart){
            driver.get(getUrl());
            //waitForLoad(driver);
            addToCart();
        }
    }

    public static void addToCart() {
    }

    public String getUrl(){
        return url;
    }
    public String getModel() {
        return model;
    }
    public String getSeries(){
        return series;
    }
    public String getBrand(){
        return brand;
    }
    public ChromeDriver getDriver(){
        return driver;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setSeries(String series){
        this.series = series;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }
    public void setModel(String model){
        this.model = model;
    }
    public void setPrice(String price){
        this.price = price;
    }






}


