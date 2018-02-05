package com.example.awaisahmed.ai_farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Awais Ahmed on 22/01/2018.
 */

public class AddDevActivity2 extends AppCompatActivity {

    WifiManager wifimanager;
    EditText dev_serialnr;
    EditText dev_pin;
    public static String input_dev_serialnr;
    public static String input_dev_pin;
    String tette = " ";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dev_layout);
        wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo[] wifiInfo = {wifimanager.getConnectionInfo()};
        final String[] ssidd = {wifiInfo[0].getSSID().toString().toLowerCase()};

        tette = getIntent().getStringExtra("failure");
        System.out.println(tette);
        if(tette.equals("toast")){
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
                TextView test = (TextView) findViewById(R.id.test);
                test.setText(ssidd[0]);

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
        builder.setTitle("Inserisci le credenziali del tuo sensore:");

        builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //qui mando pin e seriale
                //Intent per passare all' activity Main
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
