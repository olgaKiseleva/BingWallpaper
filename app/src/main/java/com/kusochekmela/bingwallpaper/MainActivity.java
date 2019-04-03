package com.kusochekmela.bingwallpaper;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private WallPaperReceiver alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarm = new WallPaperReceiver();
    }

    public void startService(View view) {
        // set boot reciever
        Context context = this.getApplicationContext();
        ComponentName receiver = new ComponentName(context, WallPaperBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // set alarm
        if (alarm != null) {
            alarm.setAlarm(context);
        } else {
            Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopService(View view) {
        Context context = this.getApplicationContext();

        //stop boot reciever
        ComponentName receiver = new ComponentName(context, WallPaperBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // stop alarm
        if (alarm != null) {
            alarm.cancelAlarm();
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewLog(View view)
    {
        Context context = this.getApplicationContext();
        TextView textbox = (TextView)findViewById(R.id.logEditText);

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(context.getString(R.string.log_file));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                textbox.append(stringBuilder.toString());
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
}
