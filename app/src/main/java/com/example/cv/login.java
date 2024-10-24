package com.example.cv;

import android.annotation.SuppressLint;
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
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;

public class login extends AppCompatActivity {


    private EditText  txtEmail, LastPass;
    private Button Loginbtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView textViewLoginNow;

   private Button google_sign_in_btn;

    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;

    GoogleSignInOptions gso;

    GoogleSignInClient gsc;
    int RC_SIGN_IN = 20;



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

    @SuppressLint("WrongViewCast")
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

        google_sign_in_btn = findViewById(R.id.google_sign_in_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        gsc = GoogleSignIn.getClient(login.this,gso);

        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
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
    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }catch (Exception e){
                Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id",user.getUid());
                            map.put("name",user.getDisplayName());
                            map.put("profile",user.getPhotoUrl().toString());
                            database.getReference().child("userss").child(user.getUid()).setValue(map);

                            Intent intent = new Intent(login.this, HomePage.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(login.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
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