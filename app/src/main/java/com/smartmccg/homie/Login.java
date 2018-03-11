package com.smartmccg.homie;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {
    Button b1;
    EditText ed1,ed2;
    TextView TitleText;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetupThePage();

        LoginManager();



        Handler StartSplashScreen = new Handler();
        StartSplashScreen.postDelayed(new Runnable() {
            @Override
            public void run() {
                PresentSplashScreen();
            }
        }, 50);
    }

    public void SetupThePage() {
        setContentView(R.layout.login);
        context = getApplicationContext();
        TitleText = findViewById(R.id.LoginTitle);
        final Typeface TitleFont= Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        TitleText.setTypeface(TitleFont);
        b1 = findViewById(R.id.button);
        ed1 = findViewById(R.id.login);
        ed2 = findViewById(R.id.password);
    }

    public void LoginManager() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("admin") && ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Login avvenuto",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            Home.class
                    );
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Credenziali errate",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void PresentSplashScreen() {
        Intent ToSplashScreen = new Intent(Login.this, SplashScreen.class); //Create an intent that will start the main activity.
        float TitleHeight = TitleText.getY();
        TitleHeight = TitleHeight + (TitleText.getHeight()/2) + (TitleText.getTextSize() - TitleText.getLineHeight());
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("Title_Height", TitleHeight);
        editor.apply();
        startActivity(ToSplashScreen);
    }
}