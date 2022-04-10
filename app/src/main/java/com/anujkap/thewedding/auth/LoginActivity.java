package com.anujkap.thewedding.auth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anujkap.thewedding.Helpers.BaseActivity;
import com.anujkap.thewedding.Helpers.SharedPrefs;
import com.anujkap.thewedding.MainActivity;
import com.anujkap.thewedding.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;

    private EditText mEmailField;
    private EditText mPasswordField;

    SharedPrefs prefs;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        Window window = getWindow();
        Drawable bg = ContextCompat.getDrawable(this, R.drawable.main_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(bg);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_login);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1101);
        }

        mEmailField = findViewById(R.id.log_in_text);
        mPasswordField = findViewById(R.id.password_text);
        mAuth = FirebaseAuth.getInstance();

        usersReference = FirebaseDatabase.getInstance(getString(R.string.SERVER_ADDRESS)).getReference().child("users");

        prefs = SharedPrefs.getInstance(this);

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(v->{
            if(validateForm()){
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        TextView link = findViewById(R.id.signup_link);
        link.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(i);
        });
        SpannableString content = new SpannableString(getString(R.string.sign_up_link));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        link.setText(content);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        showProgressDialog();
        View view=this.getCurrentFocus();
        hideKeyboard(view);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        if(user.isEmailVerified()) {
                            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dsp: snapshot.getChildren()){
                                        Log.d("Here",dsp.getKey());
                                        if(dsp.getKey().equals(user.getUid())){
                                            Log.d("Here", "in if");
                                            String uname = dsp.child("name").getValue(String.class);
                                            String phoneNum = dsp.child("phoneNumber").getValue(String.class);
                                            prefs.setName(uname);
                                            prefs.setNumber(phoneNum);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else {
                            startActivity(new Intent(LoginActivity.this,CheckVerifiedActivity.class));
                            Toast.makeText(getApplicationContext(),"Verify Email and Sign In again",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        mPasswordField.setText(null);
                    }
                    hideProgressDialog();
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

}