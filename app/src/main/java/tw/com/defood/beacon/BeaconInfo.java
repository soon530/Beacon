package tw.com.defood.beacon;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;

@ParseClassName("Beacon")
public class BeaconInfo extends ParseObject {
    private boolean mIsYoutubeOpen = true;
    private boolean mIsWebsiteOpen = true;


    private static ParseQuery<BeaconInfo> query;

    public static ParseQuery<BeaconInfo> getQuery() {
        return ParseQuery.getQuery(BeaconInfo.class);
    }

    public String getName() {
        String value = getString("name");
        return value;
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getUuid() {
        String value = getString("uuid");
        return value;
    }

    public void setUuid(String value) {
        put("uuid", value);
    }


    public String getMajor() {
        String value = getString("major");
        return value;
    }

    public void setMajor(String value) {
        put("major", value);
    }

    public String getMinor() {
        String value = getString("minor");
        return value;
    }

    public void setMinor(String value) {
        put("minor", value);
    }

    public String getTitle() {
        String value = getString("title");
        return value;
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getType() {
        String value = getString("type");
        return value;
    }

    public void setType(String value) {
        put("type", value);
    }

    public String getUrl() {
        String value = getString("url");
        return value;
    }

    public void setUrl(String value) {
        put("url", value);
    }

    public boolean getIsYoutubeOpen() {
        return mIsYoutubeOpen;
    }

    public void setIsYoutubeOpen(boolean value) {
        mIsYoutubeOpen = value;
    }

    public boolean getIsWebsiteOpen() {
        return mIsWebsiteOpen;
    }

    public void setIsWebsiteOpen(boolean value) {
        mIsWebsiteOpen = value;
    }


}
