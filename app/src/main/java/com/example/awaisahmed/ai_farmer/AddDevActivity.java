package com.example.awaisahmed.ai_farmer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;
import com.example.awaisahmed.ai_farmer.classi.WifiAdapter;

import java.util.concurrent.ExecutionException;

/**
 * Created by Awais Ahmed on 22/01/2018.
 */

public class AddDevActivity  extends AppCompatActivity {

    WifiManager wifimanager;
    EditText dev_serialnr;
    EditText dev_pin;
    public static String input_dev_serialnr;
    public static String input_dev_pin;
    SharedPreferences pref;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dev_layout);
        pref = getApplicationContext().getSharedPreferences("UserPref", 0);


        wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo[] wifiInfo = {wifimanager.getConnectionInfo()};
        final String[] ssidd = {wifiInfo[0].getSSID().toString().toLowerCase()};

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

                    Intent intent_scan_wifi_2 = new Intent(AddDevActivity.this, ScanWifiActivity.class);
                    finish();
                    startActivity(intent_scan_wifi_2);

                }  else {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    Toast.makeText(getApplicationContext(),"Collegati al tuo DEV esp",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni
    //Metodo dialog dev

    private void showDialogDev() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Serial and Pin").setTitle("Insert serial number and PIN number of your device:");
        builder.setTitle("Insert serial number and PIN number of your device:");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_devconnected_layout, null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        Button btnPositive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button btnNegative = dialogView.findViewById(R.id.dialog_negative_btn);
        final EditText edtxtSerial  = dialogView.findViewById(R.id.dev_serialnr);
        final EditText edtxtPin = dialogView.findViewById(R.id.dev_pin);

        final AlertDialog alertDialog = builder.create();


        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
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
                HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext(), pref);
                String bodyPost = "{\"name_sensor\":\"" + devSerial +
                        "\",\"pin_sensor\":\"" + devPin +
                        "\",\"user\":[" + userId + "]}";
                String postUrl = "http://app.aifarmer.du.cdr.mn/api/" + devSerial + "/";
                task.execute(postUrl, bodyPost);
                Intent intent_dev_invia = new Intent(AddDevActivity.this, MainActivity.class);
                startActivity(intent_dev_invia);
                finish();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
