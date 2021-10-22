package com.FaceRecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FaceRecognitionScreenActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition_screen);
        textView=findViewById(R.id.text);
        button=findViewById(R.id.btn_next);
        String text = "<font color=#ffffff>FACI</font><font color=#00FDFF>O</font>";
        textView.setText(Html.fromHtml(text));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FaceRecognitionScreenActivity.this,DetectorActivity.class);
                startActivityForResult(intent,2);
            }
        });

    }
}