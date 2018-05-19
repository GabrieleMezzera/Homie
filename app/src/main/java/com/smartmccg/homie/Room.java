package com.smartmccg.homie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class Room {

    public String Type = null;
    public String Number = null;
    public Context Context = null;
    public String Name = null;

    private ArrayList<Device> Devices = new ArrayList<Device>();

    public void Initialize(String RoomType, Context context, String RoomNumber, String RoomName) {
        Type = RoomType;
        Context = context;
        Number = RoomNumber;
        Name = RoomName;
    }

    public LinearLayout getSummaryView() {
        LinearLayout finalView = new LinearLayout(Context);
        CardView finalCardView = new CardView(Context);
        LinearLayout settedLinearLayout = new LinearLayout(Context);
        ArrayList<LinearLayout> TypeViews = new ArrayList<LinearLayout>();
        ArrayList<String> Types = new ArrayList<String>();
        View Divider = new View(Context);

        Divider.setBackgroundColor(ContextCompat.getColor(Context, R.color.darkerGray));
        TextView CardTitle = new TextView(Context);
        CardTitle.setText(Name);
        CardTitle.setTextSize(20);
        CardTitle.setTypeface(null, Typeface.BOLD);
        CardTitle.setPadding(30,-10,30,0);
        settedLinearLayout.addView(CardTitle);
        Divider.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, 2));

        for (int ActualDevice = 0; ActualDevice < Devices.size(); ActualDevice++) { //Passo Tutti i dispositivi
            String DeviceType = Devices.get(ActualDevice).Typ;
            boolean IsThereAnySame = false;
            for (int ActualType = 0; ActualType < Types.size(); ActualType++) { //Controllo se c'è già un Layout per quel tipo
                if (Types.get(ActualType).equalsIgnoreCase(Devices.get(ActualDevice).Typ)) {
                    IsThereAnySame = true;
                }
            }

            if (IsThereAnySame == false) { //Se il layout Non c'è
                Types.add(Devices.get(ActualDevice).Typ); //Aggiungo il nome del layout
                TypeViews.add(new LinearLayout(Context)); //Aggiungo il Layout
                TextView Title = new TextView(Context); //Costruisco il sottotitolo del tipo
                Title.setText(getDeviceTypeTranslated(DeviceType));
                Title.setTextSize(17);
                Title.setTypeface(null, Typeface.BOLD);
                Title.setPadding(30,15,30,0);
                TypeViews.get(TypeViews.size() - 1).addView(Title);

                if (Devices.get(ActualDevice).Stats.size() > 0) { //Controllo che siano diponibili parametri per il dispositivo
                    TextView TitleView = new TextView(Context);
                    TitleView.setText(Devices.get(ActualDevice).Name);
                    TitleView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                    TitleView.setPadding(30,0,30,0);
                    TypeViews.get(TypeViews.size() - 1).addView(TitleView);

                    for (int ActualStat = 0; ActualStat < Devices.get(ActualDevice).Stats.size(); ActualStat++) {
                        TextView textView = new TextView(Context);
                        textView.setText("• "+Devices.get(ActualDevice).StatsNames.get(ActualStat) + ": " + Devices.get(ActualDevice).Stats.get(ActualStat));
                        textView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setPadding(30,0,30,0);

                        for (int ActualType = 0; ActualType < Types.size(); ActualType++) { //UR
                            if (Types.get(ActualType).equalsIgnoreCase(Devices.get(ActualDevice).Typ)){
                                TypeViews.get(ActualType).addView(textView);
                            }
                        }
                    }
                }
                else {
                    TextView textView = new TextView(Context);
                    textView.setText(Devices.get(ActualDevice).Name+ ": Missing Parameters");
                    textView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setPadding(30,0,30,0);
                    TypeViews.get(TypeViews.size() - 1).addView(textView);
                }

            }
            else {

                if(Devices.get(ActualDevice).Stats.size() > 0) {

                    TextView TitleView = new TextView(Context);
                    TitleView.setText(Devices.get(ActualDevice).Name);
                    TitleView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                    TitleView.setPadding(30,0,30,0);
                    for (int ActualType = 0; ActualType < Types.size(); ActualType++) {
                        if (Types.get(ActualType).equalsIgnoreCase(Devices.get(ActualDevice).Typ)){
                            TypeViews.get(ActualType).addView(TitleView);
                        }
                    }

                    for (int ActualType = 0; ActualType < Types.size(); ActualType++) {
                        if (Types.get(ActualType).equalsIgnoreCase(Devices.get(ActualDevice).Typ)){
                            for (int k = 0; k < Devices.get(ActualDevice).Stats.size(); k++) {
                                TextView textView = new TextView(Context);
                                textView.setText("• "+Devices.get(ActualDevice).StatsNames.get(k) + ": " + Devices.get(ActualDevice).Stats.get(k));
                                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                textView.setPadding(30,0,30,0);
                                TypeViews.get(ActualType).addView(textView);
                            }
                        }
                    }

                }

                else {
                    TextView textView = new TextView(Context);
                    textView.setText(Devices.get(ActualDevice).Name+ ": Missing Parameters");
                    textView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setPadding(30,0,30,0);
                    for (int ActualType = 0; ActualType < Types.size(); ActualType++) {
                        if (Types.get(ActualType).equalsIgnoreCase(Devices.get(ActualDevice).Typ)){
                            TypeViews.get(ActualType).addView(textView);
                        }
                    }
                }
            }

        }

        for (int j = 0; j < TypeViews.size(); j++) {
            TypeViews.get(j).setOrientation(LinearLayout.VERTICAL);
            View DividerView = new View(Context);
            DividerView.setBackgroundColor(ContextCompat.getColor(Context, R.color.darkerGray));
            DividerView.setLayoutParams(new ViewGroup.LayoutParams(Summary.CardWidth, 2));
            View Blank = new View(Context);
            Blank.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 25));
            settedLinearLayout.addView(Blank);
            settedLinearLayout.addView(DividerView);
            settedLinearLayout.addView(TypeViews.get(j));

        }
        settedLinearLayout.setOrientation(LinearLayout.VERTICAL);
        finalCardView.addView(settedLinearLayout);
        finalCardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        finalCardView.setForegroundGravity(Gravity.LEFT);
        finalCardView.setUseCompatPadding(true);
        finalCardView.setRadius(12);
        finalCardView.setContentPadding(0, 30, 0, 30);
        finalView.addView(finalCardView);
        return finalView;
    }

    private String getDeviceTypeTranslated(String deviceType) {
        String OutString = null;
        switch (deviceType) {
            case "Light": {
                OutString = Context.getResources().getString(R.string.lights);
                break;
            }
            case "GasDetector": {
                OutString = Context.getResources().getString(R.string.gasdetector);
                break;
            }
            case "Door": {
                OutString = Context.getResources().getString(R.string.doors);
                break;
            }
            case "Temperature": {
                OutString = Context.getResources().getString(R.string.tempSensor);
                break;
            }
            case "Conditioner": {
                OutString = Context.getResources().getString(R.string.conditioner);
                break;
            }
            case "Radiator": {
                OutString = Context.getResources().getString(R.string.radiator);
                break;
            }
            case "Window": {
                OutString = Context.getResources().getString(R.string.windows);
                break;
            }
            case "Antitheft": {
                OutString = Context.getResources().getString(R.string.antitheft);
                break;
            }
            default: {
                OutString = deviceType;
                break;
            }

        }
        return OutString;
    }

    public void updateDeviceValue (String ValueName, String Value, String DeviceType, String DeviceNumber) {
        for (int i = 0; i<Devices.size(); i++) {
            if (Devices.get(i).Typ.equals(DeviceType) && Devices.get(i).Number.equalsIgnoreCase(DeviceNumber)) {
                for(int k = 0; k < Devices.get(i).StatsNames.size(); k++) {
                    if (Devices.get(i).StatsNames.get(k).equals(ValueName))
                        Devices.get(i).Stats.set(k, Value);
                }
            }
        }
    }

    public void addDevice (String Type, String DevNumber, String DevName) {
        boolean Added = false;
        if (Devices.size() > 0) {
            for (int i = 0; i<Devices.size(); i++) {
                if (Devices.get(i).Typ.equalsIgnoreCase(Type) && Devices.get(i).Number.equalsIgnoreCase(DevNumber)) {
                    Devices.get(i).Initialize(Type, DevNumber, DevName);
                    Added = true;
                }
            }
            if (Added == false) {
                Devices.add(new Device());
                Devices.get(Devices.size()-1).Initialize(Type, DevNumber, DevName);
            }
        }
        else {
            Devices.add(new Device());
            Devices.get(Devices.size()-1).Initialize(Type, DevNumber, DevName);
        }
    }

    public void addDeviceValue (String ValueName, String Value, String DeviceType, String DeviceNumber) {
        for (int i = 0; i<Devices.size(); i++) {
            if (Devices.get(i).Typ.equals(DeviceType) && Devices.get(i).Number.equalsIgnoreCase(DeviceNumber)) {
                Devices.get(i).addStatus(ValueName, Value);
            }
        }
    }
}
