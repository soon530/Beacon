package tw.com.defood.beacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import hugo.weaving.DebugLog;

public class Helper {

    public static void sendNotification(Context context, String text, Intent intent, int id) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("台灣滷味博物館")
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[]{1000, 1000})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addNextIntent(new Intent(context, MainActivity.class));
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    public static boolean isSuccess(ParseException e) {
        if (e == null) {
            return true;
        } else {
            return false;
        }
    }

    public static void loadDataFromServer() {
        ParseQuery<BeaconInfo> query = BeaconInfo.getQuery();
        query.findInBackground(new FindCallback<BeaconInfo>() {

            @DebugLog
            @Override
            public void done(List<BeaconInfo> beacons, ParseException parseException) {
                if (isSuccess(parseException)) {
                    pinBeacons(beacons);
                    //Helper.cacheBeacons(beacons);
                    //EventBus.getDefault().post(favorites);
                } else {
                    //EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    @DebugLog
    public static void pinBeacons(List<BeaconInfo> beacons) {
        ParseObject.unpinAllInBackground(beacons, new DeleteCallback() {
            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {

                } else {
                    //EventBus.getDefault().post(parseException);
                }
            }
        });

        ParseObject.pinAllInBackground(beacons, new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (isSuccess(parseException)) {

                } else {
                    //EventBus.getDefault().post(parseException);
                }
            }
        });
    }


    public static BeaconInfo getUrlWithMajorMinor(String major, String minor, String type) {
        ParseQuery<BeaconInfo> query = BeaconInfo.getQuery();

        query.whereEqualTo("major", major);
        query.whereEqualTo("minor", minor);
        query.whereEqualTo("type", type);

        try {
            BeaconInfo beaconInfo = query.getFirst();
            return  beaconInfo;
        } catch (ParseException e) {
            return null;
        }

    }
}
