package tw.com.defood.beacon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;

public class MapActivity extends AppCompatActivity implements BeaconConsumer {
    private static ImageView mDemoPiint;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private static Hashtable<String , Boolean> mBeaconSendStatus = new Hashtable<>();

//    public static class BeaconPosition {
//        public final String name;
//        public final int x;
//        public final int y;
//
//        public BeaconPosition(String name, int x, int y) {
//            this.name = name;
//            this.x = x;
//            this.y = y;
//        }
//    }


    private ImageView map1;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Helper.verifyBluetooth(this);
        Helper.verifyLocation(this);

        ParseQuery<BeaconMap> queryMap = BeaconMap.getQuery();
        try {
            List<BeaconMap> maps = queryMap.find();
            ParseObject.unpinAll(maps);
            ParseObject.pinAll(maps);
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        ParseQuery<BeaconPosition> queryPosition = BeaconPosition.getQuery();
        try {
            List<BeaconPosition> positions = queryPosition.find();
            ParseObject.unpinAll(positions);
            ParseObject.pinAll(positions);
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        ParseQuery<BeaconInfo> queryInfo = BeaconInfo.getQuery();
        try {
            List<BeaconInfo> infos = queryInfo.find();
            ParseObject.unpinAll(infos);
            ParseObject.pinAll(infos);
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ImageAdapter(this));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Helper.mMapPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        beaconManager.bind(this);


//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int ScreenWidth = dm.widthPixels;   //螢幕的寬
//        int ScreenHeight = dm.heightPixels;  //螢幕的高

//        Log.i("vic", "width: " + ScreenWidth + ", height: " + ScreenHeight);
//        Log.i("vic", "widht/10: " + ScreenWidth / 10);
//        Log.i("vic", "Height/18: " + ScreenHeight / 18);

        //mRoot = (FrameLayout) findViewById(R.id.root);

        //initBeaconPosition();
        //putBeaconOnMap();
        //AllBeaconPositionOnMap();

    }

//    private void AllBeaconPositionOnMap() {
//        TableLayout layout = new TableLayout(this);
//        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        );
//
//        for (int row = 0; row < 18; row++) {
//            TableRow tableRow = new TableRow(this);
//            for (int col = 0; col < 10; col++) {
//                ImageView point = new ImageView(this);
//                point.setLayoutParams(new TableRow.LayoutParams(72, 62));
//                point.setBackgroundResource(R.drawable.point);
//                point.setTag(new BeaconPosition("", col, row));
//
//                point.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        BeaconPosition beaconPosition = (BeaconPosition) v.getTag();
//                        Toast.makeText(MapActivity.this, "(col " + beaconPosition.x + ", row " + beaconPosition.y + ") ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                tableRow.addView(point, col);
//
//
//            }
//            layout.addView(tableRow);
//        }
//
//        mRoot.addView(layout, layoutParams);
//    }


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

        mPager.setCurrentItem(Helper.mMapPosition);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @DebugLog
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d("vic", "beacon size1: " + beacons.size());
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        String uuid = beacon.getId1().toString();

                        //logToDisplay("The beacon: " + beacon.toString() + "\nmeters: " + Helper.showDistance(beacon.getDistance()) + "\n RSSI: " + beacon.getRssi() + "\n TxPower: " + beacon.getTxPower(), beacon.getId3().toInt());

                        if (!mBeaconSendStatus.containsKey(uuid)) {
                            mBeaconSendStatus.put(beacon.getId1().toString(), true);
                        }
                        Log.d("vic", "beacon size2: " + mBeaconSendStatus.size());

                        for (String key : mBeaconSendStatus.keySet()) {
                            boolean beaconSendStatus = mBeaconSendStatus.get(key);
                            Log.d("vic", "beacon (" + key + ") beaconSendStatus: " + beaconSendStatus);

                            if (beacon.getDistance() <= 5) {
                                if (beaconSendStatus) {
                                    mBeaconSendStatus.put(key, false);

                                    //Helper.sendNotification(getApplicationContext(), "Beacon" + beacon.getId3(), null, beacon.getId3().toInt());
                                    Helper.sendNotification(getApplicationContext(), "歡迎您，您將可以收到各種優惠。", null, beacon.getId3().toInt());

                                    List<BeaconInfo> beaconInfos = Helper.getBeaconInfosWithUuid(uuid);
                                    if (beaconInfos == null) {
                                        return;
                                    }
                                    for (BeaconInfo beaconInfo : beaconInfos) {
                                        //beaconInfo.setIsSendNotificatioin(Helper.getTimeStamp());

                                        try {
                                            beaconInfo.pin();
                                        } catch (ParseException e) {
                                            //e.printStackTrace();
                                        }

                                    }
                                    //mAdapter.loadObjects();

                                    Log.d("vic", "enter beacon (" + key + ") send status: " + false);

                                    blinkPoint(mDemoPiint);
                                }
                            } else {
                                Log.d("vic", "exit beacon (" + key + ") send status: " + true);
                                mBeaconSendStatus.put(key, true);
                            }

                        }


                    }


                } else { // turn off beacon

//                    for (String key : mBeaconSendStatus.keySet()) {
//                        mBeaconSendStatus.put(key, true);
//                        boolean beaconSendStatus = mBeaconSendStatus.get(key);
//                        Log.d("vic", "beacon (" + key + ") beaconSendStatus: " + beaconSendStatus);
//                    }

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }

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

            //Intent intent = new Intent();
            //intent.setClass(this, SettingActivity.class);
            //startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class ImageAdapter extends PagerAdapter {
        private List<BeaconMap> mBeaconMaps;
        ArrayList<BeaconPosition> mMap1BeasonPositions = new ArrayList<>();
        ArrayList<BeaconPosition> mMap2BeasonPositions = new ArrayList<>();

        private final int[] IMAGE_URLS = new int[] {
                R.drawable.map1,
                R.drawable.map2
        };

        private final LayoutInflater inflater;
        private int mDx;
        private int mDy;
        private int mMapPosition;
        private int mTransX;
        private int mTransY;

        public ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            ParseQuery<BeaconMap> queryMap = BeaconMap.getQuery();
            queryMap.fromLocalDatastore();
            queryMap.orderByAscending("imagePath");
            try {
                 mBeaconMaps = queryMap.find();
            } catch (ParseException e) {
                //e.printStackTrace();
            }

            //initBeaconPosition();
        }

