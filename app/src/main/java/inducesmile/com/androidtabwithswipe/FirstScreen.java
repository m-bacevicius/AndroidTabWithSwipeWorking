package inducesmile.com.androidtabwithswipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class FirstScreen extends Activity {
    int buttonNumber = 1000;
    int deltaButtonNumber = 0;
    ArrayList<Button> mButtonList;
    LinearLayout linearLayout;
    String[] computerNames = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        /*ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            // Do whatever
        }*/

        if (!((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            if (Settings.Secure.getInt(getContentResolver(), "mobile_data", 1) == 1)     //if it's not connected with wifi but connected with mobile data
            {
                Log.e("MobileData", "connected");
            } else                                                                       //if it's not connected with wifi and not connected with mobile data
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FirstScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                alertDialogBuilder
                        .setMessage("No internet connection on your device. Would you like to enable it?")
                        .setTitle("No Internet Connection")
                        .setCancelable(false)

                        .setPositiveButton(" WiFi ",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        FirstScreen.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });

                alertDialogBuilder.setNeutralButton(" Mobile Data ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        onDestroy();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        }


        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ((EditText) findViewById(R.id.ServerIP)).setText(readIp());

        computerNames = getComputerNames();
        Log.e("Amount of gotten data", String.valueOf(computerNames.length));

        mButtonList = new ArrayList<>();
        if (doRead() != "") {
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
                        saveNameToPref("name", computerNames[v.getId() - buttonNumber]);
                        if (((EditText) findViewById(R.id.ServerIP)).getText().toString() != "") {
                            saveIP(((EditText) findViewById(R.id.ServerIP)).getText().toString().trim());
                        }
                        //saveNameToPref("ServerIp", ((EditText) findViewById(R.id.ServerIP)).getText().toString().trim());

                        saveNameToPref("ServerIP", doRead().split("\n")[v.getId() - buttonNumber].split(";")[1]);
                        saveNameToPref("PcIP", doRead().split("\n")[v.getId() - buttonNumber].split(";")[2]);
                        Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
                        startActivity(intent);
                    }
                });

                //Add the instance to the ArrayList
                mButtonList.add(mButton);

                //Add view to the Parent layout in which you want to add your views
                linearLayout.addView(mButton);
            }
        }


        Button button = (Button) findViewById(R.id.deleteButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doWrite("");
                Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                startActivity(intent);
                onDestroy();
            }
        });
        Button button2 = (Button) findViewById(R.id.addButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doAppend(((EditText) findViewById(R.id.editNameBox)).getText().toString() + ";" + ((EditText) findViewById(R.id.ServerIP)).getText().toString().trim() + ";" + ((EditText) findViewById(R.id.PcIP)).getText().toString().trim() );
                final Button mButton = new Button(getApplicationContext());
                mButton.setText(((EditText) findViewById(R.id.editNameBox)).getText());
                mButton.setId(buttonNumber + (deltaButtonNumber++));
                computerNames = doRead().split("\n");

                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //String temp = ((Button)findViewById(v.getId()).getText();
                        saveNameToPref("name", computerNames[v.getId() - buttonNumber]);
                        Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
                        startActivity(intent);
                    }
                });

                //Add the instance to the ArrayList
                mButtonList.add(mButton);

                //Add view to the Parent layout in which you want to add your views
                linearLayout.addView(mButton);
                //Reload the page
                Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                startActivity(intent);
                onDestroy();
                //because onRestart() doesn't work
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void doWrite(String string)         //Saves all computer names so you don't have to type it everytime
    {
        String filename = "myfile";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveIP(String string)         //Saves last entered IP
    {
        String filename = "myfile2";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String readIp() {
        String filename = "myfile2";
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

    private String[] getComputerNames ()
    {
        String[] result = new String[doRead().split("\n").length];
        int counter = 0;
        for (String line : doRead().split("\n"))
        {
            result[counter++] = line.split(";")[0];
        }
        return result;
    }

    String doRead() {
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

    void doAppend(String text) {
        String newText = doRead() + text;
        doWrite(newText);
        Log.i("doAppend", "Just appended " + text);
    }

    void saveNameToPref(String name, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }
}
