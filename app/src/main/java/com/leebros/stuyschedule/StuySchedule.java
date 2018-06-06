package com.leebros.stuyschedule;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by peterlee on 10/10/16.
 */

public class StuySchedule extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
