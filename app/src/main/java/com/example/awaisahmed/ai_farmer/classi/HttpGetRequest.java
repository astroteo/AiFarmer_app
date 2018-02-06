package com.example.awaisahmed.ai_farmer.classi;

/**
 * Created by Awais Ahmed on 25/01/2018.
 */
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.StatusActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HttpGetRequest extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    private String usrToken;
    private SharedPreferences pref;

    public HttpGetRequest(SharedPreferences pref) {
        this.pref = pref;
    }


    @Override
    protected String doInBackground(String... params){
        String stringUrl = params[0];
        String result = null;
        String inputLine = null;
        URL myUrl = null;
        HttpURLConnection connection = null;
        InputStreamReader streamReader = null;
        try {
            myUrl = new URL(stringUrl);
        }
        catch (MalformedURLException mue) {
            mue.printStackTrace();
            result = "";
        }
        try {
            connection = (HttpURLConnection) myUrl.openConnection();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            result = "";
        }
        try {
            usrToken = pref.getString("user_token", null);
            String tokenAuth = "Token " + usrToken;
            connection.setRequestProperty ("Authorization", tokenAuth);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
        }
        catch (ProtocolException pe) {
            pe.printStackTrace();
            result = "";
        }
        try {
            connection.connect();
            streamReader = new InputStreamReader(connection.getInputStream());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            result = "";
        }
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            result = "";
        }
        result = stringBuilder.toString();
        return result;
    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

    public ArrayList<SingleDevice> deJsonizeDevice(String json) {
        JSONArray jsonArray = null;
        ArrayList<SingleDevice> devicesList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(json);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        for(int i=0; i < jsonArray.length(); i++) {
            try {
                SingleDevice sd = new SingleDevice();
                sd.setId(jsonArray.getJSONObject(i).getInt("id"));
                sd.setNameDevice(jsonArray.getJSONObject(i).getString("name_sensor"));
                sd.setDateAdded(jsonArray.getJSONObject(i).getString("date_added"));
                sd.setPinDevice(jsonArray.getJSONObject(i).getString("pin_sensor"));
                sd.setTokenDevice(jsonArray.getJSONObject(i).getString("token_sensor"));
                sd.setLatitudeDevice(jsonArray.getJSONObject(i).getDouble("latitude_sensor"));
                sd.setLongitudeDevice(jsonArray.getJSONObject(i).getDouble("longitude_sensor"));
                sd.setOwners(jsonArray.getJSONObject(i).getString("owner"));
                devicesList.add(sd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return devicesList;
    }

    public int deJsonizeId(String json) {
        JSONObject jsonObject = null;
        int userId = 0;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            userId = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userId;
    }
}