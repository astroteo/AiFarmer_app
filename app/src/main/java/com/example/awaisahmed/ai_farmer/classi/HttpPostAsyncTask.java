package com.example.awaisahmed.ai_farmer.classi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.awaisahmed.ai_farmer.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Awais Ahmed on 25/01/2018.
 */

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

    Context activity;
    String response;
    boolean getToken = false;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public HttpPostAsyncTask(Context activity){
        this.activity = activity;
    }

    public HttpPostAsyncTask(Context activity, SharedPreferences pref) {
        this.activity = activity;
        this.pref = pref;
    }

    public HttpPostAsyncTask(Context activity, SharedPreferences pref, SharedPreferences.Editor editor) {
        this.activity = activity;
        this.pref = pref;
        this.editor = editor;
        getToken = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String data = params[1];//data to post
        OutputStream out = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            if (!getToken) {
                String authToken;
                authToken = "Token " + pref.getString("user_token", null);
                urlConnection.setRequestProperty("Authorization", authToken);
            }
            urlConnection.setRequestMethod("POST");
            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            response = convertInputStreamToString(inputStream);
            if (getToken) {
                deJsonizeToken(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    public String convertInputStreamToString(InputStream inputStream) {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     public void deJsonizeToken(String response) {
        String tokenKey = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            tokenKey = jsonObject.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString("user_token", tokenKey);
        editor.commit();
    }
}