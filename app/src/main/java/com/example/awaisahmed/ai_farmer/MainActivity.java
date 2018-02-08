package com.example.awaisahmed.ai_farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.SingleDevice;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("UserPref",0);

        //Controllo se sta usando il wifi
        WifiManager wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifimanager.isWifiEnabled() == false)
        {
            wifimanager.setWifiEnabled(true);
        }

        TextView logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView rlusernamem = (TextView) navigationView.getHeaderView(0).findViewById(R.id.rl_username);
        rlusernamem.setText(pref.getString("user_name" ,null));

        //Crea una lista dei device disponibili
        final ArrayList<String> myValues = new ArrayList<String>();
        String result = null;
        String myUrl = "http://app.aifarmer.du.cdr.mn/api/";
        String usrToken = pref.getString("user_token", null);
        HttpGetRequest getRequest = new HttpGetRequest(pref);
        try {
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        ArrayList<SingleDevice> devicesList = getRequest.deJsonizeDevice(result);

        ArrayAdapter<SingleDevice> adapter = new ArrayAdapter<SingleDevice>(this, R.layout.device_row, devicesList);
        final ListView myView =  findViewById(R.id.lst_vw_devices);
        myView.setAdapter(adapter);
        myView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView a, View v, int position, long id) {
                SingleDevice selDevice = (SingleDevice) myView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, TemperatureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_device", selDevice);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //Dichiarazione delle variabili
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Listener del FloatingActionButton per passare allo scan dei dev
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent per passare all' activity Scan Dev
                Intent intent_add_dev = new Intent(MainActivity.this, AddDevActivity.class);
                startActivity(intent_add_dev);
                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        });

        //Mi sa che è del menu laterale
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni
    //Metodo per gestire il bottone back, tenerlo o no--> non cambia niente
    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
       finishAffinity();
    }

    //Metodo per il menu laterale
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        SharedPreferences pref = getSharedPreferences("UserPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
}
