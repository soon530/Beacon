package tw.com.defood.beacon;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("BeaconMap")
public class BeaconMap extends ParseObject {

    public static ParseQuery<BeaconMap> getQuery() {
        return ParseQuery.getQuery(BeaconMap.class);
    }

    public String getName() {
        String value = getString("name");
        return value;
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getImagePath() {
        String value = getString("imagePath");
        return value;
    }

    public void setImagePath(String value) {
        put("imagePath", value);
    }

}
