package com.smartmccg.homie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;



public class SplashScreen extends AppCompatActivity {
    TextView SplashText;
    ImageView LogoId;
    TextView LoginTitle;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetupThePage();

        FadeOutTheLogo();

        Handler TextDelay = new Handler();
        TextDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                MoveTheText();
            }
        }, 1500);


        Handler SwitchDelay = new Handler();
        SwitchDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                SwitchActivity();
            }
        }, 3000);


    }

    public void SwitchActivity() {
        SplashScreen.this.finish(); //Finish splash activity so user cant go back to it.
        overridePendingTransition(R.anim.mainmenufadein, R.anim.splashscreenfadeout); //Apply splash exit (fade out) and main entry (fade in) animation transitions.
    }

    public void MoveTheText() {
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        float FinalHeight = sp.getFloat("Title_Height", -1);
        float InitialHeight = SplashText.getY();
        FinalHeight = FinalHeight-InitialHeight;
        ObjectAnimator TextMovement = ObjectAnimator.ofFloat(SplashText, "translationY", 0, FinalHeight);
        TextMovement.setDuration(500);
        TextMovement.start();
    }

    public void FadeOutTheLogo() {

        LogoId = findViewById(R.id.HomieLogo);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                LogoId.setAlpha(0f);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationSet LogoFadeOut = new AnimationSet(false); //change to false
        LogoFadeOut.addAnimation(fadeOut);
        LogoId.setAnimation(LogoFadeOut);


    }

    public void SetupThePage() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        SplashText = findViewById(R.id.SplashScreenTitle);
        Typeface SplashFont = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        SplashText.setTypeface(SplashFont);
        LoginTitle = findViewById(R.id.LoginTitle);

    }


}
