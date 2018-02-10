package com.example.awaisahmed.ai_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by teobaiguera on 10/02/18.
 */

public class FinishRegActivity extends AppCompatActivity {

    String devSerial = null;
    String devPin =null;

    SharedPreferences pref;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_reg);



        pref = getApplicationContext().getSharedPreferences("UserPref", 0);

        final EditText nameText = (android.widget.EditText) findViewById(R.id.dev_name);
        final EditText pinText = (EditText) findViewById(R.id.dev_pin);


        Button buttonSend = (Button) findViewById(R.id.button_send);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                final ProgressDialog dialog = ProgressDialog.show(FinishRegActivity.this, "", "Connecting..." +
                                "\n wait for  device verification",
                        true);
                dialog.show();
                */

                devSerial = nameText.getText().toString();
                devPin = pinText.getText().toString();

                if ((devPin != null && !devPin.equals("") )&& (devSerial != null && !devSerial.equals(""))) {

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
                        Intent intent_dev_invia = new Intent(FinishRegActivity.this, MainActivity.class);
                        startActivity(intent_dev_invia);
                        finish();
                    /*
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 10000); // 2000 milliseconds for wifi-testing
                    */
                    }

                else{

                    Toast.makeText(getApplicationContext(),"Please insert pin and serial number ",Toast.LENGTH_LONG).show();

                }

            }

        });
    }

}

