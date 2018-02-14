package com.smartmccg.homie;

import android.animation.ObjectAnimator;
import android.content.Intent;
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
        }, 2500);


        Handler SwitchDelay = new Handler();
        SwitchDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                SwitchActivity();
            }
        }, 3000);


    }

    public void SwitchActivity() {

        Intent ToMain = new Intent(SplashScreen.this, Home.class); //Create an intent that will start the main activity.
        SplashScreen.this.startActivity(ToMain);
        SplashScreen.this.finish(); //Finish splash activity so user cant go back to it.
        overridePendingTransition(R.anim.mainmenufadein, R.anim.splashscreenfadeout); //Apply splash exit (fade out) and main entry (fade in) animation transitions.

    }

    public void MoveTheText() {
        ObjectAnimator TextMovement;
        TextMovement = ObjectAnimator.ofFloat(SplashText, "translationY", 0f, -1000f);
        TextMovement.setDuration(500);
        TextMovement.start();
    }

    public void FadeOutTheLogo() {

        LogoId = findViewById(R.id.HomieLogo);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(0);
        fadeOut.setDuration(2000);
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
        Typeface SplashFont= Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        SplashText.setTypeface(SplashFont);
    }
}
