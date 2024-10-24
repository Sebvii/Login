package com.example.cv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {


    private EditText  txtEmail, LastPass;
    private Button Loginbtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView textViewLoginNow;

    @Override
    public void onStart() {
        super.onStart();
        // Initialize Firebase Auth instance
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find Views
        txtEmail = findViewById(R.id.txtEmail);
        LastPass = findViewById(R.id.LastPass);;
        Loginbtn = findViewById(R.id.Loginbtn);
        progressBar = findViewById(R.id.progressBar); // Initialize progressBar
        textViewLoginNow = findViewById(R.id.loginNow);

        // Set Sign-Up button click listener
        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        // Redirect to login page if user clicks "Login Now"
        textViewLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), verify_email.class);
                startActivity(intent);
                finish();
            }
        });

        // Find VideoView by its ID
        VideoView videoView = findViewById(R.id.videoViewBackground);

        // Set the video path (video file in res/raw/main_background.mp4)
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg1); // resource name should be lowercase
        videoView.setVideoURI(uri);

        // Start the video and loop it
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);  // Set video looping
            videoView.start();    // Start the video
        });
    }

    private void signUpUser() {
        // Get user input
        String email = txtEmail.getText().toString().trim();
        String password = LastPass.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required.");
            txtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Please provide a valid email.");
            txtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            LastPass.setError("Password is required.");
            LastPass.requestFocus();
            return;
        }
        // Show progressBar while signing up
        progressBar.setVisibility(View.VISIBLE);


    }
}