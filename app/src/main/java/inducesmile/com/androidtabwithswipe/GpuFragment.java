package inducesmile.com.androidtabwithswipe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GpuFragment extends Fragment {

    public GpuFragment() {
    }

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private double graphLastXValue = 5d;
    private double graph2LastXValue = 5d;
    private double graph3LastXValue = 5d;
    static final String JDBCPrefix = "jdbc:mysql://";
    public static String IP = "193.219.36.33";
    public static String Port = "43431";
    public static String USER = "root";
    public static String PASS = "sdsd";
    public static String GpuTemp = "34";
    public static String GpuLoad = "34";
    public static String GpuClock = "34";
    //private String GpuCoreTemp = "43";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gpu_fragment, container, false);
        //GridView gridview = (GridView)view.findViewById(R.id.gridview);
        //TextView textView = new TextView(getActivity());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        GraphView graph = (GraphView)view.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>();
        mSeries1.setTitle("GPU Clock");
        //mSeries1.setSpacing(50);
        mSeries1.setAnimated(true);
        //mSeries1.setSpacing(25);
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        //graph.getViewport().setMaxX(4.5);
        //graph.getViewport().setMinX(0.5);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);

        GraphView graph2 = (GraphView)view.findViewById(R.id.graph2);
        mSeries2 = new LineGraphSeries<DataPoint>();
        mSeries2.setTitle("GPU Temp");
        graph2.addSeries(mSeries2);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setScalable(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);
        graph2.getViewport().setMinY(45);
        graph2.getViewport().setMaxY(100);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);

        GraphView graph3 = (GraphView)view.findViewById(R.id.graph3);
        mSeries3 = new LineGraphSeries<>();
        mSeries3.setTitle("GPU Load");
        mSeries3.setAnimated(true);
        graph3.addSeries(mSeries1);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMaxX(120);
        graph3.getViewport().setScalable(true);
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                //getSqlData();
                //mSeries1.resetData(generateData());
                //mSeries1.resetData(Double.valueOf(data));
                graphLastXValue += 1d;
                mSeries1.appendData(new DataPoint(graphLastXValue, Double.valueOf(GpuClock)), true, 40);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                //getSqlData();
                graph2LastXValue += 1d;
                //mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                mSeries2.appendData(new DataPoint(graph2LastXValue, Double.valueOf(GpuTemp)), true, 40);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer2, 1000);

        mTimer3 = new Runnable() {
            @Override
            public void run() {
                //getSqlData();
                graph3LastXValue += 1d;
                //mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                mSeries3.appendData(new DataPoint(graph3LastXValue, Double.valueOf(GpuLoad)), true, 40);
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

    public void getSqlData() {
        // JDBC driver name and database URL
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        //final String DB_URL = "jdbc:mysql://193.219.36.33:43431/javabase";
        final String DB_URL = JDBCPrefix + IP + ':' + Port + '/' + "javabase";

        //  Database credentials

        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM Pcs ORDER BY id DESC LIMIT 1;";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                //String last = rs.getString("last");
                /*CpuCoreTemps = rs.getString("CpuCoresTemp");
                String content = rs.getString("CpuPackageTemp");*/
                GpuTemp  = rs.getString("GpuTemp");
                GpuClock = rs.getString("GpuClock");
                GpuLoad = rs.getString("GpuLoad");

                //data = content;
                //Log.e("GOT DATA: ", CpuCoreTemps);

            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }

}
