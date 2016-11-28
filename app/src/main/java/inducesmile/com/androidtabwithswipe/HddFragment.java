package inducesmile.com.androidtabwithswipe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Mantas Bacevičius on 2016-11-11.
 */

public class HddFragment extends Fragment {

    TimerSingleton singletonTimer = TimerSingleton.getInstance();

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graphLastXValue = 1d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hdd_fragment, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>();
        mSeries1.setTitle("HDD Load");
        mSeries1.setAnimated(true);
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setVerticalAxisTitle(" ");

        GraphView graph2 = (GraphView) view.findViewById(R.id.graph2);
        mSeries2 = new LineGraphSeries<>();
        mSeries2.setTitle("HDD temperature");
        graph2.addSeries(mSeries2);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.getGridLabelRenderer().setVerticalAxisTitle(" ");

        timer.start();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        singletonTimer.stop();
        timer.start();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        singletonTimer.start();
        timer.cancel();
        if(!singletonTimer.isRunning())
        {
            singletonTimer.start();
        }
    }

    protected CountDownTimer timer = new CountDownTimer(1000, 1000) {

        ComputerOuterClass.Computer response;

        public void onTick(long millisUntilFinished) {
            try {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(getIP(), 43434)
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
                mSeries1.appendData(new DataPoint(graphLastXValue, Double.valueOf(response.getHddLoad())), true, 40);
                //Log.i("Runner1", response.getHddLoad());
            } catch (Exception e) {
                Log.e("RamFragmen 1", e.toString());
            }
            try {
                mSeries2.appendData(new DataPoint(graphLastXValue, Double.valueOf(response.getHddTemp())), true, 40);
                //Log.i("Runner2", response.getHddTemp());
            } catch (Exception e) {
                Log.e("RamFragmen 2", e.toString());
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
    private String getIP()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String Ip = settings.getString("Ip", "");
        return Ip;
    }
}
