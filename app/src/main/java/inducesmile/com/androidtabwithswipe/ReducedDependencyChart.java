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
import android.widget.Toast;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Cube on 11/21/2016.
 */

public class ReducedDependencyChart extends Activity implements AdapterView.OnItemSelectedListener {

    private LineChartView chartTop;
    private ColumnChartView chartBottom;

    private LineChartData lineData;
    private ColumnChartData columnData;
    private List<List<ComputerOuterClass.Computer>> dataList;
    protected static ProgressDialog dialog;
    private GetChart1 asyncRate = new GetChart1();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reduced_dependency_chart);

        dialog = ProgressDialog.show(ReducedDependencyChart.this, "",
                "Loading your chart. Please wait...", true);

        try {
            asyncRate = new GetChart1();
            asyncRate.execute(String.valueOf(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()), "Avg");
        } catch (Exception e) {
        }

        // *** TOP LINE CHART ***
        chartTop = (LineChartView) findViewById(R.id.chart_top);

        // Generate and set data for line chart
        //generateInitialLineData();            //Transported to onPostExecute

        // *** BOTTOM COLUMN CHART ***

        chartBottom = (ColumnChartView) findViewById(R.id.chart_bottom);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        //asyncRate = new GetChart1();
        //asyncRate.execute(String.valueOf(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()), "Avg");
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }



    private void generateColumnData() {

        int numSubcolumns = 1;
        // int numColumns = months.length;
        int numColumns = dataList.size();

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                //values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
                values.add(new SubcolumnValue((float) dataList.get(i).size(), ChartUtils.pickColor()));
            }

            //axisValues.add(new AxisValue(i).setLabel(months[i]));

            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(4));

        chartBottom.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        chartBottom.setOnValueTouchListener(new ReducedDependencyChart.ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        chartBottom.setValueSelectionEnabled(true);

        chartBottom.setZoomType(ZoomType.HORIZONTAL);
    }



    private void generateInitialLineData() {
        int numValues = 3600;

        //List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        List<PointValue> values2 = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            values2.add(new PointValue(i, 0));
            //axisValues.add(new AxisValue(i).setLabel(days[i]));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);
        line.setHasPoints(false);

        Line line2 = new Line(values2);
        line.setColor(ChartUtils.COLOR_BLUE).setCubic(true);
        line.setHasPoints(false);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        lines.add(line2);
        lines.add(new Line(line));
        lines.add(new Line(line2));

        lineData = new LineChartData(lines);
        //lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 110, 3600, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);

        chartTop.setZoomType(ZoomType.HORIZONTAL);
    }

    private void generateLineData(int color, float range) {
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        //line.setColor(color);
        line.setStrokeWidth(1);
        //line.setHasPoints(false);

        // Modify data targets
        Line line2 = lineData.getLines().get(1);// For this example there is always only one line.
        //line2.setColor(color);
        line2.setStrokeWidth(1);
        //line.setHasPoints(false);

        Line line3 = lineData.getLines().get(2);// For this example there is always only one line.
        //line3.setColor(color);

        Line line4 = lineData.getLines().get(3);// For this example there is always only one line.
        //line4.setColor(color);

        for (int x = 0; x < line.getValues().size(); x++) {
            line.getValues().get(x).setTarget(line.getValues().get(x).getX(), (float) Math.random() * range);
            line2.getValues().get(x).setTarget(line.getValues().get(x).getX(), (float) Math.random() * range);
            line3.getValues().get(x).setTarget(line.getValues().get(x).getX(), (float) Math.random() * range);
            line4.getValues().get(x).setTarget(line.getValues().get(x).getX(), (float) Math.random() * range);
        }

        // Start new data animation with 300ms duration;

        chartTop.startDataAnimation(300);
    }

    private void generateMyLineData(int index, int color) {
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        List<ComputerOuterClass.Computer> list = dataList.get(index);

        chartTop.setCurrentViewport(new Viewport(0, 110, list.size(), 0));

        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        line.setStrokeWidth(1);

        Line line2 = lineData.getLines().get(1);// For this example there is always only one line.
        line2.setColor(Color.BLACK);
        line2.setStrokeWidth(1);

        Line line3 = lineData.getLines().get(2);// For this example there is always only one line.
        line3.setColor(Color.BLUE);
        line3.setStrokeWidth(1);

        Line line4 = lineData.getLines().get(3);// For this example there is always only one line.
        line4.setColor(Color.RED);
        line4.setStrokeWidth(1);

        /*for (int i = 0; i < list.size(); i++) {
            values.add(new PointValue(i, Integer.valueOf(list.get(i).getCpuPackageLoad())));
            values2.add(new PointValue(i, Integer.valueOf(list.get(i).getGpuLoad())));
            values3.add(new PointValue(i, Float.valueOf(list.get(i).getLoadRam())));
            values4.add(new PointValue(i, Float.valueOf(list.get(i).getHddLoad())));
        }*/

        for (int x = 0; x < list.size(); x++) {
            line.getValues().get(x).setTarget(x, Float.valueOf((list.get(x).getCpuPackageLoad())));
            line2.getValues().get(x).setTarget(x, Float.valueOf((list.get(x).getGpuLoad())));
            line3.getValues().get(x).setTarget(x, Float.valueOf((list.get(x).getLoadRam())));
            line4.getValues().get(x).setTarget(x, Float.valueOf((list.get(x).getHddLoad())));
        }
        line.setHasPoints(false);     //todo useful

        line2.setHasPoints(false);    //todo useful;

        line3.setHasPoints(false);    //todo useful

        line4.setHasPoints(false);    //todo useful


        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            //generateLineData(value.getColor(), 100);
            generateMyLineData(columnIndex, value.getColor());
        }

        @Override
        public void onValueDeselected() {

            generateLineData(ChartUtils.COLOR_GREEN, 0);

        }
    }

    private List<List<ComputerOuterClass.Computer>> getList(Map<Integer, ComputerOuterClass.Computer> map) {
        List<ComputerOuterClass.Computer> innerList = new ArrayList<>();
        List<List<ComputerOuterClass.Computer>> outterlist = new ArrayList<>();

        Calendar lastDate = null;
        try {
            lastDate = stringToDate(map.get(1).getDate());
        } catch (Exception e) {
            Log.e("Cal conversion", e.toString());
        }
        Log.e("ListActivity", lastDate.toString());
        boolean isExceded = false;
        boolean isFinished = false;
        int counter = 2;

        lastDate.add(Calendar.HOUR, 1);

        while (!isFinished) {
            isExceded = false;
            innerList = new ArrayList<>();
            while (!isExceded && !isFinished) {
                Calendar mapDate = null;
                try {
                    mapDate = stringToDate(map.get(counter).getDate());
                } catch (Exception e) {
                }
                if (mapDate == null) {
                    isExceded = true;
                    isFinished = true;
                }
                if (mapDate != null && mapDate.compareTo(lastDate) < 0) {
                    innerList.add(map.get(counter++));
                }
                if (mapDate != null && mapDate.compareTo(lastDate) >= 0) {
                    isExceded = true;
                    break;
                }
            }
            lastDate.add(Calendar.HOUR, 1);

            Log.e("Innerlist size", String.valueOf(innerList.size()));
            outterlist.add(innerList);
        }
        Log.e("OutterList size", String.valueOf(outterlist.size()));
        return outterlist;
    }

    public Calendar stringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = sdf.parse(dateString);// all done

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(date); // sets calendar time/date

        return cal;
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

    private Map<Integer, ComputerOuterClass.Computer> getMap(String screenSize, String method) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(getIP(), 43433)
                .usePlaintext(true)
                .build();
        computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);

        ComputerOuterClass.ComputerListResponse response = stub.getComputerListWithName(ComputerOuterClass.ComputerName.newBuilder().setName(screenSize + ";" + readFromPref() + ";" + method).build());
        // System.out.print(response + "The count " + response.getComputerListCount() + '\n');
        Map<Integer, ComputerOuterClass.Computer> map = response.getComputerListMap();
        return map;
    }

    private class GetChart1 extends AsyncTask<String, Integer, Map<Integer, ComputerOuterClass.Computer>> {

        @Override
        protected Map<Integer, ComputerOuterClass.Computer> doInBackground(String... params) {
            //Map<Integer, ComputerOuterClass.Computer> map = getMap();
            return getMap(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Map<Integer, ComputerOuterClass.Computer> result) {
            // Do whatever you need with the string, you can update your UI from here
            dataList = getList(result);

            generateInitialLineData();
            generateColumnData();
            dialog.hide();
        }
    }
}