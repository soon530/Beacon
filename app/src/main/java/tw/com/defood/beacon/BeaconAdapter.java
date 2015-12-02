package tw.com.defood.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hugo.weaving.DebugLog;

public class BeaconAdapter extends ArrayAdapter<Beacon> {
    private final int mResId;
    private List<Beacon> mList;
    private final LayoutInflater mInflater;

    public BeaconAdapter(Context context, int resId, List<Beacon> beacons) {
        super(context, resId, beacons);
        mResId = resId;
        mList = beacons;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Beacon beacon = (Beacon) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(mResId, null);
        }
        TextView beaconInfo = (TextView) convertView.findViewById(R.id.beaconInfo);
        beaconInfo.setText(beacon.toString() + "\nMeter:" + beacon.getDistance() + "\nRSSI:" + beacon.getRssi() + "\nTxPower:" + beacon.getTxPower());

        return convertView;
    }

    @DebugLog
    public void update(Collection<Beacon> beacons) {

        mList.clear();
        for (Beacon beacon : beacons) {
            mList.add(beacon);
        }

        // sort by RSSI
        Collections.sort(mList, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon lhs, Beacon rhs) {
                if (lhs.getRssi() == 0) {
                    return 1;
                } else if (rhs.getRssi() == 0) {
                    return -1;
                }
                if (lhs.getRssi() > rhs.getRssi()) {
                    return -1;
                } else if (lhs.getRssi() < rhs.getRssi()) {
                    return 1;
                }
                return 0;
            }
        });

        notifyDataSetChanged();
    }

}
