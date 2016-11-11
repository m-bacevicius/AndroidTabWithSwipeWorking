package inducesmile.com.androidtabwithswipe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;

import java.util.Timer;
import java.util.TimerTask;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Mantas Bacevičius on 2016-11-02.
 */
public class TimerSingleton {
    private static TimerSingleton ourInstance = new TimerSingleton();

    public static TimerSingleton getInstance() {
        return ourInstance;
    }

    private TimerSingleton() {
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Log.e("TIMER", "IS WORKING");
            ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                    .usePlaintext(true)
                    .build();
            //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
            computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
            ComputerOuterClass.Computer response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName(readFromPref(TestActivity2.getContext())).build());
            try
            {
                if (Integer.valueOf(response.getCpuPackageLoad()) >= 50 && counter >= 600)
                {
                    Log.e("Timer", "CPU is heavily loaded");
                    getNotification2(TestActivity2.getContext(), "Heavy load for CPU has been detected: " + response.getCpuPackageLoad());
                    counter = 0;
                }
                else
                {
                    Log.d("Timer gRPC", response.getCpuPackageLoad());
                    Log.e("Timer counter", String.valueOf(counter));
                    counter++;
                }
            }
            catch (Exception e)
            {
                stopTimer();
                //timer3.cancel();
                Log.e("", e.toString());
            }
            handler.postDelayed(this, 10000);
        }
    };

    private void startTimer()
    {
        runnable.run();
    }
    private void stopTimer()
    {
        handler.removeCallbacks(runnable);
    }

    private static String readFromPref(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String name = settings.getString("name", "");
        return name;
    }

    public static void getNotification2(Context context, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                //.setPriority(Notification.PRIORITY_MAX)
                .setColor(Color.RED)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)

                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                //.setDefaults(Notification.FLAG_SHOW_LIGHTS)
                .setLights(Color.MAGENTA, 100, 100)
                .setContentTitle("Be advised")
                .setContentText(content)
                //.setOngoing(false)
                .setSmallIcon(R.drawable.temperature)

        //.setStyle(new NotificationCompat.BigTextStyle())
        ;
        // Setting notification style
        Intent intent = new Intent(context, TestActivity2.class);       //TODO was () instead of context, TestActivity2.class
                                                                        //TODO look for a better result

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(contentIntent);

        Intent realtimeIntent = new Intent(context, MainActivity.class);
        PendingIntent realtime = PendingIntent.getActivity(context, 0, realtimeIntent, 0);
        builder.addAction(R.mipmap.ic_flash, "REALTIME", realtime);

        Intent chartIntent = new Intent(context, ChartTest.class);
        PendingIntent chartPendlingIntent = PendingIntent.getActivity(context, 0, chartIntent, 0);
        builder.addAction(R.mipmap.ic_chart, "LAST 24H", chartPendlingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(0, notification);
    }

    static boolean isRunning = false;
    static long counter = 601;

    protected void start()
    {
        startTimer();
        //timer3.start();
        isRunning = true;
        Log.d("Timer Singleton", "Timer has started");

    }
    protected void stop()
    {
        stopTimer();
        //timer3.cancel();
        isRunning = false;
        Log.d("Timer Singleton", "Timer has stopped");
    }
    protected boolean isRunning()
    {
        return isRunning;
    }

    private void doGrpc()
    {
        Log.e("TIMER", "IS WORKING");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                .usePlaintext(true)
                .build();
        //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.Computer response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName(readFromPref(TestActivity2.getContext())).build());
        try
        {
            if (Integer.valueOf(response.getCpuPackageLoad()) >= 50 && counter >= 600)
            {
                Log.e("Timer", "CPU is heavily loaded");
                getNotification2(TestActivity2.getContext(), "Heavy load for CPU has been detected: " + response.getCpuPackageLoad());
                counter = 0;
            }
            else
            {
                Log.d("Timer gRPC", response.getCpuPackageLoad());
                Log.e("Timer counter", String.valueOf(counter));
                counter++;
            }
        }
        catch (Exception e)
        {
            stopTimer();
            //timer3.cancel();
            Log.e("", e.toString());
        }
    }
}
