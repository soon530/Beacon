package tw.com.defood.beacon;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import hugo.weaving.DebugLog;

public class Myapp extends Application implements BootstrapNotifier {
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;

    private static final String APPLICATION_ID = "eErBLqF1Imny0NSMjqkZpVOxODv4ngAZdew7TktX";
    private static final String CLIENT_KEY = "Z3GjMkBg7HreWEWniwouV5faNAFVHVtde3PUZXw5";


    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(BeaconMap.class);
        ParseObject.registerSubclass(BeaconPosition.class);
        ParseObject.registerSubclass(BeaconInfo.class);

        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        Region region = new Region("backgroundRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // set the duration of the scan to be 10 seconds
        beaconManager.setBackgroundScanPeriod(10000l);
        // set the time between each scan to be 60 seconds
        beaconManager.setBackgroundBetweenScanPeriod(60000l);

//        beaconManager.setForegroundScanPeriod(1000l);
//        beaconManager.setForegroundBetweenScanPeriod(10000l);
    }


    @DebugLog
    @Override
    public void didEnterRegion(Region region) {

//        if (!haveDetectedBeaconsSinceBoot) {
//            Intent intent = new Intent(this, SplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            this.startActivity(intent);
//            haveDetectedBeaconsSinceBoot = true;
//            Helper.sendNotification(this, "觀迎來到滷味博物館，已將Beacon功能開啟。", new Intent(this, MapActivity.class), 1);
//        }

    }

    @DebugLog
    @Override
    public void didExitRegion(Region region) {

    }

    @DebugLog
    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }


}
