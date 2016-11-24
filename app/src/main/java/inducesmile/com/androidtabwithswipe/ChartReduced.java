package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Cube on 11/21/2016.
 */

public class ChartReduced extends Activity implements AdapterView.OnItemSelectedListener {

    LineChart chart;
    LineChart chart2;
    ProgressDialog dialog;
    private GetChart1 asyncRate = new GetChart1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_reduced);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        dialog = ProgressDialog.show(ChartReduced.this, "",
                "Loading your chart. Please wait...", true);

        chart = (LineChart) findViewById(R.id.chart);
        chart2 = (LineChart) findViewById(R.id.chart2);

        try {
           // asyncRate.execute(String.valueOf(((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()),readFromPref(),"Avg");
        } catch (Exception e) {
        }

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Average");
        categories.add("Max");
        categories.add("Min");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
    //Spinner onItemSelected method
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

        asyncRate = new GetChart1();
        dialog.show();
        //If "Average" is selected, we change argument to "Avg", else we leave it in
        if (item.compareTo("Average") == 0)
        {
            asyncRate.execute(String.valueOf(((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()),"Avg");
        }
        else if (item.compareTo("Max") == 0)  {
            asyncRate.execute(String.valueOf(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()), "Max");
        }
        else if (item.compareTo("Min") == 0)  {
            asyncRate.execute(String.valueOf(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()), "Min");
        }

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private class GetChart1 extends AsyncTask<String, Integer, Map<Integer, ComputerOuterClass.Computer>> {

        @Override
        protected Map<Integer, ComputerOuterClass.Computer> doInBackground(String... params) {
            Map<Integer, ComputerOuterClass.Computer> map = getMap(Integer.valueOf(params[0]), params[1]);
            Log.d("doInBackground", "Getting data");
            return map;
        }

        @Override
        protected void onPostExecute(Map<Integer, ComputerOuterClass.Computer> result) {
            // Do whatever you need with the string, you can update your UI from here
            LineData lineData = new LineData();

            LineDataSet tempDataSet = new LineDataSet(fillUpTheChart(result, "Load").get(0), "CPU Load");
            tempDataSet.setColor(Color.MAGENTA);
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
            tempDataSet.setColor(Color.MAGENTA);
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

    private Map<Integer, ComputerOuterClass.Computer> getMap(int screenSize, String method) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(getIP(), 43433)
                .usePlaintext(true)
                .build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
        ComputerOuterClass.ComputerListResponse response = stub.getComputerListWithName(ComputerOuterClass.ComputerName.newBuilder().setName(screenSize + ";" + readFromPref() + ";" + method).build());
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
        for (int x = 1; x < stuff.size(); x++) {
            ComputerOuterClass.Computer computer = stuff.get(x);
            Log.d("counter", String.valueOf(x));
            if (sortOfData == "Temperature") {
                cpu = Double.valueOf(computer.getCpuPackageTemp());
                gpu = Double.valueOf(computer.getGpuTemp());
                ram = 0f;
                hdd = Double.valueOf(computer.getHddTemp());
            } else if (sortOfData == "Load") {
                cpu = Double.valueOf(computer.getCpuPackageLoad());
                gpu = Double.valueOf(computer.getGpuLoad());
                ram = Double.valueOf(computer.getLoadRam());
                hdd = Double.valueOf(computer.getHddLoad());
            }
            if (Double.isNaN(cpu) || Double.isNaN(gpu) || Double.isNaN(ram) || Double.isNaN(hdd)) {
                Log.e("ChartTest", "tempData is null");
            } else {
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
    private String getIP()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String Ip = settings.getString("Ip", "");
        return Ip;
    }
}
