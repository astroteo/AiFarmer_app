package com.example.awaisahmed.ai_farmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;
import com.example.awaisahmed.ai_farmer.classi.HumidityReading;
import com.example.awaisahmed.ai_farmer.classi.Reading;
import com.example.awaisahmed.ai_farmer.classi.SingleDevice;
import com.example.awaisahmed.ai_farmer.classi.TemperatureReading;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Awais Ahmed on 22/01/2018.
 */

public class StatusActivity extends AppCompatActivity {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");
    ArrayList<Date> dateTList =new ArrayList<>();
    ArrayList<Float> tList =new ArrayList<>();
    DataPoint[] dpLine, dpPoints;
    GraphView graph = null;
    LineGraphSeries<DataPoint> tSeries;
    PointsGraphSeries<DataPoint> pointTSeries;

    protected String myUrl;
    protected String myUrlT;
    protected String myUrlH;
    protected Button btnOn;
    protected Button btnOff;
    protected Button btnTemp;
    protected Button btnHum;
    protected Button btnRefresh;
    protected SingleDevice device;
    protected TextView txtTemp;
    protected TextView txtHum;
    SharedPreferences pref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_layout);
        pref = getApplicationContext().getSharedPreferences("UserPref", 0);

        btnOn = findViewById(R.id.btOn);
        btnOff = findViewById(R.id.btOff);
        btnTemp = findViewById(R.id.btn_temp);
        btnHum = findViewById(R.id.btn_hum);
        btnRefresh = findViewById(R.id.btRefresch);
        txtTemp = findViewById(R.id.txt_vw_temp);
        txtHum = findViewById(R.id.txt_vw_hum);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        device = (SingleDevice) bundle.getParcelable("selected_device");

        myUrl = "http://app.aifarmer.du.cdr.mn/api/" + device.getNameDevice() + "/";

        myUrlT = myUrl + "t";
        ArrayList<TemperatureReading> tempList = deJsonizeTemperature(getData(myUrlT));
        if (tempList.size() == 0) {
            txtTemp.setText("Null yet");
        } else {
            Float lastTemp = tempList.get(0).getReading();
            txtTemp.setText(lastTemp.toString());
        }

        myUrlH = myUrl + "h";
        ArrayList<HumidityReading> humList = deJsonizeHumidity(getData(myUrlH));
        if (humList.size() == 0) {
            txtHum.setText("Null yet");
        } else {
            Float lastHum = humList.get(0).getReading();
            txtHum.setText(lastHum.toString());
        }
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext(), pref);
                String content = "";
                content = "{\"id\":" + device.getId() +
                        ",\"name_sensor\":\"" + device.getNameDevice() +
                        "\",\"date_added\":\"" + device.getDateAdded() +
                        "\",\"pin_sensor\":\"" + device.getPinDevice() +
                        "\",\"token_sensor\":\"" + device.getTokenDevice() +
                        "\",\"latitude_sensor\":\"" + device.getLatitudeDevice() +
                        "\",\"longitude_sensor\":\"" + device.getLongitudeDevice() +
                        "\",\"activation_sensor\":true,\"owner\":" + device.getOwners() + "}";
                task.execute(myUrl, content);
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext(), pref);
                String content = "";
                content = "{\"id\":" + device.getId() +
                        ",\"name_sensor\":\"" + device.getNameDevice() +
                        "\",\"date_added\":\"" + device.getDateAdded() +
                        "\",\"pin_sensor\":\"" + device.getPinDevice() +
                        "\",\"token_sensor\":\"" + device.getTokenDevice() +
                        "\",\"latitude_sensor\":\"" + device.getLatitudeDevice() +
                        "\",\"longitude_sensor\":\"" + device.getLongitudeDevice() +
                        "\",\"activation_sensor\":false,\"owner\":" + device.getOwners() + "}";
                task.execute(myUrl, content);

            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }


    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected String getData(String myUrl) {
        String result = "";
        String usrToken;
        final SharedPreferences settings = StatusActivity.this.getSharedPreferences("token_log", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        usrToken = settings.getString("token_log", "none");
        HttpGetRequest getRequest = new HttpGetRequest(pref);
        try {
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        return result;
    }

    public ArrayList<TemperatureReading> deJsonizeTemperature(String json) {
        JSONArray jsonArray = null;
        ArrayList<TemperatureReading> temperatureList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(json);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        for(int i=0; i < jsonArray.length(); i++) {
            try {
                TemperatureReading tr = new TemperatureReading();
                tr.setId(jsonArray.getJSONObject(i).getInt("id"));
                tr.setSensorTemperature(jsonArray.getJSONObject(i).getInt("sensor_temperature"));
                tr.setDateTemperature(jsonArray.getJSONObject(i).getString("date_temperature"));
                tr.setTemperatureRead(jsonArray.getJSONObject(i).getInt("temperature_read"));
                temperatureList.add(tr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return temperatureList;
    }

    public ArrayList<HumidityReading> deJsonizeHumidity(String json) {
        JSONArray jsonArray = null;
        ArrayList<HumidityReading> humidityList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(json);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        for(int i=0; i < jsonArray.length(); i++) {
            try {
                HumidityReading hr = new HumidityReading();
                hr.setId(jsonArray.getJSONObject(i).getInt("id"));
                hr.setSensorHumidity(jsonArray.getJSONObject(i).getInt("sensor_humidity"));
                hr.setDateHumidity(jsonArray.getJSONObject(i).getString("date_humidity"));
                hr.setHumidityRead(jsonArray.getJSONObject(i).getInt("humidity_read"));
                humidityList.add(hr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return humidityList;
    }

    protected void drawGraphic(ArrayList<? extends Reading> dataList, final String typeValue) {
        for(int i = dataList.size() -1; i >= 0; i--) {
            float t = dataList.get(i).getReading();
            tList.add(t);
            String dateTString = dataList.get(i).getDate();
            try {
                Date dateT = formatter.parse(dateTString.substring(0,dateTString.indexOf("."))+"Z");
                dateTList.add(dateT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        graph = findViewById(R.id.graph);

        // Set viewport
        graph.getViewport().setScalable(true);
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(StatusActivity.this));
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        if (dateTList.size() > 1) {
            graph.getViewport().setMinX(dateTList.get(0).getTime());
            graph.getViewport().setMinY((float) -10);
            graph.getViewport().setMaxX(dateTList.get(dateTList.size() - 1).getTime());
            graph.getViewport().setMaxY((float) 40);
            System.out.println(dateTList.get(dateTList.size() - 1).getTime());
            System.out.println(dateTList.get(0).getTime());


            dpLine = new DataPoint[]{new DataPoint(dateTList.get(0).getTime(), tList.get(0))};
            tSeries = new LineGraphSeries<>(dpLine);
            dpPoints = new DataPoint[]{new DataPoint(dateTList.get(0).getTime(), tList.get(0))};
            pointTSeries = new PointsGraphSeries<>(dpPoints);

            for (int i = 1; i <= dataList.size() - 2; i++) {
                tSeries.appendData(new DataPoint(dateTList.get(i).getTime(), tList.get(i)), true, 100);
                pointTSeries.appendData(new DataPoint(dateTList.get(i).getTime(), tList.get(i)), true, 100);
            }

            graph.getViewport().setXAxisBoundsManual(true);
            tSeries.setColor(Color.RED);
            pointTSeries.setShape(PointsGraphSeries.Shape.POINT);
            pointTSeries.setSize((float) 30.0);
            pointTSeries.setColor(Color.RED);
            graph.addSeries(pointTSeries);
            graph.addSeries(tSeries);
            tSeries.setTitle(typeValue);
            pointTSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    double time = dataPoint.getX();
                    String DateStr = new SimpleDateFormat("dd-MM-yyyy").format(time);
                    Toast.makeText(StatusActivity.this, typeValue + dataPoint.getY() + " " +
                            "\n Date:" + DateStr, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
