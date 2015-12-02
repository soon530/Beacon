package tw.com.defood.beacon;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private BeaconAdapter mBeaconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        beaconManager.bind(this);
        initView();
    }

    private void initView() {

        ListView deviceListView = (ListView) findViewById(R.id.list);
        mBeaconAdapter = new BeaconAdapter(this, R.layout.item_beacon,
                new ArrayList<Beacon>());
        deviceListView.setAdapter(mBeaconAdapter);
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

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                updateList(beacons);

            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

    }

    private void updateList(final Collection<Beacon> beacons) {

        if (beacons.size() == 0) {
            return;
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBeaconAdapter.update(beacons);
            }
        });
    }

}
