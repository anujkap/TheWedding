package com.anujkap.thewedding.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.anujkap.thewedding.Helpers.SharedPrefs;
import com.anujkap.thewedding.classes.Event;
import com.anujkap.thewedding.MainActivity;
import com.anujkap.thewedding.Helpers.ObjectSerializer;
import com.anujkap.thewedding.R;
import com.anujkap.thewedding.auth.CheckVerifiedActivity;
import com.anujkap.thewedding.auth.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private FirebaseUser user;
    SharedPrefs prefs;
    boolean checkA = false;
    boolean checkB = false;

    private ArrayList<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Objects.requireNonNull(getSupportActionBar()).hide();
        Window window = getWindow();
        Drawable bg = AppCompatResources.getDrawable(this, R.drawable.main_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(bg);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        prefs = SharedPrefs.getInstance(this);
    }

    @Override
    protected void onResume() {
        DatabaseReference timeline= FirebaseDatabase.getInstance(getString(R.string.SERVER_ADDRESS)).getReference("timeline");
        DatabaseReference passcodeRef = FirebaseDatabase.getInstance(getString(R.string.SERVER_ADDRESS)).getReference("users/allow/passcode");
        timeline.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    Event e = dsp.getValue(Event.class);
                    events.add(e);
                    assert e != null;
                    Log.d("Here", ""+e.getName());
                }
                try {
                    prefs.setEvents(ObjectSerializer.serialize(events));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                checkA = true;
                if (checkB){
                    proceed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        passcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String passcode = snapshot.getValue(String.class);
                prefs.setPasscode(passcode);
                checkB = true;
                if(checkA)
                    proceed();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                if(hasWindowFocus())
                    proceed();
            }
        }.start();
        super.onResume();
    }

    private void proceed() {
        if (user != null && user.isEmailVerified()) {
            Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
            SplashScreen.this.startActivity(myIntent);
            SplashScreen.this.finish();
        } else if (user == null) {
            Intent myIntent = new Intent(SplashScreen.this, SignUpActivity.class);
            SplashScreen.this.startActivity(myIntent);
            finish();
        } else {
            startActivity(new Intent(SplashScreen.this, CheckVerifiedActivity.class));
            finish();
        }
    }


}
