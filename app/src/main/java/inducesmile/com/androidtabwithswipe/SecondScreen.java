package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Cube on 10/25/2016.
 */

public class SecondScreen extends Activity {

    private static Context mContext;
    TimerSingleton timer = TimerSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);

        mContext = getApplicationContext();

        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        Log.d("Screen width", String.valueOf(width));
        Log.d("Screen height", String.valueOf(height));
        TextView ping = (TextView)findViewById(R.id.pingStatus);

        Log.e("ReadFormPref", "IP = " + readFromPref("PcIP"));

        if (readFromPref("PcIP").contains(":")) {

            if (isPingable(readFromPref("PcIP").split(":")[0], readFromPref("PcIP").split(":")[1])) {

                ping.setText("Succesful");
                ping.setTextColor(Color.GREEN);
            } else {
                ping.setText("Unuccesful");
                ping.setTextColor(Color.RED);
            }
        }

        else
        {
            if (isPingable(readFromPref("PcIP"))) {

                ping.setText("Succesful");
                ping.setTextColor(Color.GREEN);
            } else {
                ping.setText("Unuccesful");
                ping.setTextColor(Color.RED);
            }
        }

        ((TextView)findViewById(R.id.testbox1)).setText(readFromPref("ServerIP"));
        ((TextView)findViewById(R.id.testbox2)).setText(readFromPref("PcIP"));


        /*if (!timer.isRunning() && readFromPref() != "")
        {
            timer.start();
            ((TextView)findViewById(R.id.someTextView1)).setText(String.valueOf(timer.isRunning()));
        }
        else if (readFromPref() == "")
        {
            Log.e("Timer Singleton", "DeviceName is empty, timer.start is canceled");
        }
        else {
            Log.e("TIMER SINGLETON", "TIMER IS ALREADY RUNNING!!!");
            ((TextView)findViewById(R.id.someTextView1)).setText(String.valueOf(timer.isRunning()));
        }*/
        Log.e("Timer", "onCreate");
        ((TextView) findViewById(R.id.testTextView)).setText(readFromPref());

        ((Button) findViewById(R.id.goFurther)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.goToTest)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DependecyChart.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChartReduced.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReducedDependencyChart.class);
                startActivity(intent);
            }
        });
        Switch switch1 = (Switch)findViewById(R.id.switch1);
        /*if (timer.isRunning())
            switch1.setChecked(true);
        else
        {
            switch1.setChecked(false);
        }*/
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    timer.setIsEnabled(true);
                }else{
                    timer.setIsEnabled(false);
                    getNotification2();
                }

            }
        });

    }

    private boolean isPingable(String IP, String port){
        Log.e("executeCommand", "running");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 -p " + port + " " + IP);
            int mExitValue = mIpAddrProcess.waitFor();
            Log.e("executeCommand", String.valueOf(mExitValue));
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            Log.e("executeCommand", ignore.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("executeCommand", e.toString());
        }
        return false;
    }

    private boolean isPingable(String IP){
        Log.e("executeCommand", "running");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + IP);
            int mExitValue = mIpAddrProcess.waitFor();
            Log.e("mExitvalue", String.valueOf(mExitValue));
            if(mExitValue == 0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.stop();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (!timer.isRunning())
        {
            timer.start();
        }
    }

    public void getNotification2() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(Color.RED)
                .setWhen(System.currentTimeMillis())
                //.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(Color.MAGENTA, 100, 100)
                .setContentTitle("Be advised")
                .setContentText("Notifications are turned off")
                .setSmallIcon(R.drawable.temperature)
        //.setStyle(new NotificationCompat.BigTextStyle())
        ;
        // Setting notification style
        Intent intent = new Intent();


        PendingIntent contentIntent = PendingIntent.getActivity(SecondScreen.this, 0, intent, 0);
        builder.setContentIntent(contentIntent);

        Intent realtimeIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent realtime = PendingIntent.getActivity(this, 0, realtimeIntent, 0);
        builder.addAction(R.mipmap.ic_flash, "REALTIME", realtime);

        Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
        PendingIntent chartPendlingIntent = PendingIntent.getActivity(this, 0, chartIntent, 0);
        builder.addAction(R.mipmap.ic_chart, "LAST 24H", chartPendlingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }



    String readFromPref() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString("name", "");
        return name;
    }
    String readFromPref(String key)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString(key, "");
        return name;
    }
}
