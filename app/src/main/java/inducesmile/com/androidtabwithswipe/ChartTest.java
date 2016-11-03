package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

    List<Entry> entries = new ArrayList<Entry>();
    LineChart chart;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_test);

        dialog = ProgressDialog.show(ChartTest.this, "",
                "Loading your chart. Please wait...", true);

        chart = (LineChart) findViewById(R.id.chart);

        try {
            //entries = fillUpTheDataSeries(getMap());
            //startTestThread();
            GetRate asyncRate = new GetRate();
            asyncRate.execute();
        }
        catch (Exception e)
        {}
        /*LineDataSet dataSet = new LineDataSet(entries, "Cpu package temp"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setDrawCircles(false);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        //chart.animateY(3000);
        chart.animateX(3000, Easing.EasingOption.EaseInOutSine);
        chart.setData(lineData);
        chart.invalidate(); // refresh*/
    }

    private class GetRate extends AsyncTask<Void, Integer, Map<Integer, ComputerOuterClass.Computer>> {

        @Override
        protected Map<Integer, ComputerOuterClass.Computer> doInBackground(Void... params) {
            Map<Integer, ComputerOuterClass.Computer> map = getMap();
            Log.d("doInBackground", "Getting data");
            return map;
        }

        @Override
        protected void onPostExecute(Map<Integer, ComputerOuterClass.Computer> result) {
            // Do whatever you need with the string, you can update your UI from here
            entries = fillUpTheDataSeries(result);
            Log.d("onPostExecute", "Filling up data");
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
            dialog.hide();
        }
    }

    /*protected void startTestThread() {
        Thread t = new Thread() {
            public void run() {

                entries = fillUpTheDataSeries(getMap());
            }
        };
        t.start();
    }*/

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
       // System.out.print(response + "The count " + response.getComputerListCount() + '\n');
        Map<Integer, ComputerOuterClass.Computer> map = response.getComputerListMap();
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
            double temp = Double.valueOf(tab.getCpuPackageLoad());
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
            //mSeries1.appendData(new DataPoint(date, temp), true, 40);
            counter++;
            entries.add(new Entry(counter, (int) temp));
        }
        return entries;
    }
}
