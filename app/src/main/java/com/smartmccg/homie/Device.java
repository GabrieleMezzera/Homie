package com.smartmccg.homie;

import java.util.ArrayList;

public class Device {
    public String Typ = null;
    public String Number = null;
    public String Name = null;
    public ArrayList<String> StatsNames = new ArrayList<String>();
    public ArrayList<String> Stats = new ArrayList<String>();



    public void Initialize(String Type, String DeviceNumber, String DeviceName) {
        Typ = Type;
        Number = DeviceNumber;
        Name = DeviceName;
    }

    public void addStatus(String StatName, String Status) {
        boolean Added = false;
        if (Stats.size() > 0) {
            for (int i = 0; i<Stats.size(); i++) {
                if (StatsNames.get(i).equalsIgnoreCase(StatName)) {
                    Stats.set(i, Status);
                    Added = true;
                }
            }
            if (Added == false) {
                Stats.add(new String(StatName));
                StatsNames.add(StatName);
            }
        }
        else {
            this.Stats.add(Status);
            this.StatsNames.add(StatName);
        }
    }





}
