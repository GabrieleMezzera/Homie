package com.smartmccg.homie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    TextView SplashText;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        SplashText = (TextView)findViewById(R.id.SplashScreenTitle);
        Typeface SplashFont= Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        SplashText.setTypeface(SplashFont);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Create an intent that will start the main activity.
                Intent ToMain = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(ToMain);

                //Finish splash activity so user cant go back to it.
                SplashScreen.this.finish();

                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.mainmenufadein, R.anim.splashscreenfadeout);
            }
        }, 1500);
    }

}
