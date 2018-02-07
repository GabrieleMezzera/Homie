package com.smartmccg.homie;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

        MoveTheText();

        SwitchActivity();


    }

    public void SwitchActivity() {

        Intent ToMain = new Intent(SplashScreen.this, MainActivity.class); //Create an intent that will start the main activity.
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

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(0);
        fadeOut.setDuration(2000);

        AnimationSet LogoFadeOut = new AnimationSet(false); //change to false
        LogoFadeOut.addAnimation(fadeOut);
        LogoId = findViewById(R.id.HomieLogo);
        LogoId.setAnimation(LogoFadeOut);
        showLockTaskEscapeMessage();


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
