package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ComputerOuterClass;
import com.example.computerServiceGrpc;
import com.github.mikephil.charting.charts.Chart;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Created by Cube on 10/25/2016.
 */

public class TestActivity2 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity2);
        //((TextView)findViewById(R.id.testTextView)).setText(doRead());
        ((TextView)findViewById(R.id.testTextView)).setText(readFromPref());

        ((Button)findViewById(R.id.goFurther)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.goToTest)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChartTest.class);
                startActivity(intent);
            }
        });

        /*try{
            ManagedChannel channel = ManagedChannelBuilder.forAddress("158.129.25.160", 43432)
                    .usePlaintext(true)
                    .build();
            computerServiceGrpc.computerServiceBlockingStub stub = computerServiceGrpc.newBlockingStub(channel);
            ComputerOuterClass.ComputerListResponse response = stub.getComputerList(ComputerOuterClass.Empty.newBuilder().build());
            //ComputerOuterClass.Computer response = stub.getRealtimeComputer(ComputerOuterClass.Empty.newBuilder().build());
            System.out.print(response + "The count " + response.getComputerListCount() + '\n');
            Map<Integer, ComputerOuterClass.Computer> map = response.getComputerListMap();
            Log.e("LOG SIZE", String.valueOf(map.size()));
            for (int x = 0; x < map.size(); x++) {
                Log.i("MAP DATA",map.get(x).getCpuCoreTemp());
            }
        }
        catch (Exception e)
        {
            Log.e("ERROR", e.toString());
        }*/

    }
    String doRead()
    {
        String filename = "myfile";
        String string = "Hello world!";
        FileInputStream inputStream;

        /*try {
            inputStream = openFileInput(filename, Context.MODE_PRIVATE);
            inputStream.read(string.getBytes());
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
    String readFromPref()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String name = settings.getString("name", "");
        return name;
    }
}
