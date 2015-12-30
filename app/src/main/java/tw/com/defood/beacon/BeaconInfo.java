package tw.com.defood.beacon;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;

@ParseClassName("BeaconInfo")
public class BeaconInfo extends ParseObject {

    public static ParseQuery<BeaconInfo> getQuery() {
        return ParseQuery.getQuery(BeaconInfo.class);
    }

    public String getType() {
        String value = getString("type");
        return value;
    }

    public void setType(String value) {
        put("type", value);
    }

    public String getImagePath() {
        String value = getString("imagePath");
        return value;
    }

    public void setImagePath(String value) {
        put("imagePath", value);
    }

    public String getTitle() {
        String value = getString("title");
        return value;
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getContent() {
        String value = getString("content");
        return value;
    }

    public void setContent(String value) {
        put("content", value);
    }

    public String getUrl() {
        String value = getString("url");
        return value;
    }

    public void setUrl(String value) {
        put("url", value);
    }


//    @Override
//    public String toString() {
//        return "\n" + super.toString() +
//                "\nname: " + getName() +
//                "\nisSendNotification: " + getIsSendNotificatioin();
//    }
}
