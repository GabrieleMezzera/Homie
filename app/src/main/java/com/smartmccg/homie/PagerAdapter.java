/**
 * Created by Andrea on 14/02/2018.
 */
package com.smartmccg.homie;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.smartmccg.homie.Door;
import com.smartmccg.homie.Light;
import com.smartmccg.homie.Temperature;

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
                Temperature temperature = new Temperature();
                return temperature;
            case 1:
                Light light = new Light();
                return light;
            case 2:
                Door door = new Door();
                return door;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}