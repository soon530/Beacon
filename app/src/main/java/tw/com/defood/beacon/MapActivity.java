package tw.com.defood.beacon;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class MapActivity extends AppCompatActivity {


    public static class BeaconPosition {
        public final String name;
        public final int x;
        public final int y;

        public BeaconPosition(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }


    private ImageView map1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter(this));


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
    protected void onResume() {
        super.onResume();

    }

    private class ImageAdapter extends PagerAdapter {
        ArrayList<BeaconPosition> mMap1BeasonPositions = new ArrayList<>();
        ArrayList<BeaconPosition> mMap2BeasonPositions = new ArrayList<>();

        private final int[] IMAGE_URLS = new int[] {
                R.drawable.map1,
                R.drawable.map2
        };

        private final LayoutInflater inflater;
        private int mDx;
        private int mDy;

        public ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            initBeaconPosition();
        }

        private void initBeaconPosition() {
            mMap1BeasonPositions.add(new BeaconPosition("B01", 6, 4));
            mMap1BeasonPositions.add(new BeaconPosition("B02", 6, 6));
            mMap1BeasonPositions.add(new BeaconPosition("B03", 4, 9));
            mMap1BeasonPositions.add(new BeaconPosition("B04", 3, 10));
            mMap1BeasonPositions.add(new BeaconPosition("B05", 1, 12));
            mMap1BeasonPositions.add(new BeaconPosition("B06", 3, 13));
            mMap1BeasonPositions.add(new BeaconPosition("B07", 6, 13));
            mMap1BeasonPositions.add(new BeaconPosition("B08", 5, 11));

            mMap2BeasonPositions.add(new BeaconPosition("B09", 6, 12));
            mMap2BeasonPositions.add(new BeaconPosition("B10", 7, 11));
            mMap2BeasonPositions.add(new BeaconPosition("B11", 6, 10));
            mMap2BeasonPositions.add(new BeaconPosition("B12", 5, 9));
            mMap2BeasonPositions.add(new BeaconPosition("B13", 1, 11));
            mMap2BeasonPositions.add(new BeaconPosition("B14", 3, 14));
        }

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
            mMap.setBackgroundResource(IMAGE_URLS[position]);

            mMap.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("vic", "mMap width: " + mMap.getWidth());
                    Log.i("vic", "mMap height: " + mMap.getHeight());

                    int mapWidth = mMap.getWidth();
                    int mapHeight = mMap.getHeight();

                    mDx = mapWidth / 10;
                    mDy = mapHeight / 18;

                    putBeaconOnMap(position, mMap);
                }
            });

            view.addView(root, 0);
            return root;
        }

        private void putBeaconOnMap(int position, FrameLayout mMap) {
            switch (position) {
                case 0:
                    putBeaconOnMap(mMap1BeasonPositions, mMap);
                    break;

                case 1:
                    putBeaconOnMap(mMap2BeasonPositions, mMap);
                    break;
            }

        }

        private void putBeaconOnMap(final ArrayList<BeaconPosition> beasonPositions, FrameLayout mMap) {

            for (BeaconPosition beaconPosition : beasonPositions) {
                int x = mDx * beaconPosition.x;
                int y = mDy * beaconPosition.y;

                ImageView point = new ImageView(MapActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mDx, mDy);
                layoutParams.setMargins(x, y, 0, 0);
                point.setLayoutParams(layoutParams);
                point.setBackgroundResource(R.drawable.point);
                point.setTag(beaconPosition);

                point.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BeaconPosition beaconPosition = (BeaconPosition) v.getTag();
                        Toast.makeText(MapActivity.this, "(x " + beaconPosition.x + ", y " + beaconPosition.y + ") ", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(MapActivity.this, InfoActivity.class);
                        startActivity(intent);
                    }
                });

                mMap.addView(point);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);        }
    }
}
