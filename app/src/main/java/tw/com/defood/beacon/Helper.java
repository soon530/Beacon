package tw.com.defood.beacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.altbeacon.beacon.BeaconManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;

public class Helper {

    private static BeaconInfo mBeaconInfo;
//    public static List<BeaconMap> mMap;
//    public static List<BeaconPosition> mPosition;
//    public static List<BeaconInfo> mInfo;

    private static BeaconPosition mBeaconPosition;
    public static int mMapPosition = 0;


    public static void sendNotification(Context context, String text, Intent intent, int id) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("台灣滷味博物館")
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[]{1000, 1000});
                        //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, SplashActivity.class));
        //stackBuilder.addNextIntent(intent);
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

        ParseQuery<BeaconMap> query = BeaconMap.getQuery();
        query.findInBackground(new FindCallback<BeaconMap>() {

            @DebugLog
            @Override
            public void done(List<BeaconMap> maps, ParseException parseException) {
                if (isSuccess(parseException)) {
                    //mMaps = maps;
                    //EventBus.getDefault().post(maps);
                } else {
                    EventBus.getDefault().post(parseException);
                }
            }
        });
    }

    @DebugLog
    public static void pinBeacons(List<BeaconInfo> beacons, final Context context) {
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
                    Toast.makeText(context, "The data of all beacons has downloaded.", Toast.LENGTH_LONG).show();
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

    public static void cacheBeaconInfo(BeaconInfo beaconInfo) {
        mBeaconInfo = beaconInfo;
    }

    public static BeaconInfo getBeaconInfoFromCache() {
        return mBeaconInfo;
    }

//    @DebugLog
//    public static void isSendNotificatioin(String uuid, boolean isSendNotification) {
//        ParseQuery<BeaconInfo> query = BeaconInfo.getQuery();
//
//        query.whereEqualTo("uuid", uuid);
//
//        try {
//            BeaconInfo beaconInfo = query.getFirst();
//
//            beaconInfo.setIsSendNotificatioin(isSendNotification);
//
//        } catch (ParseException e) {
//            System.out.println(e);
//        }
//
//    }

    @DebugLog
    public static List<BeaconInfo> getBeaconInfosWithUuid(String uuid) {
        ParseQuery<BeaconInfo> query = BeaconInfo.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("uuid", uuid);

        try {
            List<BeaconInfo> beaconInfos = query.find();
            return  beaconInfos;
        } catch (ParseException e) {
            System.out.println(e);
            return null;
        }
    }

    public static String getTimeStamp() {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(nowDate);
    }

    public static String showDistance(Double distance) {
        DecimalFormat df = new DecimalFormat("##.###");
        return df.format(distance);
    }

    public static void verifyBluetooth(final Activity activity) {

        try {
            if (!BeaconManager.getInstanceForApplication(activity).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("您的藍牙沒有開啟");
                builder.setMessage("請在設定中開啟藍牙，並重新啟動「i必看!台灣滷味博物館」。");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        final Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity( intent);
                        activity.finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("你的手機不支援Beacon");
            builder.setMessage("很抱歉，您無法使用Beacon來收看導覽。");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    activity.finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static void verifyLocation(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("App需要存取位置資訊");
                builder.setMessage("請充許App使用位置，以便可以在背景接收Beacon訊號。");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }

    }


    public static void cacheBeaconPosition(BeaconPosition beaconPosition) {
        mBeaconPosition = beaconPosition;
    }

    public static BeaconPosition getBeaconPositionFromCache() {
        return mBeaconPosition;
    }
}
