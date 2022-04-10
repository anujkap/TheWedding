package com.anujkap.thewedding.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.anujkap.thewedding.classes.Event;

import java.io.IOException;
import java.util.ArrayList;

public class SharedPrefs {
    private static SharedPrefs instance = null;
    private SharedPreferences prefs;
    private static final String APP_PREFS = "TheWeddding";

    private static final String EVENTS = "events";
    private static final String PASSCODE = "passcode";
    private static final String NAME = "displayName";
    private static final String NUMBER = "number";


    private SharedPrefs(Context context) {
        prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefs(context);
        }
        return instance;
    }

    public void setEvents(String events){
        prefs.edit()
                .putString(EVENTS, events)
                .apply();
    }

    public String getEvents() throws IOException {
        return prefs.getString(EVENTS,ObjectSerializer.serialize(new ArrayList<Event>()));
    }

    public void clearAll(){
        prefs.edit()
                .clear()
                .apply();
    }

    public void setPasscode(String passcode) {
        prefs.edit()
                .putString(PASSCODE, passcode)
                .apply();
    }

    public String getPasscode() {
        return prefs.getString(PASSCODE, "");
    }

    public void setNumber(String number) {
        prefs.edit()
                .putString(NUMBER, number)
                .apply();
    }

    public String getNumber() {
        return prefs.getString(NUMBER, "");
    }

    public void setName(String name) {
        prefs.edit()
                .putString(NAME, name)
                .apply();
    }

    public String getName() {
        return prefs.getString(NAME, "");
    }
}
