package com.kusochekmela.bingwallpaper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kiseleva on 18.10.2017.
 */

public class WallPaperReceiver extends BroadcastReceiver {

    private static final String TAG = "com.kusochek.mela.bingwallpaper";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private Handler mHandler;

    private final String locale = "ru-RU";

    @Override
    public void onReceive(Context context, Intent intent) {
       // File logfile = new File(context.getFilesDir(), "log.txt");

            //  Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();
//            logToFile(context, TAG,"Start event");
            final Context localContext = context;
            mHandler = new Handler(Looper.getMainLooper());

            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=" + locale)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
//                    logToFile(localContext, TAG,"Request failed \n" + e.getMessage() );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonData = response.body().string();
                        JSONObject Jobject = null;
                        try {
                            Jobject = new JSONObject(jsonData);
                            Jobject = (JSONObject) Jobject.getJSONArray("images").get(0);
                            final String url = "http://www.bing.com" + Jobject.get("url").toString();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    GlideApp
                                            .with(localContext)
                                            .asBitmap()
                                            .load(url)
                                            .centerCrop()
                                            .into(new SimpleTarget<Bitmap>(dm.widthPixels, dm.heightPixels) {
                                                public void onResourceReady(Bitmap resource, Transition glideAnimation) {
                                                    WallpaperManager myWallpaperManager = WallpaperManager.getInstance(localContext);
                                                    try {
                                                        myWallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
//                                                        logToFile(localContext, TAG, "Bitmap successfully set");
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
//                                                        logToFile(localContext,TAG,"Bitmap setting failed\n"+e.getMessage());
                                                    }
                                                }
                                            });
                                }
                            });

                        } catch (JSONException e) {
//                            logToFile(localContext,TAG,"JSON parsing failed\n" + e.getMessage());
                        }
                    } else {
//                        logToFile(localContext,TAG,"Request not successfull. Status is " + response.code());
                    }
                }
            });

    }

    public void setAlarm(Context context)
    {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WallPaperReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
                AlarmManager.INTERVAL_HOUR*3, alarmIntent);
    }

    public void cancelAlarm()
    {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

    }

    private static void logToFile(Context context, String logMessageTag, String logMessage)
    {
        try
        {
            OutputStreamWriter writer = new OutputStreamWriter(
                                context.openFileOutput(context.getString(R.string.log_file),
                                Context.MODE_APPEND));
            writer.write(String.format("%1s [%2s] : %3s\r\n",
                    getDateTimeStamp(), logMessageTag, logMessage));
            writer.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Unable to log exception to file.");
        }
    }
    private static String getDateTimeStamp()
    {
        Date dateNow = java.util.Calendar.getInstance().getTime();

        return (DateFormat.getDateTimeInstance
                (DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(dateNow));
    }
}
