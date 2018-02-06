package com.example.awaisahmed.ai_farmer;

import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Created by Awais Ahmed on 21/12/2017.
 */

public class LoginActivity extends AppCompatActivity {

    EditText input_username;
    EditText input_password;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ScrollView myLayout = this.findViewById(R.id.parent_scroll_view);
        myLayout.requestFocus();

        pref = getApplicationContext().getSharedPreferences("UserPref",0);
        editor = pref.edit();

        input_username = (EditText) findViewById(R.id.username);
        input_password = (EditText) findViewById(R.id.password);
        TextView or = (TextView) findViewById(R.id.or);
        Button button_register = (Button) findViewById(R.id.button_register);
        Button button_login = (Button) findViewById(R.id.button_login);

        Toast.makeText(this, "Welcome to AI Farmer App", Toast.LENGTH_SHORT).show();


        if (pref.contains("user_name") && pref.contains("user_token")) {
            startActivity(new Intent(LoginActivity.this,AlreadyLoggedActivity.class));
            finish();
        }


        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideKeyboard();
                final ProgressDialog dialog_connection_test = ProgressDialog.show(LoginActivity.this, "", "logging... wait a bit" +
                                "\n ",
                        true);
                dialog_connection_test.show();
                editor.putString("user_name", input_username.getText().toString());
                editor.commit();
                String rpsw = input_password.getText().toString();
                HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext(), pref, editor);
                try {
                    task.execute("http://app.aifarmer.du.cdr.mn/rest-auth/login/",
                            "{\"username\":\"" + pref.getString("user_name", null) + "\"" +
                                    ",\"password\":\"" + rpsw + "\"}");
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,"Unregistered user or wrong password",Toast.LENGTH_LONG);
                    e.printStackTrace();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog_connection_test.dismiss();
                    }
                }, 5000);
                Intent intent_register_register = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent_register_register);
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent_register_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_register_register);
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
