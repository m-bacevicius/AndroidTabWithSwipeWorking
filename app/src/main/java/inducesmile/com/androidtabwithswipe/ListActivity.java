package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

import static android.R.attr.data;
import static android.R.attr.value;
import static inducesmile.com.androidtabwithswipe.TestActivity2.getContext;

/**
 * Created by Cube on 10/27/2016.
 */

public class ListActivity extends ActionBarActivity {

    protected ProgressDialog dialog;
    /**
     * Deep copy of data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_column_dependency);
        /*dialog = ProgressDialog.show(ListActivity.this, "",
                "Loading your chart. Please wait...", true);*/
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        private LineChartView chartTop;
        private ColumnChartView chartBottom;

        private LineChartData lineData;
        private ColumnChartData columnData;
        private List<List<ComputerOuterClass.Computer>> dataList;
        protected static ProgressDialog dialog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_line_column_dependency, container, false);

            /*((Button) findViewById(R.id.goFurther)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });*/

            dialog = ProgressDialog.show(getActivity(), "",
                    "Loading your chart. Please wait...", true);

            try {
                GetChart1 asyncRate = new GetChart1();
                dataList = getList(asyncRate.execute().get());
            }
            catch (Exception e)
            {}
            //dataList = getList(getMap());         //TODO change if neccessary

            dialog.hide();

            // *** TOP LINE CHART ***
            chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
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
                    values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
                }

                //axisValues.add(new AxisValue(i).setLabel(months[i]));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
            }

            columnData = new ColumnChartData(columns);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL);

            // chartBottom.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // SelectedValue sv = chartBottom.getSelectedValue();
            // if (!sv.isSet()) {
            // generateInitialLineData();
            // }
            //
            // }
            // });

        }

        /**
         * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
         * will select value on column chart.
         */
        private void generateInitialLineData() {
            int numValues = 100;

            //List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                //axisValues.add(new AxisValue(i).setLabel(days[i]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            lineData = new LineChartData(lines);
            //lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewport- remember to set viewports after data.
            Viewport v = new Viewport(0, 110, 100, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        private void generateLineData(int color, float range) {
            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            // Modify data targets
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            //line.setHasPoints(false);
            for (PointValue value : line.getValues()) {
                // Change target only for Y value.
                value.setTarget(value.getX(), (float) Math.random() * range);
            }
            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private void generateMyLineData(int index, int color) {
            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            List<PointValue> values = new ArrayList<PointValue>();
            List<ComputerOuterClass.Computer> list = dataList.get(index);

            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            line.getValues().clear();

            for (int i = 0; i < list.size(); i++) {
                values.add(new PointValue(i, Integer.valueOf(list.get(i).getCpuPackageTemp())));
            }
            line.setHasPoints(false);
            line.setValues(values);

            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(1500);
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                //generateLineData(value.getColor(), 100);
                generateMyLineData(columnIndex, value.getColor());
                Log.e("SubColumnIndex", String.valueOf(columnIndex));
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
                        //Log.e("lol", "found null");
                        isExceded = true;
                        isFinished = true;
                    }
                    if (mapDate != null && mapDate.compareTo(lastDate) < 0) {
                        //Log.e("lol", "found it smaller");
                        innerList.add(map.get(counter++));
                    }
                    if (mapDate != null && mapDate.compareTo(lastDate) >= 0) {
                        //Log.e("lol", "found it bigger");
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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            String name = settings.getString("name", "");
            return name;
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
        private class GetChart1 extends AsyncTask<Void, Integer, Map<Integer, ComputerOuterClass.Computer>> {

            @Override
            protected Map<Integer, ComputerOuterClass.Computer>  doInBackground(Void... params) {
                //Map<Integer, ComputerOuterClass.Computer> map = getMap();
                return getMap();
            }

            @Override
            protected void onPostExecute(Map<Integer, ComputerOuterClass.Computer> result) {
                // Do whatever you need with the string, you can update your UI from here
                dataList = getList(result);
                //dialog.hide();
            }
        }
    }
}