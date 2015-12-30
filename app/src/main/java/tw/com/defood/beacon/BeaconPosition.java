package tw.com.defood.beacon;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("BeaconPosition")
public class BeaconPosition extends ParseObject {

    public static ParseQuery<BeaconPosition> getQuery() {
        return ParseQuery.getQuery(BeaconPosition.class);
    }

    public String getName() {
        String value = getString("name");
        return value;
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getTitle() {
        String value = getString("title");
        return value;
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getImagePath() {
        String value = getString("imagePath");
        return value;
    }

    public void setImagePath(String value) {
        put("imagePath", value);
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

    public String getX() {
        String value = getString("x");
        return value;
    }

    public void setX(String value) {
        put("x", value);
    }

    public String getY() {
        String value = getString("y");
        return value;
    }

    public void setY(String value) {
        put("y", value);
    }

    public BeaconMap getBeaconMap() {
        return (BeaconMap) getParseObject("BeaconMap");
    }

    public void setBeaconMap(BeaconMap value) {
        put("BeaconMap", value);
    }
}
