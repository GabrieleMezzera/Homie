/**
 * Created by Andrea on 14/02/2018.
 */
package com.smartmccg.homie.kitchen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Light light = new Light();
                return light;
            case 1:
                SensorGas sensorGas = new SensorGas();
                return sensorGas;
            case 2:
                Window window = new Window();
                return window;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}