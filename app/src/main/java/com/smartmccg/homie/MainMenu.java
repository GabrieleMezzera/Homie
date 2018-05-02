package com.smartmccg.homie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainMenu extends AppCompatActivity {
    PahoMqttClient mqttHelper;
    MqttAndroidClient client;
    int LoadingStatus = 0;
    private DrawerLayout navigationMenuDrawer;
    private TextView userNameinNavDraw;
    private boolean TopicSelection;
    private String NavDrawHeaderUrl;
    private boolean UrlRecieved;
    private Bitmap NavDrawHeaderBitmap;
    private boolean NavHeaderReady = false;
    private boolean PlacesRecieved = false;
    private String ActualFragmentName;
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActualFragmentName = getResources().getString(R.string.summary);
        replaceFragment(new Summary());
        setContentView(R.layout.activity_main_menu);
        appContext = getApplicationContext();
        SharedPreferences sp = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        String userName = sp.getString("userName", null);
        HandleMqtt();
        new MainMenu.AsyncTopicRequest().execute(userName);
        LoadingTopicsListener();
        SetupthePage();
        NavigationDrawerClickHandler();
    }

    public void SetupthePage() {
        SharedPreferences sp = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
        navigationMenuDrawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        if (LoadingStatus == 1) {
            String FirstTopic = sp.getString("Description_1", null);
            sp.edit().putString("Description", sp.getString("Description_1", null)).commit();
            sp.edit().putString("Topic", sp.getString("Topic_1", null)).commit();
            String HtmlReadyTitle = "<font color='" + String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.pureWhite)) + "'>" + FirstTopic + "</font>";
            actionbar.setTitle(Html.fromHtml(HtmlReadyTitle));
        } else {
            String HtmlReadyTitle = "<font color='" + String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.pureWhite)) + "'>" + getString(R.string.app_name) + "</font>";
            actionbar.setTitle(Html.fromHtml(HtmlReadyTitle));
        }
        new ImageUrlRequest().execute();
        while (UrlRecieved != true) {
        }

        new DownloadImageTask().execute(NavDrawHeaderUrl);

    }

    public void PreTopicChange(NavigationView navigationView) {
        navigationView.getMenu().clear();
        SharedPreferences sp = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
        for (int i = 0; i < sp.getInt("topic_number", 1); i++) {
            String DescriptionKey = "Description_" + (i + 1);
            navigationView.getMenu().add(sp.getString(DescriptionKey, null)).setIcon(R.drawable.ic_map_pin_marked);
            TopicSelection = true;
        }
    }

    public void ManageTopicChange(MenuItem menuItem, NavigationView navigationView) {
        SharedPreferences sp = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
        try {
            mqttHelper.unSubscribe(client, sp.getString("Topic", null));
        } catch (MqttException e) {
            e.printStackTrace();
        }
        String SelectedDescription = menuItem.toString();
        String TopicSelected = null;
        for (int i = 0; i < sp.getInt("topic_number", 1); i++) {
            if (sp.getString("Description_" + (i + 1), null).equals(SelectedDescription)) {
                TopicSelected = sp.getString("Topic_" + (i + 1), null);
                break;
            }
        }
        sp.edit().putString("Description", SelectedDescription).commit();
        sp.edit().putString("Topic", TopicSelected).commit();
        try {
            mqttHelper.subscribe(client, sp.getString("Topic", null), 1);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), SelectedDescription + " " + getString(R.string.selected), Toast.LENGTH_LONG).show();
        String HtmlReadyTitle = "<font color='" + String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.pureWhite)) + "'>" + sp.getString("Description", null) + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(HtmlReadyTitle));
        navigationMenuDrawer.closeDrawers();
        TopicSelection = false;
    }

    public void NavigationDrawerClickHandler() {

        TopicSelection = false;
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);

                        // close drawer when item is tapped
                        if (TopicSelection == true) {
                            ManageTopicChange(menuItem, navigationView);
                        }

                        if (menuItem.toString().equalsIgnoreCase(getString(R.string.change_topic))) {
                            if (LoadingStatus == -3) {
                                Toast.makeText(getApplicationContext(), getString(R.string.missing_topics), Toast.LENGTH_LONG).show();
                            } else {
                                PreTopicChange(navigationView);
                            }
                        }

                        if (TopicSelection == false) {
                            navigationMenuDrawer.closeDrawers();
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigationMenuDrawer.openDrawer(GravityCompat.START);
                NavigationView NavView = findViewById(R.id.nav_view);
                NavView.getMenu().clear();
                MakeNavDrawMenu(NavView);
                LinearLayout linearLayout = findViewById(R.id.nav_header_layout);
                SharedPreferences userinfo = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
                userNameinNavDraw = findViewById(R.id.nav_username);
                userNameinNavDraw.setText(userinfo.getString("userName", null));
                SharedPreferences sp = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
                TextView selectedTopicinNavDraw = findViewById(R.id.nav_topicselected);
                if (NavHeaderReady == true) {
                    linearLayout.setBackground(new BitmapDrawable(ThumbnailUtils.extractThumbnail(NavDrawHeaderBitmap, (NavDrawHeaderBitmap.getHeight() * linearLayout.getWidth() / linearLayout.getHeight()), NavDrawHeaderBitmap.getHeight())));
                } else {
                    linearLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }
                if (LoadingStatus == -3) {
                    selectedTopicinNavDraw.setText(getString(R.string.selectedTopic) + ": " + getString(R.string.notopicavailable));
                } else {
                    selectedTopicinNavDraw.setText(getString(R.string.selectedTopic) + ": " + sp.getString("Topic", getString(R.string.notopicavailable)));
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadingTopicsListener() {
        while (LoadingStatus == 0) {
        }
        SharedPreferences aboutTopics = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
        if (LoadingStatus == 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.loaded) + " " + aboutTopics.getInt("topic_number", -1) + " Topics", Toast.LENGTH_LONG).show();
        } else if (LoadingStatus == -1) {
            Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        } else if (LoadingStatus == -2) {
            Toast.makeText(getApplicationContext(), getString(R.string.Query_error), Toast.LENGTH_LONG).show();
        } else if (LoadingStatus == -3) {
            Toast.makeText(getApplicationContext(), getString(R.string.missing_topics), Toast.LENGTH_LONG).show();
        }
    }

    public void MakeNavDrawMenu(NavigationView NavView) {
        Menu NavDrawMenu = NavView.getMenu();
        SharedPreferences sp = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);

        SubMenu General = NavDrawMenu.addSubMenu(getResources().getString(R.string.general));
        if (ActualFragmentName.equals(getResources().getString(R.string.summary))) {
            General.add(getResources().getString(R.string.summary)).setIcon(R.drawable.ic_house).setChecked(true);
        } else {
            General.add(getResources().getString(R.string.summary)).setIcon(R.drawable.ic_house);
        }
        if (PlacesRecieved == true) {
            SubMenu Places = NavDrawMenu.addSubMenu(getResources().getString(R.string.places));
        }
        SubMenu Topics = NavDrawMenu.addSubMenu("Topics");
        Topics.add(getString(R.string.change_topic)).setIcon(R.drawable.ic_move_option);
    }

    public void replaceFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void HandleMqtt() {
        SharedPreferences userinfo = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        final SharedPreferences sp = getSharedPreferences( "about_topics", Activity.MODE_PRIVATE);
        mqttHelper = new PahoMqttClient();
        client = mqttHelper.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, userinfo.getString("userName", null));
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                try {
                    mqttHelper.subscribe(client, sp.getString("Topic", null), 1);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                sp.edit().putString("LastMessage", new String (mqttMessage.getPayload())).commit();
                Toast.makeText(getApplicationContext(), sp.getString("LastMessage", null), Toast.LENGTH_LONG).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private class AsyncTopicRequest extends AsyncTask<String, String, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainMenu.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\t" + getString(R.string.TopicsRequest) + "...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://homiegearin.gear.host/android_login_api/RequestTopics.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0]);
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
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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

                    String finalRawMessage = result.toString();
                    // Pass data to onPostExecute method
                    if (finalRawMessage.equalsIgnoreCase("ERROR: Could not connect. ") || finalRawMessage.equalsIgnoreCase("ERROR: Could not able to execute the Query") || finalRawMessage.equalsIgnoreCase("No records matching your query were found.")) {
                        SharedPreferences aboutTopics = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor StoryTeller = aboutTopics.edit();
                        StoryTeller.putInt("topic_number", 0);
                        StoryTeller.commit();
                        if (finalRawMessage.equalsIgnoreCase("ERROR: Could not connect. ")) {
                            LoadingStatus = -1;
                        } else if (finalRawMessage.equalsIgnoreCase("ERROR: Could not able to execute the Query")) {
                            LoadingStatus = -2;
                        } else if (finalRawMessage.equalsIgnoreCase("No records matching your query were found.")) {
                            LoadingStatus = -3;
                        }

                    } else {
                        String[] splitTopics = finalRawMessage.split("ç");
                        SharedPreferences aboutTopics = getSharedPreferences("about_topics", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor StoryTeller = aboutTopics.edit();
                        StoryTeller.putInt("topic_number", splitTopics.length);
                        for (int i = 0; i < splitTopics.length; i++) {
                            String SplitString[] = splitTopics[i].split(String.valueOf("#"));
                            String TopicKeyName = "Topic_" + (i + 1);
                            StoryTeller.putString(TopicKeyName, SplitString[0]);
                            String DescriptionKeyName = "Description_" + (i + 1);
                            StoryTeller.putString(DescriptionKeyName, SplitString[1]);
                        }
                        StoryTeller.commit();
                        LoadingStatus = 1;
                    }

                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            } finally {
                conn.disconnect();
                pdLoading.dismiss();
            }


            return null;
        }

    }

    private class ImageUrlRequest extends AsyncTask<String, String, Void> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://homiegearin.gear.host/nav_draw_support/RandomImage.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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

                    NavDrawHeaderUrl = result.toString();
                    UrlRecieved = true;
                    // Pass data to onPostExecute method

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }


            return null;
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            NavDrawHeaderBitmap = result;
            NavHeaderReady = true;
        }
    }




}
