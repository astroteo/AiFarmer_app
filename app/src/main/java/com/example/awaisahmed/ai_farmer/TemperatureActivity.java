package com.example.awaisahmed.ai_farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.awaisahmed.ai_farmer.classi.SingleDevice;
import com.example.awaisahmed.ai_farmer.classi.TemperatureReading;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by alfed on 01/02/2018.
 */

public class TemperatureActivity extends StatusActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnTemp.setEnabled(false);
        Intent intent = getIntent();
        getData(myUrlT);
        String result = getData(myUrlT);
        ArrayList<TemperatureReading> temperatureList = deJsonizeTemperature(result);
        drawGraphic(temperatureList, "Temperature");

        btnHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemperatureActivity.this, HumidityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_device", device);
                intent.putExtras(bundle);
                startActivity(intent);
                //aggiungo dei finish
                finish();
            }
        });
    }
}