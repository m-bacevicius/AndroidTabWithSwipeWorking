package inducesmile.com.androidtabwithswipe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Cube on 11/11/2016.
 */

public class RamFragment extends Fragment {

    public RamFragment() {
    }

    TimerSingleton singletonTimer = TimerSingleton.getInstance();

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private double graphLastXValue = 1d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ram_fragment, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>();
        mSeries1.setTitle("RAM Load");
        mSeries1.setAnimated(true);
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);

        GraphView graph2 = (GraphView) view.findViewById(R.id.graph2);
        mSeries2 = new LineGraphSeries<>();
        mSeries2.setTitle("Used RAM");
        graph2.addSeries(mSeries2);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);

        GraphView graph3 = (GraphView) view.findViewById(R.id.graph3);
        mSeries3 = new LineGraphSeries<>();
        mSeries3.setTitle("Free RAM");
        //mSeries1.setSpacing(25);
        graph3.addSeries(mSeries3);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMinX(0);
        graph3.getViewport().setMaxX(40);
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);


        timer.start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        timer.start();

        Log.e("RamFragment", "ONRESUME");
        singletonTimer.stop();
    }
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
    protected CountDownTimer timer = new CountDownTimer(1000, 1000) {

        ComputerOuterClass.Computer response;

        public void onTick(long millisUntilFinished) {
            try {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43431)
                        .usePlaintext(true)
                        .build();
                //ComputerOuterClass.ComputerName request = ComputerOuterClass.ComputerName.newBuilder().setName(name).build();
                computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
                response = stub.getRealtimeComputerWithName(ComputerOuterClass.ComputerName.newBuilder().setName(readFromPref()).build());
            } catch (Exception e) {
                Log.e("RamFragment", "gRPC error");
            }
            try {
                graphLastXValue += 1d;
                mSeries1.appendData(new DataPoint(graphLastXValue, Double.valueOf(response.getLoadRam())), true, 40);
            } catch (Exception e) {
                Log.e("RamFragmen 1", e.toString());
            }
            try {
                mSeries2.appendData(new DataPoint(graphLastXValue, Double.valueOf(response.getUsedRam())), true, 40);
            } catch (Exception e) {
                Log.e("RamFragmen 2", e.toString());
            }
            try {
                mSeries3.appendData(new DataPoint(graphLastXValue, Double.valueOf(response.getFreeRam())), true, 40);
            } catch (Exception e) {
                Log.e("RamFragmen 3", e.toString());
            }
        }

        public void onFinish() {
            start();
        }
    };

    String readFromPref() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String name = settings.getString("name", "");
        return name;
    }
}
