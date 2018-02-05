package com.example.awaisahmed.ai_farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awaisahmed.ai_farmer.classi.HttpGetRequest;
import com.example.awaisahmed.ai_farmer.classi.HttpPostAsyncTask;

/**
 * Created by Awais Ahmed on 22/12/2017.
 */

public class RegisterActivity extends AppCompatActivity {


    EditText input_username;
    EditText input_email;
    EditText input_password;
    EditText input_conferma_password;
    Button button_register;

    String rusername;
    String remail;
    String rpsw;
    String rcpsw;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        pref = getApplicationContext().getSharedPreferences("UserPref",0);
        editor = pref.edit();

        input_username = (EditText) findViewById(R.id.username);
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText) findViewById(R.id.password);
        input_conferma_password = (EditText) findViewById(R.id.conferma_password);
        button_register = (Button) findViewById(R.id.button_register);

        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideKeyboard();

                rusername = input_username.getText().toString();
                remail = input_email.getText().toString();
                rpsw = input_password.getText().toString();
                rcpsw = input_conferma_password.getText().toString();

                if(rpsw.equals(rcpsw)){
                    HttpPostAsyncTask task = new HttpPostAsyncTask(getApplicationContext(), pref, editor);
                    task.execute("http://app.aifarmer.du.cdr.mn/rest-auth/registration/",
                            "{\"username\":\"" + rusername + "\"" +
                                    ",\"email\":\"" + remail + "\"" +
                                    ",\"password1\":\"" + rpsw + "\"" +
                                    ",\"password2\":\"" + rcpsw + "\"}");
                    editor.putString("user_name", rusername);
                    editor.commit();
                    readRegisterUsername();
                } else {
                    Toast.makeText(getApplicationContext(), "Password fields didn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////Funzioni

    private void readRegisterUsername() {
        Intent intent_username_register = new Intent(this,MainActivity.class);
        intent_username_register.putExtra("string_username_login", rusername);
        startActivity(intent_username_register);
    }
    //Metodo per nascondere la tastiera
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //Metodo per gestire il bottone back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

