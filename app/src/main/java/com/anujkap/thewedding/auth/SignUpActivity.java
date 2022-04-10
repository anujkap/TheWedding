package com.anujkap.thewedding.auth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
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
import com.anujkap.thewedding.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasscodeField;
    private EditText mPhoneNumberField;
    private EditText mNameField;
    private Button sign_up;

    TextView sign_in_link;
    private String passcodeCheck;
    private String email;
    private String password;
    private String phno;
    private String name;

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
        setContentView(R.layout.activity_sign_up);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1101);
        }

        mEmailField = findViewById(R.id.sign_up_text);
        mPasswordField = findViewById(R.id.password_text);
        mPasscodeField = findViewById(R.id.passcode_text);
        mPhoneNumberField = findViewById(R.id.phone_text);
        mNameField = findViewById(R.id.name_text);
        sign_up = findViewById(R.id.signup_button);

        sign_in_link = findViewById(R.id.signin_link);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(getString(R.string.SERVER_ADDRESS)).getReference();

        SharedPrefs prefs = SharedPrefs.getInstance(this);
        showProgressDialog();
        passcodeCheck = prefs.getPasscode();

        sign_up.setOnClickListener(view -> {
            Log.d(TAG,"pressed");
                if(findViewById(R.id.signup_id).getVisibility() == View.VISIBLE){
                    if(validateForm()) {
                        email = mEmailField.getText().toString();
                        password = mPasswordField.getText().toString();
                        phno = mPhoneNumberField.getText().toString();
                        name = mNameField.getText().toString();
                        createAccount(email,password);
                    }
                }
                else{
                    Log.d(TAG,"here");
                    String passcode = mPasscodeField.getText().toString();
                    if(passcode.equals(passcodeCheck)){
                        findViewById(R.id.passcode_check).setVisibility(View.GONE);

                        findViewById(R.id.signup_id).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_id).setClickable(true);
                        mEmailField.setClickable(true);
                        findViewById(R.id.signup_password).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_password).setClickable(true);
                        mPasswordField.setClickable(true);

                        findViewById(R.id.signup_phone).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_phone).setClickable(true);
                        mPhoneNumberField.setClickable(true);
                        findViewById(R.id.signup_name).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_name).setClickable(true);
                        mNameField.setClickable(true);

                        sign_up.setText("Create Account");

                    }
                    else{
                        mPasscodeField.setError("Incorrect");
                    }
                }
        });

        sign_in_link.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this,LoginActivity.class)));
        SpannableString content = new SpannableString(getString(R.string.sign_in_link));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        sign_in_link.setText(content);

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        View view=this.getCurrentFocus();
        hideKeyboard(view);

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        sendEmailVerification();
                        FirebaseUser user= mAuth.getCurrentUser();
                        String userId = user.getUid();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("phoneNumber", phno);
                        map.put("name", name);
                        mDatabase.child("users").child(userId).setValue(map);
                        Intent intent = new Intent(SignUpActivity.this, CheckVerifiedActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        mPasswordField.setText(null);
                    }
                    hideProgressDialog();
                });
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(SignUpActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
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

        String name = mNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required.");
            valid = false;
        } else {
            mNameField.setError(null);
        }

        String phone = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mPhoneNumberField.setError("Required.");
            valid = false;
        } else {
            mPhoneNumberField.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}