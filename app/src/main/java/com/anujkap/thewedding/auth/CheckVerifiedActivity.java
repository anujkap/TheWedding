package com.anujkap.thewedding.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.anujkap.thewedding.Helpers.BaseActivity;
import com.anujkap.thewedding.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CheckVerifiedActivity extends BaseActivity {

    FirebaseUser user;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        Window window = getWindow();
        Drawable bg = getDrawable(R.drawable.main_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(bg);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_verified);
        mAuth = FirebaseAuth.getInstance();

        Button b = findViewById(R.id.check);
        b.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            mAuth.signOut();
            startActivity(new Intent(CheckVerifiedActivity.this,LoginActivity.class));
        });
        TextView resend = findViewById(R.id.resend);
        resend.setOnClickListener(v -> sendEmailVerification());
        SpannableString content = new SpannableString(getString(R.string.resend));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resend.setText(content);
    }
    private void sendEmailVerification() {

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckVerifiedActivity.this);
                        builder.setTitle("Success");
                        builder.setMessage("An email has been sent to your account");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Ok", (dialog, which) -> {
                        });


                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();
//                        Toast.makeText(getApplicationContext(),
//                                "Verification email sent to " + user.getEmail(),
//                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Verification:", "sendEmailVerification", task.getException());
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckVerifiedActivity.this);
                        builder.setTitle("Sorry");
                        builder.setMessage("Please try again later");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Ok", (dialog, which) -> {
                        });


                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();
                    }
                });
    }
}
