package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Cube on 10/25/2016.
 */

public class TestActivity2 extends Activity {

    private static Context mContext;
    TimerSingleton timer = TimerSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity2);

        mContext = getApplicationContext();

        if (!timer.isRunning() && readFromPref() != "")
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
        }
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
                Intent intent = new Intent(getApplicationContext(), ChartTest.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getNotification2();
                //redFlashLight();          //TODO WORKS!!!
                timer.stop();
                ((TextView)findViewById(R.id.someTextView1)).setText(String.valueOf(timer.isRunning()));
            }
        });

    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Timer", "onResume");
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
                .setContentTitle("Content Title")
                .setContentText("This is a content test")
                .setSmallIcon(R.drawable.temperature)
        //.setStyle(new NotificationCompat.BigTextStyle())
        ;
        // Setting notification style
        Intent intent = new Intent();


        PendingIntent contentIntent = PendingIntent.getActivity(TestActivity2.this, 0, intent, 0);
        builder.setContentIntent(contentIntent);

        Intent realtimeIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent realtime = PendingIntent.getActivity(this, 0, realtimeIntent, 0);
        builder.addAction(R.mipmap.ic_flash, "REALTIME", realtime);

        Intent chartIntent = new Intent(getApplicationContext(), ChartTest.class);
        PendingIntent chartPendlingIntent = PendingIntent.getActivity(this, 0, chartIntent, 0);
        builder.addAction(R.mipmap.ic_chart, "LAST 24H", chartPendlingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }

    private void RedFlashLight()
    {
        NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
        Notification notif = new Notification();
        notif.ledARGB = 0xFFff0000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;
        nm.notify(1234, notif);
    }


    public void getNotification() {
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(TestActivity2.this, 0, intent, 0);
        Notification notification = new Notification.Builder(TestActivity2.this)
                .setTicker("Ticker Title")
                .setContentTitle("Content Title")
                .setContentText("This is a content test")
                .setSmallIcon(R.drawable.temperature)
                .addAction(R.mipmap.ic_chart, "Ignore", pIntent)
                //.addAction(R.drawable.glass, "Investigate", pIntent)
                .setContentIntent(pIntent).getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    void redFlashLight() {
        Notification.Builder builder = new Notification.Builder(getContext());
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(TestActivity2.this, 0, intent, 0);
        builder.setContentIntent(pIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("My Ticker")
                .setWhen(System.currentTimeMillis())
                //.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(Color.MAGENTA, 300, 100)
                .setContentTitle("My Title 1")
                .setContentText("My Text 1");
        Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);


    }

    String doRead() {
        String filename = "myfile";
        String string = "Hello world!";
        FileInputStream inputStream;

        /*try {
            inputStream = openFileInput(filename, Context.MODE_PRIVATE);
            inputStream.read(string.getBytes());
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    String readFromPref() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString("name", "");
        return name;
    }
}
