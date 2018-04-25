package com.smartmccg.homie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login extends AppCompatActivity {
    Button b1;
    EditText ed1,ed2;
    TextView TitleText;
    Context context;
    String userName;

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
        }, 150);

        Handler RestoreVisibility = new Handler();
        RestoreVisibility.postDelayed(new Runnable() {
            @Override
            public void run() {
                TitleText.setVisibility(View.VISIBLE);
                b1.setVisibility(View.VISIBLE);
                ed1.setVisibility(View.VISIBLE);
                ed2.setVisibility(View.VISIBLE);
            }
        }, 1000);
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
        TitleText.setVisibility(View.INVISIBLE);
        b1.setVisibility(View.INVISIBLE);
        ed1.setVisibility(View.INVISIBLE);
        ed2.setVisibility(View.INVISIBLE);

    }

    public void LoginManager() {

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = ed1.getText().toString();
                new AsyncLogin().execute(userName, convertPassMd5(ed2.getText().toString()));
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

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\t" + getString(R.string.loading) + "...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://homiegearin.gear.host/android_login_api/Login.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                SharedPreferences userData = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
                SharedPreferences.Editor SharerUserInfo = userData.edit();
                SharerUserInfo.putString("userName", userName);
                SharerUserInfo.commit();
                startActivity(intent);
                finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_username_password), Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();

            }
        }

    }

    public static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }
}


