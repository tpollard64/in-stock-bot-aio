import loaders.Products;
import loaders.Proxy;
import loaders.Proxy.ProxyObject;
import stores.BestBuy;
import stores.OutputColors;
import thread.ThreadPool;
import loaders.Products.ProductObject;
import java.util.ArrayList;
import java.util.Random;

public class Bot  implements OutputColors {

    static ThreadPool pool;
    static ArrayList<ProxyObject> proxies;
    static ArrayList<ProxyObject> inUse;
    static ArrayList<ProductObject> products;
    static int taskCount = 0;


    public static void init(){
        proxies = Proxy.load("src/main/proxies/proxies.txt");
        inUse = new ArrayList<ProxyObject>();
        products = Products.load("src/main/products/bestbuy.json");

        assert products != null;
        for (Object product : products.toArray())
            taskCount += ((ProductObject) product).getTasks();

        pool = new ThreadPool(taskCount);
    }
    public static void main(String[] args){
        Bot bot = new Bot();
        bot.run();
    }

    private void run(){
        init();
        if (products == null) {
            return;
        }
        products.forEach(p -> {
            for (int start = 0; start < p.getTasks(); start++){
                pool.run(new BestBuy(getRandomProxy(), p.getUrl(), p.getSeries(), p.getBrand(), p.getModel(), p.getPrice()));
            }
        });

        System.out.println("Proxies loaded: " + (proxies.size() +  " Proxies in use: " + inUse.size()) + "\nTasks loaded: " + taskCount + "\nPress Enter to start tasks.");
        try{System.in.read();}
        catch(Exception e){
            e.printStackTrace();
        }
        pool.flush();
    }

    public static ProxyObject getRandomProxy() {
        int proxyCount = proxies.size();
        int usedCount = inUse.size();

        if(proxyCount == 0 && usedCount == 0)
            return null;

        int index = new Random().nextInt(proxyCount != 0 ? proxyCount : usedCount);
        if(proxyCount != 0) {
            ProxyObject proxy = proxies.remove(index);
            inUse.add(proxy);
            return proxy;
        } else
            return inUse.get(index);

    }


}
