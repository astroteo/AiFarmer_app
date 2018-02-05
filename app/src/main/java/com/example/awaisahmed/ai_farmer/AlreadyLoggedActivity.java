package com.example.awaisahmed.ai_farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlreadyLoggedActivity extends AppCompatActivity {

    Button logout;
    Button enter;
    TextView tvEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_logged);

        final SharedPreferences pref = getSharedPreferences("UserPref", 0);
        final SharedPreferences.Editor editor = pref.edit();

        tvEnter = findViewById(R.id.welcome_user);
        tvEnter.setText(pref.getString("user_name", null));
        enter = findViewById(R.id.enter);
        logout = findViewById(R.id.logout);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(AlreadyLoggedActivity.this,MainActivity.class)));
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.clear();
                editor.commit();
                startActivity(new Intent(AlreadyLoggedActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}
