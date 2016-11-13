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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

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

    LineChart chart;
    LineChart chart2;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_test);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        dialog = ProgressDialog.show(ChartTest.this, "",
                "Loading your chart. Please wait...", true);

        chart = (LineChart) findViewById(R.id.chart);
        chart2 = (LineChart) findViewById(R.id.chart2);

        try {
            GetChart1 asyncRate = new GetChart1();
            asyncRate.execute();
        }
        catch (Exception e)
        {}
    }

    private class GetChart1 extends AsyncTask<Void, Integer, Map<Integer, ComputerOuterClass.Computer>> {

        @Override
        protected Map<Integer, ComputerOuterClass.Computer> doInBackground(Void... params) {
            Map<Integer, ComputerOuterClass.Computer> map = getMap();
            Log.d("doInBackground", "Getting data");
            return map;
        }

        @Override
        protected void onPostExecute(Map<Integer, ComputerOuterClass.Computer> result) {
            // Do whatever you need with the string, you can update your UI from here
            LineData lineData = new LineData();

            LineDataSet tempDataSet = new LineDataSet(fillUpTheChart(result, "Load").get(0), "CPU Load");
            tempDataSet.setColor(Color.YELLOW);
            tempDataSet.setDrawCircles(false);
            lineData.addDataSet(tempDataSet);

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Load").get(1), "GPU Load");
            tempDataSet.setColor(Color.GREEN);
            tempDataSet.setDrawCircles(false);
            lineData.addDataSet(tempDataSet);

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Load").get(2), "RAM Load");
            tempDataSet.setColor(Color.RED);
            tempDataSet.setDrawCircles(false);
            lineData.addDataSet(tempDataSet);

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Load").get(3), "HDD Load");
            tempDataSet.setColor(Color.BLUE);
            tempDataSet.setDrawCircles(false);
            lineData.addDataSet(tempDataSet);

            //chart.animateY(3000);
            //chart.animateX(3000, Easing.EasingOption.EaseInOutSine);

            //chart2 data filling

            LineData lineData2 = new LineData();

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Temperature").get(0), "CPU Temperature");
            tempDataSet.setColor(Color.YELLOW);
            tempDataSet.setDrawCircles(false);
            lineData2.addDataSet(tempDataSet);

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Temperature").get(1), "GPU Temperature");
            tempDataSet.setColor(Color.GREEN);
            tempDataSet.setDrawCircles(false);
            lineData2.addDataSet(tempDataSet);

            tempDataSet = new LineDataSet(fillUpTheChart(result, "Temperature").get(3), "HDD Temperature");
            tempDataSet.setColor(Color.BLUE);
            tempDataSet.setDrawCircles(false);
            lineData2.addDataSet(tempDataSet);

            chart.setData(lineData);
            chart.invalidate(); // refresh

            chart2.setData(lineData2);
            chart2.invalidate();

            dialog.hide();
        }
    }

    private Map<Integer, ComputerOuterClass.Computer> getMap() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43432)
                .usePlaintext(true)
                .build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.ComputerListResponse response = stub.getComputerListWithName(ComputerOuterClass.ComputerName.newBuilder().setName(readFromPref()).build());
       // System.out.print(response + "The count " + response.getComputerListCount() + '\n');
        Map<Integer, ComputerOuterClass.Computer> map = response.getComputerListMap();
        return map;
    }

    private List<List<Entry>> fillUpTheChart(Map<Integer, ComputerOuterClass.Computer> stuff, String sortOfData) {
        List<Entry> Cpu = new ArrayList<Entry>();
        List<Entry> Gpu = new ArrayList<Entry>();
        List<Entry> Ram = new ArrayList<Entry>();
        List<Entry> Hdd = new ArrayList<Entry>();
        double cpu = 0;
        double gpu = 0;
        double ram = 0;
        double hdd = 0;


        int counter = 0;
        for (Map.Entry<Integer, ComputerOuterClass.Computer> entry : stuff.entrySet()) {
            Integer key = entry.getKey();
            ComputerOuterClass.Computer computer = entry.getValue();
            if (sortOfData == "Temperature") {
                cpu = Double.valueOf(computer.getCpuPackageTemp());
                gpu = Double.valueOf(computer.getGpuTemp());
                ram = 0f;
                hdd = Double.valueOf(computer.getHddTemp());
            }
            else if (sortOfData == "Load")
            {
                cpu = Double.valueOf(computer.getCpuPackageLoad());
                gpu = Double.valueOf(computer.getGpuLoad());
                ram = Double.valueOf(computer.getLoadRam());
                hdd = Double.valueOf(computer.getHddLoad());
            }
            if (Double.isNaN(cpu) || Double.isNaN(gpu) || Double.isNaN(ram) || Double.isNaN(hdd)) {
                Log.e("ChartTest", "tempData is null");
            }
            else {
                counter++;
                //entries.add(new Entry(counter, (int) tempData));
                Cpu.add(new Entry(counter, (int) cpu));
                Gpu.add(new Entry(counter, (int) gpu));
                Ram.add(new Entry(counter, (int) ram));
                Hdd.add(new Entry(counter, (int) hdd));
            }
        }
        //List<List<Entry>> list = new ArrayList<>();
        List<List<Entry>> list = new ArrayList<List<Entry>>(4);
        list.add(Cpu);
        list.add(Gpu);
        list.add(Ram);
        list.add(Hdd);
        return list;

    }
    private String readFromPref() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString("name", "");
        return name;
    }
}
