package alerts;
import com.twilio.rest.notify.v1.service.Notification;
import stores.Store;

import java.util.ArrayList;

public class TextAlerts {

    private static Store store;
    private static String url;
    private static final String PHONE_NUMBER = "\"+15555555555\"}"; // Your phone number goes here use +1 in front for US numbers
    private static final String SERVICE_SID = ""; //twilio service sid

    public TextAlerts(Store store){
        this.store = store;
        this.url = getStore().getUrl();
    }

    public static void alert(ArrayList<String> phoneNumbers) {
        addContacts();
        for (int i = 0; i < phoneNumbers.size(); i++) {
            Notification notification = Notification
                    .creator(SERVICE_SID)
                    .setBody(getStore().getModel()+ " is in stock \n" + getUrl()).setToBinding("{\"binding_type\":\"sms\",\"address\":" + phoneNumbers.get(i))
                    .create();
        }
    }

    public static ArrayList<String> addContacts(){
        ArrayList<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(PHONE_NUMBER);
        return phoneNumbers;
    }

    public static Store getStore(){
        return store;
    }

    public static String getUrl(){
        return url;
    }
}
