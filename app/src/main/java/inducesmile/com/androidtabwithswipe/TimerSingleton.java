package inducesmile.com.androidtabwithswipe;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;

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

    static CountDownTimer timer3 = new CountDownTimer(1000, 1000) { // adjust the milli seconds here         this is working;

        public void onTick(long millisUntilFinished) {
            Log.e("TIMER", "IS WORKING");
            ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                    .usePlaintext(true)
                    .build();
            //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
            computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
            ComputerOuterClass.Computer response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName(readFromPref(TestActivity2.getContext())).build());
            try
            {
                if (Integer.valueOf(response.getCpuPackageLoad()) >= 50)
                {
                    Log.e("Timer", "CPU is heavily loaded");
                    getNotification2(TestActivity2.getContext(), "Heavy load for CPU has been detected");
                }
                else
                {
                    Log.d("Timer gRPC", response.getCpuPackageLoad());
                }
            }
            catch (Exception e)
            {
                timer3.cancel();
                Log.e("", e.toString());
            }

        }

        public void onFinish() {
            timer3.start();
        }
    };

    private static String readFromPref(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String name = settings.getString("name", "");
        return name;
    }

    public static void getNotification2(Context context, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setContentTitle("Content Title")
                .setContentText(content)
                .setSmallIcon(R.drawable.temperature)
        //.setStyle(new NotificationCompat.BigTextStyle())
        ;
        // Setting notification style
        Intent intent = new Intent();


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(contentIntent);

        Intent realtimeIntent = new Intent(context, MainActivity.class);
        PendingIntent realtime = PendingIntent.getActivity(context, 0, realtimeIntent, 0);
        builder.addAction(R.mipmap.ic_flash, "SEE REALTIME", realtime);

        Intent chartIntent = new Intent(context, ChartTest.class);
        PendingIntent chartPendlingIntent = PendingIntent.getActivity(context, 0, chartIntent, 0);
        builder.addAction(R.mipmap.ic_chart, "SEE CHART", chartPendlingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(0, notification);
    }

    static boolean isRunning = false;

    protected void start()
    {
        timer3.start();
        isRunning = true;
        Log.d("Timer Singleton", "Timer has started");

    }
    protected void stop()
    {
        timer3.cancel();
        isRunning = false;
        Log.d("Timer Singleton", "Timer has stopped");
    }
    protected boolean isRunning()
    {
        return isRunning;
    }
}
