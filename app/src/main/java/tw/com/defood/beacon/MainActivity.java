package tw.com.defood.beacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    //private TextView mBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.loadDataFromServer();

        verifyBluetooth();
        verifyLocation();

        beaconManager.bind(this);
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    private void verifyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @DebugLog
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    logToDisplay("The beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away. ("+firstBeacon.getRssi()+")", firstBeacon.getId3().toInt() );

                    String major = firstBeacon.getId2().toString();
                    String minor = firstBeacon.getId3().toString();


                    if (firstBeacon.getDistance() <= 2) {
                        openVideoFromLocal(major, minor);

                    } else if (firstBeacon.getDistance() > 2 && firstBeacon.getDistance() < 5) {
                        openWebsiteFromLocal(major, minor);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }

    @DebugLog
    private void openVideoFromLocal(String major, String minor) {

        BeaconInfo beaconInfo = Helper.getUrlWithMajorMinor(major, minor, "video");

        if (beaconInfo != null && beaconInfo.getIsYoutubeOpen()) {
            beaconInfo.setIsYoutubeOpen(false);

            String videoId = beaconInfo.getUrl().replace("https://www.youtube.com/watch?v=", "");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            intent.putExtra("VIDEO_ID", videoId);

            String id = major + minor + "2";
            Helper.sendNotification(getApplicationContext(), beaconInfo.getName() + " (" + major + "," + minor + ") " + "開啟導灠影片", intent, Integer.parseInt(id));
        }
    }

    @DebugLog
    private void openWebsiteFromLocal(String major, String minor) {
        BeaconInfo beaconInfo = Helper.getUrlWithMajorMinor(major, minor, "website");

        if (beaconInfo != null && beaconInfo.getIsWebsiteOpen()) {
            beaconInfo.setIsWebsiteOpen(false);

            String url = beaconInfo.getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            String id = major + minor + "3";
            Helper.sendNotification(getApplicationContext(), beaconInfo.getName() + " (" + major + "," + minor + ") " + "開啟購物網站", intent, Integer.parseInt(id));
        }
    }

    private void logToDisplay(final String line, final int id) {
        runOnUiThread(new Runnable() {
            public void run() {
                switch (id) {
                    case 9:
                        TextView b1 = (TextView) MainActivity.this.findViewById(R.id.b1);
                        b1.setText(line);
                        break;

                    case 0:
                        TextView b2 = (TextView) MainActivity.this.findViewById(R.id.b2);
                        b2.setText(line);
                        break;

                }

            }
        });
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
