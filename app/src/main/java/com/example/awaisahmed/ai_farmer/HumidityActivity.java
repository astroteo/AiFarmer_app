package com.example.awaisahmed.ai_farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.awaisahmed.ai_farmer.classi.HumidityReading;
import com.example.awaisahmed.ai_farmer.classi.SingleDevice;


import java.util.ArrayList;

/**
 * Created by alfed on 01/02/2018.
 */

public class HumidityActivity extends StatusActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnHum.setEnabled(false);
        Intent intent = getIntent();
        getData(myUrlH);
        String result = getData(myUrlH);
        ArrayList<HumidityReading> humidityList = deJsonizeHumidity(result);
        drawGraphic(humidityList, "Humidity");

        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HumidityActivity.this, TemperatureActivity.class);
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
