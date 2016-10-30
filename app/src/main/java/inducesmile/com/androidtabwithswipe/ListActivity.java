package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Cube on 10/27/2016.
 */

public class ListActivity extends Activity {

    public static Map<Integer, ComputerOuterClass.Computer> map;
    public static BarGraphSeries<DataPoint> mSeries1;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_test);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        map = getMap();
        //fillUpTheDataSeries(map, mSeries1);
        initGraph(graph);
    }

    public void initGraph (GraphView graph)
    {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(fillUpTheDataSeries(map));

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(80);
        graph.computeScroll();

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        //graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext(), new SimpleDateFormat("HH:mm:ss")));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(80);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.addSeries(series);
    }
    @Override
    public void onResume() {
        super.onResume();
            new Thread(new Runnable() {
                public void run() {
                    //map = getMap();
                    android.os.SystemClock.sleep(5000);
                }
            }).start();


        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), String.valueOf(map.containsKey(2)), Toast.LENGTH_SHORT).show();
                /*for ( Map.Entry<Integer, ComputerOuterClass.Computer> entry : map.entrySet()) {
                    Integer key = entry.getKey();
                    ComputerOuterClass.Computer tab = entry.getValue();
                    Log.e(String.valueOf(key), tab.getCpuCoreTemp());
                    // do something with key and/or tab
                }*/
                //fillUpTheDataSeries(map, mSeries1);
            }
        });
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

    private DataPoint[] fillUpTheDataSeries(Map<Integer, ComputerOuterClass.Computer> stuff)
    {
        DataPoint[] points = new DataPoint[stuff.entrySet().size()];
        int counter = 0;
        for ( Map.Entry<Integer, ComputerOuterClass.Computer> entry : stuff.entrySet())
        {
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
            if (d1 == null)
            {
                Log.e("","d1 is null");
            }
            if (Double.isNaN(temp))
            {
                Log.e("", "temp is null");
            }
            Log.e("DATE", date.toString());
            Log.d("TEMP", String.valueOf(temp));
            //mSeries1.appendData(new DataPoint(date, temp), true, 40);
            points[counter++] = new DataPoint(date, temp);
        }
        return points;
        //mSeries1.appendData(new DataPoint(graphLastXValue, Double.valueOf(GpuClock)), true, 40);
    }
}