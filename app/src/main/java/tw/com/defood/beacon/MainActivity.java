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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private ListView mListView;
    private BeaconParseQueryAdapter mAdapter;
    private static Hashtable<String , Boolean> mBeaconSendStatus = new Hashtable<>();

    //private TextView mBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Helper.loadDataFromServer(getApplicationContext());

        //verifyBluetooth();
        verifyLocation();

        beaconManager.bind(this);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new BeaconParseQueryAdapter(getApplicationContext(), new BeaconParseQueryAdapter.ListClickHandler() {
            @Override
            public void onDetailClick(BeaconInfo beaconInfo) {
                Helper.cacheBeaconInfo(beaconInfo);
                startActivity(new Intent(getApplicationContext(), DetailActivity.class));
            }
        });
        mListView.setAdapter(mAdapter);

        //mBeaconSendStatus = new Hashtable<>();
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
                    for (Beacon beacon : beacons) {
                        String uuid = beacon.getId1().toString();

                        logToDisplay("The beacon: " + beacon.toString() + "\nmeters: " + Helper.showDistance(beacon.getDistance()) + "\n RSSI: "+beacon.getRssi() + "\n TxPower: " + beacon.getTxPower(), beacon.getId3().toInt() );

                        if (!mBeaconSendStatus.containsKey(uuid)) {
                            mBeaconSendStatus.put(beacon.getId1().toString(), true);
                        }
                        Log.d("vic", "beacon size: " + mBeaconSendStatus.size());

                        for (String key : mBeaconSendStatus.keySet()) {
                            boolean beaconSendStatus = mBeaconSendStatus.get(key);
                            Log.d("vic", "beacon ("+ key +") beaconSendStatus: " + beaconSendStatus);

                            if (beacon.getDistance() <= 5) {
                                if (beaconSendStatus) {
                                    mBeaconSendStatus.put(key, false);
                                    Helper.sendNotification(getApplicationContext(), "Beacon" + beacon.getId3(), null, beacon.getId3().toInt());

                                    List<BeaconInfo> beaconInfos = Helper.getBeaconInfosWithUuid(uuid);
                                    if (beaconInfos == null) {
                                        return;
                                    }
                                    for (BeaconInfo beaconInfo : beaconInfos) {
                                        //beaconInfo.setIsSendNotificatioin(Helper.getTimeStamp());

                                        try {
                                            beaconInfo.pin();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    mAdapter.loadObjects();

                                    Log.d("vic", "enter beacon ("+ key +") send status: " + false);

                                }
                            } else {
                                Log.d("vic", "exit beacon (" + key + ") send status: " + true);
                                //mBeaconSendStatus.put(key, true);
                            }

                        }


                    }



                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
//                    Beacon beacon = beacons.iterator().next();
                    //logToDisplay("The beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away. ("+firstBeacon.getRssi()+")", firstBeacon.getId3().toInt() );

//                    String uuid = beacon.getId1().toString();
//                    String major = beacon.getId2().toString();
//                    String minor = beacon.getId3().toString();


//                    if (firstBeacon.getDistance() <= 2) {
//                        openVideoFromLocal(major, minor);
//
//                    } else if (firstBeacon.getDistance() > 2 && firstBeacon.getDistance() < 5) {
//                        openWebsiteFromLocal(major, minor);
//                    }

//                    if (beacon.getDistance() <= 3) {
                        //sendNotification(uuid, false);
                        //Helper.sendNotification(getApplicationContext(), "", null, 0);
                        //mAdapter.loadObjects();
//                    } else {
                        //sendNotification(uuid, true);
                        //Helper.isSendNotificatioin(uuid, false);
//                    }

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }

//    private void sendNotification(String uuid, boolean isSendNotification) {
//        List<BeaconInfo> beaconInfos = Helper.getBeaconInfosWithUuid(uuid);
//
//        if (beaconInfos == null) {
//            return;
//        }
//
//        for (BeaconInfo beaconInfo : beaconInfos) {
//            if (beaconInfo.getIsSendNotificatioin()) {
//                beaconInfo.setIsSendNotificatioin(isSendNotification);
//                try {
//                    beaconInfo.pin();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        Helper.sendNotification(getApplicationContext(), beaconInfos.get(0).getName(), null, 2);
//        //mAdapter.loadObjects();
//
//    }

//    @DebugLog
//    private void openVideoFromLocal(String major, String minor) {
//
//        BeaconInfo beaconInfo = Helper.getUrlWithMajorMinor(major, minor, "video");
//
//        if (beaconInfo != null && beaconInfo.getIsYoutubeOpen()) {
//            beaconInfo.setIsYoutubeOpen(false);
//
//            String videoId = beaconInfo.getUrl().replace("https://www.youtube.com/watch?v=", "");
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
//            intent.putExtra("VIDEO_ID", videoId);
//
//            String id = major + minor + "2";
//            Helper.sendNotification(getApplicationContext(), beaconInfo.getName() + " (" + major + "," + minor + ") " + "開啟導灠影片", intent, Integer.parseInt(id));
//        }
//    }

//    @DebugLog
//    private void openWebsiteFromLocal(String major, String minor) {
//        BeaconInfo beaconInfo = Helper.getUrlWithMajorMinor(major, minor, "website");
//
//        if (beaconInfo != null && beaconInfo.getIsWebsiteOpen()) {
//            beaconInfo.setIsWebsiteOpen(false);
//
//            String url = beaconInfo.getUrl();
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//
//            String id = major + minor + "3";
//            Helper.sendNotification(getApplicationContext(), beaconInfo.getName() + " (" + major + "," + minor + ") " + "開啟購物網站", intent, Integer.parseInt(id));
//        }
//    }

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
