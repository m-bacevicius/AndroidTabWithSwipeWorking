package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Cube on 10/30/2016.
 */

public class ChartTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_test);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getNotification(v);
                //setAlarm(v);
                getNotification2();

            }
        });
        try {
            entries = fillUpTheDataSeries(getMap());
        }
        catch (Exception e)
        {}
        LineDataSet dataSet = new LineDataSet(entries, "Cpu package temp"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setDrawCircles(false);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        //chart.animateY(3000);
        chart.animateX(3000, Easing.EasingOption.EaseInOutSine);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

    public void getNotification (View view)
    {
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(ChartTest.this, 0, intent, 0);
        Notification notification = new Notification.Builder(ChartTest.this)
                .setTicker("Ticker Title")
                .setContentTitle("Content Title")
                .setContentText("This is a content test")
                .setSmallIcon(R.drawable.temperature)
                .addAction(R.mipmap.ic_launcher, "Ignore", pIntent)
                //.addAction(R.drawable.glass, "Investigate", pIntent)
                .setContentIntent(pIntent).getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void getNotification2()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setContentTitle("Content Title")
                .setContentText("This is a content test")
                .setSmallIcon(R.drawable.temperature)
                .setStyle(new NotificationCompat.BigTextStyle());
        // Setting notification style
        Intent intent = new Intent();
        Intent realtimeIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(ChartTest.this, 0, intent, 0);
        builder.setContentIntent(contentIntent);

        PendingIntent realtime = PendingIntent.getActivity(this, 0, realtimeIntent, 0);
        builder.addAction(R.drawable.chart, "SEE CHART", realtime);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }

    public void setAlarm(View view)
    {
        long alertTime = System.nanoTime()+1*1000;

        Intent alertIntent = new Intent(this, AlertReceiver.class);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private Map<Integer, ComputerOuterClass.Computer> getMap() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43432)
                .usePlaintext(true)
                .build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.ComputerListResponse response = stub.getComputerList(ComputerOuterClass.Empty.newBuilder().build());
        //ComputerOuterClass.Computer response = stub.getRealtimeComputer(ComputerOuterClass.Empty.newBuilder().build());
        System.out.print(response + "The count " + response.getComputerListCount() + '\n');
        Map<Integer, ComputerOuterClass.Computer> map = response.getComputerListMap();
        /*for (int x = 0; x < map.size(); x++) {
            System.out.println(map.get(x).getCpuCoreTemp());
        }*/
        return map;
    }

    private List<Entry> fillUpTheDataSeries(Map<Integer, ComputerOuterClass.Computer> stuff) {
        //DataPoint[] points = new DataPoint[stuff.entrySet().size()];
        List<Entry> entries = new ArrayList<Entry>();
        int counter = 0;
        for (Map.Entry<Integer, ComputerOuterClass.Computer> entry : stuff.entrySet()) {
            Integer key = entry.getKey();
            ComputerOuterClass.Computer tab = entry.getValue();
            String data = tab.getDate();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            double temp = Double.valueOf(tab.getCpuPackageTemp());
            try {
                date = dateFormat.parse(data);// all done
            } catch (Exception e) {
                Log.e("Data conversion", e.toString());
            }
            Date d1 = Calendar.getInstance().getTime();
            if (d1 == null) {
                Log.e("", "d1 is null");
            }
            if (Double.isNaN(temp)) {
                Log.e("", "temp is null");
            }
            Log.e("DATE", date.toString());
            Log.d("TEMP", String.valueOf(temp));
            //mSeries1.appendData(new DataPoint(date, temp), true, 40);
            counter++;
            entries.add(new Entry(counter, (int) temp));
        }
        return entries;
    }
}
