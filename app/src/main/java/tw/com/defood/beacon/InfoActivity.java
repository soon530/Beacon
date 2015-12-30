package tw.com.defood.beacon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.annotation.Target;
import java.util.List;

import hugo.weaving.DebugLog;

public class InfoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private ImageView mBack;
    private BeaconInfo mVideo;
    private BeaconInfo mCoupon;
    private BeaconInfo mProduct;
    private ImageView mMap;
    private YouTubePlayer mYoutubePlayer;
    private ImageView mNear;
    private ImageView mFar;
    private TextView mVideoContent;
    private LinearLayout mVideoContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final BeaconPosition beaconPosition = Helper.getBeaconPositionFromCache();

        ParseQuery<BeaconInfo> queryInfo = BeaconInfo.getQuery();
        queryInfo.fromLocalDatastore();
        queryInfo.whereEqualTo("BeaconPosition", beaconPosition);
        queryInfo.whereEqualTo("type", "video");
        try {
            mVideo = queryInfo.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        queryInfo.fromLocalDatastore();
        queryInfo.whereEqualTo("BeaconPosition", beaconPosition);
        queryInfo.whereEqualTo("type", "coupon");
        try {
            mCoupon = queryInfo.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        queryInfo.fromLocalDatastore();
        queryInfo.whereEqualTo("BeaconPosition", beaconPosition);
        queryInfo.whereEqualTo("type", "product");
        try {
            mProduct = queryInfo.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(beaconPosition.getTitle());

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        // Initializing video player with developer key
        youTubeView.initialize("AIzaSyAhq1fQGZDJ1B9w3MjaRZiS6aWVebzKDSs", this);

        mVideoContentLayout = (LinearLayout) findViewById(R.id.video_content_layout);

        mVideoContent = (TextView) findViewById(R.id.video_content);
        mVideoContent.setText(mVideo.getContent());

        if (mVideo.getImagePath() == null) {
            youTubeView.setVisibility(View.GONE);
            mVideoContentLayout.setVisibility(View.GONE);
        }


        mMap = (ImageView) findViewById(R.id.map);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switch (Helper.mMapPosition) {
            case 0:
                mMap.setImageResource(R.drawable.btn_1f);
                break;
            case 1:
                mMap.setImageResource(R.drawable.btn_2f);
                break;
        }

        mNear = (ImageView) findViewById(R.id.near);
        int id = Integer.parseInt(beaconPosition.getName().replace("ib",""));
        int nearId = id + 1;
        String nearName = "";
        if (nearId <= 9) {
            nearName = "ib" + "0" + nearId;
        } else {
            nearName = "ib" + nearId;
        }
        int nearResId = getResources().getIdentifier(nearName, "drawable", getPackageName());
        mNear.setImageResource(nearResId);
        final String finalNearName = nearName;
        mNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<BeaconPosition> queryPosition = BeaconPosition.getQuery();
                queryPosition.fromLocalDatastore();
                queryPosition.whereEqualTo("name", finalNearName);
                BeaconPosition beaconPositions = null;
                try {
                     beaconPositions = queryPosition.getFirst();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (beaconPosition == null ) return;

                Helper.cacheBeaconPosition(beaconPositions);
                Intent intent = new Intent();
                intent.setClass(InfoActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        mFar = (ImageView) findViewById(R.id.far);
        int farId = id + 2;
        String farName = "";
        if (nearId <= 9) {
            farName = "ib" + "0" + farId;
        } else {
            farName = "ib" + farId;
        }
        int farResId = getResources().getIdentifier(farName, "drawable", getPackageName());
        mFar.setImageResource(farResId);
        mFar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





    }

    @DebugLog
    @Override
    protected void onResume() {
        super.onResume();
        if (mYoutubePlayer != null)
            mYoutubePlayer.loadVideo(mVideo.getImagePath());

        //getYouTubePlayerProvider().initialize("AIzaSyAhq1fQGZDJ1B9w3MjaRZiS6aWVebzKDSs", this);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (mYoutubePlayer != null)
        //    mYoutubePlayer.release();
    }

    @DebugLog
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (mVideo == null || mVideo.getImagePath() == null) {
            Toast.makeText(this, "很抱歉，目前沒有導覽影片!", Toast.LENGTH_LONG).show();

            return;
        }

        if (!wasRestored) {
            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            //Ol0gLmHQkwI
            youTubePlayer.loadVideo(mVideo.getImagePath());
            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @DebugLog
                @Override
                public void onLoading() {

                }

                @DebugLog
                @Override
                public void onLoaded(String s) {
                    youTubePlayer.play();
                }

                @DebugLog
                @Override
                public void onAdStarted() {

                }

                @DebugLog
                @Override
                public void onVideoStarted() {

                }

                @DebugLog
                @Override
                public void onVideoEnded() {

                }

                @DebugLog
                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });

        } else {
            youTubePlayer.play();
        }

        mYoutubePlayer = youTubePlayer;
    }

    @DebugLog
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyAhq1fQGZDJ1B9w3MjaRZiS6aWVebzKDSs", this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        //super.onSaveInstanceState(bundle);
    }
}
