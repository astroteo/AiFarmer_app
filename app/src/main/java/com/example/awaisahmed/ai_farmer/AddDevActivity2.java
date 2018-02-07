package com.example.awaisahmed.ai_farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by Awais Ahmed on 22/01/2018.
 */

public class AddDevActivity2 extends AppCompatActivity {

    WifiManager wifimanager;
    EditText dev_serialnr;
    EditText dev_pin;
    public static String input_dev_serialnr;
    public static String input_dev_pin;
    String fail_intent = " ";
    SharedPreferences pref;
    EditText edtxtSerial;
    EditText edtxtPin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dev_layout);
        pref = getApplicationContext().getSharedPreferences("UserPref", 0);

        wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo[] wifiInfo = {wifimanager.getConnectionInfo()};
        final String[] ssidd = {wifiInfo[0].getSSID().toString().toLowerCase()};

        fail_intent = getIntent().getStringExtra("failure");
        System.out.println(fail_intent);
        if(fail_intent.equals("toast")){
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Fail connection retry" ,
                    true);
            dialog.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 10000);
        }

        //Dichiarazione delle varibili
        Button sensorConnected = (Button) findViewById(R.id.sensorConnected);
        Button sensorNotConnected = (Button) findViewById(R.id.sensorNotConnected);

        //Listener del bottone del sensore già connesso
        sensorConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostra il dialog del dev per il PIN
                showDialogDev();
                }
        });

        //Listener del bottone del sensore non connesso
        sensorNotConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent per passare all' activity della Scan Dev
                //wifimanager.disconnect();
                wifiInfo[0] =wifimanager.getConnectionInfo();
                ssidd[0] = wifiInfo[0].getSSID().toString().toLowerCase();

                String esp = "esp";
                String ssid_guess = ssidd[0];

                String ssid_correct =ssid_guess.substring(1,ssid_guess.length()-3);

                if (ssid_correct.startsWith(esp)){

                    Intent intent_scan_wifi_2 = new Intent(AddDevActivity2.this, ScanWifiActivity.class);
                    finish();
                    startActivity(intent_scan_wifi_2);

                }  else {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));}
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni
    //Metodo dialog dev
    private void showDialogDev() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_devconnected_layout);
        builder.setMessage("Serial and Pin").setTitle("Insert serial number and PIN number of your device:");

        LayoutInflater inflater = getLayoutInflater();
        edtxtSerial  = findViewById(R.id.dev_serialnr);
        edtxtPin = (EditText) findViewById(R.id.dev_pin);


        builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String devSerial = edtxtSerial.getText().toString();
                String devPin = edtxtPin.getText().toString();
                String result = "";
                String username = pref.getString("user_name", null);
                String myUrl = "http://app.aifarmer.du.cdr.mn/api/user/" + username + "/";
                HttpGetRequest getRequest = new HttpGetRequest(pref);
                try {
                    result = getRequest.execute(myUrl).get();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                }
                int userId = getRequest.deJsonizeId(result);
                HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext());
                task.execute("http://app.aifarmer.du.cdr.mn/api/" + devSerial + "/",
                        "{\"name_sensor\":\"" + devSerial +
                                "\",\"pin_sensor\":\"" + devPin +
                                "\",\"user\":[" + userId + "]}");
                Intent intent_dev_invia = new Intent(AddDevActivity2.this, MainActivity.class);
                startActivity(intent_dev_invia);
                finish();
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
