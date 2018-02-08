package com.example.awaisahmed.ai_farmer.classi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.AddDevActivity;
import com.example.awaisahmed.ai_farmer.LoginActivity;
import com.example.awaisahmed.ai_farmer.MainActivity;
import com.example.awaisahmed.ai_farmer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Awais Ahmed on 25/01/2018.
 */

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

    Context activity;
    String response;
    String err;
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
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedWriter writer = null;
        InputStream inputStream = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        if (!getToken) {
            String authToken;
            authToken = "Token " + pref.getString("user_token", null);
            urlConnection.setRequestProperty("Authorization", authToken);
        }
        try {
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException pe) {
            pe.printStackTrace();
        }
        try {
            out = new BufferedOutputStream(urlConnection.getOutputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        try {
            writer.write(data);
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException ioe){
            ioe.printStackTrace();
            /*
            response = "";
            deJsonizeToken(response);
            */
        }
        response = convertInputStreamToString(inputStream);
        if (getToken) {
            deJsonizeToken(response);
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