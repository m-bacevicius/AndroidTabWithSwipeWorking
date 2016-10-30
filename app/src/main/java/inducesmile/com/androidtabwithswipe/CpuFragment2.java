package inducesmile.com.androidtabwithswipe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CpuFragment2 extends Fragment {

    public CpuFragment2() {
    }

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private List<LineGraphSeries<DataPoint>> mSeriesCpuCoreLoad;
    private List<LineGraphSeries<DataPoint>> mSeriesCpuCoreTemp;
    private List<LineGraphSeries<DataPoint>> mSeriesCpuCoreClock;


    private double graphLastXValue = 5d;
    private static String[] CoreTemp;
    private static String[] CoreClock;
    private static String[] CoreLoad;
    private int NumberOfCores;
    private static String DeviceName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cpu_fragment2, container, false);
        mSeriesCpuCoreTemp = new ArrayList<LineGraphSeries<DataPoint>>();
        mSeriesCpuCoreLoad = new ArrayList<LineGraphSeries<DataPoint>>();
        mSeriesCpuCoreClock = new ArrayList<LineGraphSeries<DataPoint>>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DeviceName = readFromPref();
        Toast toast = Toast.makeText(getContext(), DeviceName, Toast.LENGTH_SHORT);
        toast.show();

        ((Button) view.findViewById(R.id.testButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TestActivity.class);
                onPause();
                startActivity(intent);
            }
        });

        //getSqlData();
        try {
            getGrpcData(DeviceName);
        } catch (Exception e) {
            Log.e("found the exception", e.toString());
        }

        for (int x = 0; x < NumberOfCores; x++) {
            mSeriesCpuCoreClock.add(new LineGraphSeries<DataPoint>());
            mSeriesCpuCoreLoad.add(new LineGraphSeries<DataPoint>());
            mSeriesCpuCoreTemp.add(new LineGraphSeries<DataPoint>());
        }

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(40);
        for (int x = 0; x < NumberOfCores; x++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mSeriesCpuCoreTemp.get(x).setColor(color);
            mSeriesCpuCoreTemp.get(x).setTitle("Core" + x + " Temperature");
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph.addSeries(mSeriesCpuCoreTemp.get(x));
        }

        GraphView graph2 = (GraphView) view.findViewById(R.id.graph2);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMaxX(40);
        for (int x = 0; x < NumberOfCores; x++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mSeriesCpuCoreLoad.get(x).setColor(color);
            mSeriesCpuCoreLoad.get(x).setTitle("Core" + x + " Load");

            graph2.getLegendRenderer().setVisible(true);
            graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph2.addSeries(mSeriesCpuCoreLoad.get(x));
        }

        GraphView graph3 = (GraphView) view.findViewById(R.id.graph3);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMaxX(40);
        for (int x = 0; x < NumberOfCores; x++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mSeriesCpuCoreClock.get(x).setColor(color);
            mSeriesCpuCoreClock.get(x).setTitle("Core" + x + " Clock");

            graph3.getLegendRenderer().setVisible(true);
            graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph3.addSeries(mSeriesCpuCoreClock.get(x));
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                try {
                    getGrpcData(DeviceName);
                    graphLastXValue += 1d;
                    for (int x = 0; x < CoreTemp.length; x++) {
                        mSeriesCpuCoreTemp.get(x).appendData(new DataPoint(graphLastXValue, Double.valueOf(CoreTemp[x])), true, 40);
                    }
                } catch (Exception e) {
                    Log.e("found the exception", e.toString());
                }
                //mSeries1.appendData(new DataPoint(graphLastXValue, Double.valueOf(GpuClock)), true, 40);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int x = 0; x < CoreLoad.length; x++) {
                        mSeriesCpuCoreLoad.get(x).appendData(new DataPoint(graphLastXValue, Double.valueOf(CoreLoad[x])), true, 40);
                    }
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    Log.e("Timer2", e.toString());
                }
            }
        };
        mHandler.postDelayed(mTimer2, 1000);

        mTimer3 = new Runnable() {
            @Override
            public void run() {
                try {

                    for (int x = 0; x < CoreClock.length; x++) {
                        mSeriesCpuCoreClock.get(x).appendData(new DataPoint(graphLastXValue, Double.valueOf(CoreClock[x])), true, 40);
                    }
                } catch (Exception e) {
                    Log.e("Timer3", e.toString());
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer3, 1000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        mHandler.removeCallbacks(mTimer2);
        mHandler.removeCallbacks(mTimer3);
        super.onPause();
    }

    public void getGrpcData() throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                .usePlaintext(true)
                .build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        //ComputerOuterClass.ComputerListResponse response = stub.getComputerList(ComputerOuterClass.Empty.newBuilder().build());
        ComputerOuterClass.Computer response = stub.getRealtimeComputer(ComputerOuterClass.Empty.newBuilder().build());
        CoreClock = response.getCpuCoreClock().split(" ");
        CoreLoad = response.getCpuCoreLoad().split(" ");
        CoreTemp = response.getCpuCoreTemp().split(" ");
        NumberOfCores = CoreClock.length;
    }

    public void getGrpcData(String name) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                .usePlaintext(true)
                .build();
        //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.Computer response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName(name).build());
        CoreClock = response.getCpuCoreClock().split(" ");
        CoreLoad = response.getCpuCoreLoad().split(" ");
        CoreTemp = response.getCpuCoreTemp().split(" ");
        NumberOfCores = CoreClock.length;

        /*ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                .usePlaintext(true)
                .build();
        //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.Computer response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName("DESKTOP-0RPJR9K").build());*/
    }

    String readFromPref() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String name = settings.getString("name", "");
        return name;
    }
}
