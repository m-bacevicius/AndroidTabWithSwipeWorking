package inducesmile.com.androidtabwithswipe;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Cube on 10/25/2016.
 */

public class TestActivity extends Activity {
    int buttonNumber = 1000;
    int deltaButtonNumber = 0;
    ArrayList<Button> mButtonList;
    LinearLayout linearLayout;
    String[] computerNames = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        //doWrite("Testinis stringas");
        //doAppend("Testinis stringas2");
        ((TextView)findViewById(R.id.namesTextView)).setText(doRead());


        computerNames = doRead().split("\n");
        Log.e("Amount of gotten data", String.valueOf(computerNames.length));

        mButtonList = new ArrayList<>();
        for (int x = 0; x < computerNames.length; x++, deltaButtonNumber++) {

            /* can repeat several times*/

            //Create a temporary instance which will be added to the list
            final Button mButton = new Button(this);
            mButton.setText(computerNames[x]);
            mButton.setId(buttonNumber + x);
            //TODO make onClickListener
            //mButton.setOnClickListener();

            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //String temp = ((Button)findViewById(v.getId()).getText();
                    Log.i("LOL", String.valueOf(v.getId()));
                    saveNameToPref(computerNames[v.getId() - buttonNumber]);
                    Intent intent = new Intent(getApplicationContext(), TestActivity2.class);
                    startActivity(intent);
                }
            });

            //Add the instance to the ArrayList
            mButtonList.add(mButton);

            //Add view to the Parent layout in which you want to add your views
            linearLayout.addView(mButton);
        }
        //mButtonList.get(4).setText(doRead());

        //Remove 2nd(index:1) TextView from the parent LinearLayout
        //mLinearLayout.removeView(mTextViewList.get(1));


        Button button = (Button) findViewById(R.id.deleteButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doWrite("");
                ((TextView)findViewById(R.id.namesTextView)).setText(doRead());
            }
        });
        Button button2 = (Button) findViewById(R.id.addButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doAppend(((EditText)findViewById(R.id.editNameBox)).getText().toString());
                ((TextView)findViewById(R.id.namesTextView)).setText(doRead());
                final Button mButton = new Button(getApplicationContext());
                mButton.setText(((EditText) findViewById(R.id.editNameBox)).getText());
                mButton.setId(buttonNumber + (deltaButtonNumber++));
                computerNames = doRead().split("\n");

                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //String temp = ((Button)findViewById(v.getId()).getText();
                        Log.i("LOL", String.valueOf(v.getId()));
                        saveNameToPref(computerNames[v.getId() - buttonNumber]);
                        Intent intent = new Intent(getApplicationContext(), TestActivity2.class);
                        startActivity(intent);
                    }
                });

                //Add the instance to the ArrayList
                mButtonList.add(mButton);

                //Add view to the Parent layout in which you want to add your views
                linearLayout.addView(mButton);
            }
        });
    }
    void doWrite(String string)
    {
        String filename = "myfile";
        //String string = "Deividas myli labuti";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String doRead()
    {
        String filename = "myfile";
        String string = "Hello world!";
        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            Log.e("doRead", sb.toString());
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
    void doAppend (String text)
    {
        String newText = doRead() + text;
        doWrite(newText);
        Log.i("doAppend", "Just appended " + text);
    }
    void saveNameToPref(String text)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", text);
        editor.commit();
    }
}
