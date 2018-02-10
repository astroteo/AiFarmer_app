package com.example.awaisahmed.ai_farmer;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;
import com.example.awaisahmed.ai_farmer.classi.WifiAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by teobaiguera on 10/02/18.
 */
public class ScanWifiActivity_ALT extends AppCompatActivity {
    WifiManager wifimanager;
    String ssid = null;
    WifiAdapter wifiAdapter_wifi;
    String selectedWifi;
    EditText edtxtSerial ;
    EditText edtxtPin;
    String fail_intent ="";
    SharedPreferences pref;
    public static final int DEFAULT_MAX_RETRIES = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanwifi_layout);
        pref = getApplicationContext().getSharedPreferences("UserPref", 0);
        System.out.println("THIS ACTIVITY");

        //Dichiarazione delle variabili
        FloatingActionButton scan_btn = (FloatingActionButton) findViewById(R.id.scan_btn);
        final ListView wifilist = (ListView) findViewById(R.id.wifilist);
        wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //controllare se wifi acceso o no quindi viene accesso
        if (wifimanager.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "WiFi spento...Accensione in corso", Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        } else Toast.makeText(this, "Scansione in corso delle WiFi...", Toast.LENGTH_LONG).show();


        //Inizio scan delle wifi
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get List of ScanResults
                final List<ScanResult> result = wifimanager.getScanResults();/////////////<----------------
                // Create Temporary HashMap
                HashMap<String, ScanResult> map = new HashMap<String, ScanResult>();
                // Add ScanResults to Map to remove duplicates
                for(ScanResult scanResult : result){
                    if(scanResult.SSID != null && !scanResult.SSID.isEmpty() && !scanResult.SSID.startsWith("esp")){
                        map.put(scanResult.SSID, scanResult);
                    }
                }
                // Add to new List
                final List<ScanResult> results = new ArrayList<ScanResult>(map.values());/////////////------->
                // Create Comparator to sort by level
                Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        return (o1.level < o2.level ? -1 : (o1.level == o2.level ? 0 : 1));
                    }
                };
                if(results == null){
                    Toast.makeText(getApplicationContext(), "No WiFi Networks", Toast.LENGTH_SHORT).show();
                }
                // Apply Comparator and sort
                Collections.sort(results, comparator);
                //Adapter
                wifiAdapter_wifi = new WifiAdapter(ScanWifiActivity_ALT.this, results);/////////////------->
                wifilist.setAdapter(wifiAdapter_wifi);
            }
        }, filter);
        wifimanager.startScan();

        Toast.makeText(this, "Passa la passsword del wifi per connettere il tuo DEV",Toast.LENGTH_LONG).show();

        //gestire click
        wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedWifi = (String) wifiAdapter_wifi.getItem(position);
                showDialogWifi();
            }
        });
        //Bottone scan di scansione
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifimanager.startScan();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni
    //Metodo show dialog wifi
    private void showDialogWifi(){
        AlertDialog.Builder builder_wifi = new AlertDialog.Builder(this);
        builder_wifi.setTitle("Password");
        final EditText psw_text = new EditText(this);
        psw_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder_wifi.setView(psw_text);

        builder_wifi.setPositiveButton("Connettiti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String psw = psw_text.getText().toString();
                sendAndCheckSsidPswd(selectedWifi, psw);

                final ProgressDialog dialog_connection_test = ProgressDialog.show(ScanWifiActivity_ALT.this, "", "Connecting..." +
                                "\n 35 seconds for WiFi testing",
                        true);
                dialog_connection_test.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getResponse();
                        dialog_connection_test.dismiss();

                    }
                }, 40000);
                //showDialogDev();
                //controllo del pin s'è giusto
                //showDialogDev();

                // qui non va bene Toast.makeText(getApplicationContext(),"Riprova",Toast.LENGTH_SHORT).show();
            }
        });
        builder_wifi.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder_wifi.show();
    }

    //funzioni passate da matteo
    private void sendAndCheckSsidPswd(final String ssidIns, final String pswdIns)  {
        final String[] Req_send = {new String()};
        String url = "http://192.168.4.1?ssid=" + ssidIns+"&pswd="+pswdIns;
        //String url = "http://www.google.com" + pin;
        RequestQueue queue = Volley.newRequestQueue(this);
        String ReqString =new String();
        ReqString = "no Message received";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //checkResp.setText(response.toString());
                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    //Metodo
    private void  simpleClient() {
        String url = "http://192.168.4.1";
        RequestQueue queue = Volley.newRequestQueue(this);
        String respString = "";
        //final TextView checkResp =(TextView) findViewById(R.id.view_check);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //checkResp.setText(response.toString());
                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) );


    }
    //Metodo per il show dialog dev

    private void getResponse( )  {
        final String[] Req_send = {new String()};
        String url = "http://192.168.4.1";
        //String url = "http://www.google.com" + pin;
        RequestQueue queue = Volley.newRequestQueue(this);
        String ReqString =new String();
        ReqString = "no Message received";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().startsWith("!!OK!!")){

                            for (int i=0;i<5; i++) {
                                simpleClient();
                            }
                            //showDialogDev();
                            final ProgressDialog dialog_connection_test_result = ProgressDialog.show(ScanWifiActivity_ALT.this, "", "Connecting..." +
                                            "GOOD YOUR SENSOR IS NOW CONNECTED\n... wait for registration ",
                                    true);
                            dialog_connection_test_result.show();

                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Intent goalIntent= new Intent(ScanWifiActivity_ALT.this,FinishRegActivity.class);
                                    //failIntent.putExtra("failure", fail_intent = "toast");

                                    startActivity(goalIntent);

                                    finish();
                                }
                            }, 15000);




                        }
                        else if (response.toString().startsWith("!!NO!!")){
                            // add here the second Progress dialog [ !! in arduino settled up to 3000]
                            simpleClient();
                            final ProgressDialog dialog_connection_test_result = ProgressDialog.show(ScanWifiActivity_ALT.this, "", "Connecting..." +
                                            "FAIL YOUR SENSOR DID NOT CONNECTED\n...try agaun ",
                                    true);
                            dialog_connection_test_result.show();

                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Intent failIntent= new Intent(ScanWifiActivity_ALT.this,AddDevActivity.class);
                                    //failIntent.putExtra("failure", fail_intent = "toast");

                                    startActivity(failIntent);

                                    finish();
                                }
                            }, 15000);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) );


        System.out.println(stringRequest);
        if (stringRequest.toString().length() >0)
        {
            Toast.makeText(getApplicationContext(),  stringRequest.toString(), Toast.LENGTH_LONG).show();
        }
    }



    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
