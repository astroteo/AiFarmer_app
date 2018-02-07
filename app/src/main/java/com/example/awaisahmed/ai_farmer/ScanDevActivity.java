package com.example.awaisahmed.ai_farmer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.example.awaisahmed.ai_farmer.classi.WifiAdapter;

/**
 * Created by Awais Ahmed on 15/01/2018.
 */

public class ScanDevActivity extends AppCompatActivity {

    public WifiManager wifimanager;
    public WifiAdapter wifiAdapter;
    public ListView wifilist;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanwifi_layout);

        //Dichiarazione delle variabili
        FloatingActionButton scan_btn = (FloatingActionButton) findViewById(R.id.scan_btn);
        wifilist = (ListView) findViewById(R.id.wifilist);

        //Wifi Manager
        wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //Controllo dello stato del WIFI, e accendione se spento
        /*if (wifimanager.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "WiFi spento...Accensione in corso", Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        }

        Toast.makeText(this, "Ricerca in corso del device...", Toast.LENGTH_SHORT).show();*/

        //Inizio scan delle wifi
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get List of ScanResults
                List<ScanResult> result = wifimanager.getScanResults();/////////////<----------------
                // Create Temporary HashMap
                HashMap<String, ScanResult> map = new HashMap<String, ScanResult>();
                // Add ScanResults to Map to remove duplicates
                for(ScanResult scanResult : result){
                    if(scanResult.SSID.startsWith("esp")){
                        map.put(scanResult.SSID, scanResult);
                    }
                }
                // Add to new List
                List<ScanResult> results = new ArrayList<ScanResult>(map.values());/////////////------->
                // Create Comparator to sort by level
                Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        return (o1.level < o2.level ? -1 : (o1.level == o2.level ? 0 : 1));
                    }
                };
                // Apply Comparator and sort
                Collections.sort(results, comparator);
                //Adapter
                wifiAdapter = new WifiAdapter(ScanDevActivity.this, results);/////////////------->
                wifilist.setAdapter(wifiAdapter);
            }
        }, filter);
        wifimanager.startScan();

        Toast.makeText(this, "Ricerca in corso del device...", Toast.LENGTH_LONG).show();

        //Ho la wifilist dei dev e gestisco il click sulle listview
        wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDev = (String) wifiAdapter.getItem(position);
                Toast.makeText(getApplicationContext(), "Collegati a "+selectedDev,Toast.LENGTH_LONG).show();
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

                //goToNextActivity();

                //if (ssid.equals("esp32")){
                //    goToNextActivity();
                //}






                //WifiConfiguration conf = new WifiConfiguration();
                //conf.SSID = "\"" + selectedDev + "\"";
                //conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //conf.allowedAuthAlgorithms.clear();
                //wifimanager.addNetwork(conf);

                /*for (WifiConfiguration i : wifimanager.getConfiguredNetworks()) {
                    if(i.SSID != null && i.SSID.equals("\"" + selectedDev + "\"")) {

                        wifimanager.disconnect();
                        wifimanager.enableNetwork(i.networkId, true);
                        wifimanager.reconnect();
                        Toast.makeText(getApplicationContext(), "Mi sto collegando a "+selectedDev,Toast.LENGTH_LONG).show();

                        break;
                    }
                ///FINO QUI DOPO VARI TENTETIVI FUNZIONA
                }*/

                //ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                //NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //if(networkInfo.isConnected()){
                //    goToNextActivity();
                //}
                //ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                //NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
                //if (networkInfo.equals(selectedDev)){
                //    goToNextActivity();
                //}


            }
        });

        WifiInfo wifiInfo = wifimanager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        while (ssid.equals("esp32")){
            goToNextActivity();
        }

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifimanager.startScan();
            }
        });
    }

    private void goToNextActivity(){
        Intent intent_to_go_scan_wifi = new Intent(ScanDevActivity.this, ScanWifiActivity.class);
        startActivity(intent_to_go_scan_wifi);
    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni
    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

