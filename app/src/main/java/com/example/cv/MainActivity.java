package com.example.cv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private Button getstartbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // next page
        getstartbtn = findViewById(R.id.getstartbtn);
        getstartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Secondpage.class);
                startActivity(intent);
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
}