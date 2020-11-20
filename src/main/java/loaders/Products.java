package loaders;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Products {
    public static ArrayList<ProductObject> load(String name){
        File file = new File(name);

        if(!file.exists()){
            System.out.println(name + " not found...creating file.");
            create(name);
            return null;
        }
        Type type = new TypeToken<ArrayList<ProductObject>>() { }.getType();
        try {
            return new GsonBuilder().create().fromJson(new FileReader(name), type);
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {  e.printStackTrace(); }
        return null;

    }

    public static void create(String name) {
        ArrayList<ProductObject> products = new ArrayList<>();
    }


    public static class ProductObject {
        private String series, brand, model, productID, payment, url, price, store;
        private int tasks;

        public ProductObject(String series, String brand, String model, String productID, String payment, String url, String price, String store, String addToCartUrl, String mobileUrl, int tasks){
            this.series = series;
            this.brand = brand;
            this.model = model;
            this.url = url;
            this.price = price;
            this.series = store;
            this.tasks = tasks;
        }

        public String getSeries() {
            return series;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        public String getProductID() {
            return productID;
        }

        public String getPayment(){
            return payment;
        }

        public String getUrl() {
            return url;
        }

        public String getPrice() {
            return price;
        }

        public String getStore() {
            return store;
        }

        public int getTasks() {
            return tasks;
        }


        @Override
        public String toString() {
            return "ProductObject{" +
                    "series='" + series + '\'' +
                    ", brand='" + brand + '\'' +
                    ", model='" + model + '\'' +
                    ", productID='" + productID + '\'' +
                    ", url='" + url + '\'' +
                    ", price='" + price + '\'' +
                    ", store='" + store + '\'' +
                    ", tasks='" + tasks + '\'' +
                    '}';
        }
    }
}