//        private void initBeaconPosition() {
//            mMap1BeasonPositions.add(new BeaconPosition("B01", 6, 4));
//            mMap1BeasonPositions.add(new BeaconPosition("B02", 6, 6));
//            mMap1BeasonPositions.add(new BeaconPosition("B03", 4, 9));
//            mMap1BeasonPositions.add(new BeaconPosition("B04", 3, 10));
//            mMap1BeasonPositions.add(new BeaconPosition("B05", 1, 12));
//            mMap1BeasonPositions.add(new BeaconPosition("B06", 3, 13));
//            mMap1BeasonPositions.add(new BeaconPosition("B07", 6, 13));
//            mMap1BeasonPositions.add(new BeaconPosition("B08", 5, 11));
//
//            mMap2BeasonPositions.add(new BeaconPosition("B09", 6, 12));
//            mMap2BeasonPositions.add(new BeaconPosition("B10", 7, 11));
//            mMap2BeasonPositions.add(new BeaconPosition("B11", 6, 10));
//            mMap2BeasonPositions.add(new BeaconPosition("B12", 5, 9));
//            mMap2BeasonPositions.add(new BeaconPosition("B13", 1, 11));
//            mMap2BeasonPositions.add(new BeaconPosition("B14", 3, 14));
//        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View root = inflater.inflate(R.layout.item_pager_image, view, false);
            assert root != null;
            final FrameLayout mMap = (FrameLayout) root.findViewById(R.id.map);
            final BeaconMap beaconMap = mBeaconMaps.get(position);
            int imageResId = getResources().getIdentifier(beaconMap.getImagePath(), "drawable", getPackageName());
            final ImageView map = new ImageView(MapActivity.this);
            map.setImageResource(imageResId);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            map.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //map.setAdjustViewBounds(true);
            mMap.addView(map, layoutParams);

            //mMap.setBackgroundResource(imageResId);

            map.post(new Runnable() {
                @Override
                public void run() {
                    // Get image matrix values and place them in an array
                    float[] f = new float[9];
                    map.getImageMatrix().getValues(f);

                    // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
                    final float scaleX = f[Matrix.MSCALE_X];
                    final float scaleY = f[Matrix.MSCALE_Y];

                    mTransX = (int) f[Matrix.MTRANS_X];
                    mTransY = (int) f[Matrix.MTRANS_Y];

                    // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
                    final Drawable d = map.getDrawable();
                    final int origW = d.getIntrinsicWidth();
                    final int origH = d.getIntrinsicHeight();

                    // Calculate the actual dimensions
                    final int actW = Math.round(origW * scaleX);
                    final int actH = Math.round(origH * scaleY);

                    Log.e("DBG", "["+origW+","+origH+"] -> ["+actW+","+actH+"] & scales: x="+scaleX+" y="+scaleY+"trans: x= "+mTransX+" y="+mTransY);


                    Log.i("vic", "mMap padding left: " + map.getScaleX());
                    Log.i("vic", "mMap paddin right: " + map.getScaleY());

                    // Get the ImageView and its bitmap
                    Drawable drawing = map.getDrawable();
                    Log.i("vic", "mMap drawing width: " + drawing.getIntrinsicWidth());
                    Log.i("vic", "mMap drawing height: " + drawing.getIntrinsicHeight());


                    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

                    // Get current dimensions
                    int width = bitmap.getScaledWidth(new DisplayMetrics());
                    int height = bitmap.getScaledHeight(new DisplayMetrics());
                    Log.i("vic", "mMap bitmap width: " + width);
                    Log.i("vic", "mMap bitmap height: " + height);


                    Log.i("vic", "mMap measured width: " + map.getMeasuredWidth());
                    Log.i("vic", "mMap measured height: " + map.getMeasuredHeight());


                    Log.i("vic", "mMap width: " + map.getWidth());
                    Log.i("vic", "mMap height: " + map.getHeight());

                    int mapWidth = map.getWidth();
                    int mapHeight = map.getHeight();

                    mDx = actW / 10;
                    mDy = actH / 18;

                    putBeaconOnMap(beaconMap, mMap);
                }
            });

            view.addView(root, 0);
            return root;
        }

        private void putBeaconOnMap(BeaconMap beaconMap, FrameLayout map) {

            ParseQuery<BeaconPosition> queryPosition = BeaconPosition.getQuery();
            queryPosition.fromLocalDatastore();
            queryPosition.whereEqualTo("BeaconMap", beaconMap);
            try {
                List<BeaconPosition> beaconPositions = queryPosition.find();
                putBeaconOnMap(beaconPositions, map);
            } catch (ParseException e) {
                //e.printStackTrace();
            }



//            switch (position) {
//                case 0:
//                    putBeaconOnMap(mMap1BeasonPositions, mMap);
//                    break;
//
//                case 1:
//                    putBeaconOnMap(mMap2BeasonPositions, mMap);
//                    break;
//            }

        }

        private void putBeaconOnMap(final List<BeaconPosition> beasonPositions, FrameLayout mMap) {

            for (BeaconPosition beaconPosition : beasonPositions) {

                int x = mDx * Integer.valueOf(beaconPosition.getX());
                int y = mDy * Integer.valueOf(beaconPosition.getY());

                ImageView point = new ImageView(MapActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mDx + 30 , mDy + 30);
//                layoutParams.setMargins(x -12, y - 19, 0, 0);
                layoutParams.setMargins(x + mTransX - 11 , y + mTransY - 19, 0, 0);
                point.setLayoutParams(layoutParams);
//                point.setBackgroundResource(R.drawable.point);
                point.setTag(beaconPosition);
                //point.setVisibility(View.INVISIBLE);

                point.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BeaconPosition beaconPosition = (BeaconPosition) v.getTag();
                        //Toast.makeText(MapActivity.this, "(x " + beaconPosition.getX() + ", y " + beaconPosition.getY() + ") ", Toast.LENGTH_SHORT).show();
                        Helper.cacheBeaconPosition(beaconPosition);
                        Intent intent = new Intent();
                        intent.setClass(MapActivity.this, InfoActivity.class);
                        startActivity(intent);
                    }
                });

                mMap.addView(point);

                if (beaconPosition.getX().equals("6") && beaconPosition.getY().equals("4")) {
                    //blinkPoint(point);

                    MapActivity.mDemoPiint = point;
                }
            }
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);        }
    }

    @DebugLog
    private void blinkPoint(final ImageView point) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                point.setImageResource(R.drawable.point);
                AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
                alphaAnimation1.setDuration(500);
                alphaAnimation1.setRepeatCount(Animation.INFINITE);
                alphaAnimation1.setRepeatMode(Animation.REVERSE);
                point.setAnimation(alphaAnimation1);
                alphaAnimation1.start();
            }
        });

    }


}
