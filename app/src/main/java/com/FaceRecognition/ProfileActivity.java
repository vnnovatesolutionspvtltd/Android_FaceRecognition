package com.FaceRecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    CircleImageView imageView;
    TextView name,email,phone,dateofbirth;
    Button okay;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView=findViewById(R.id.profile_images);
        name=findViewById(R.id.profile_name);
        email=findViewById(R.id.profile_email);
        phone=findViewById(R.id.profile_phone);
        dateofbirth=findViewById(R.id.profile_dateofbirth);
        okay=findViewById(R.id.profile_okay);
        Bundle bundle=new Bundle();
         bundle=getIntent().getExtras();
        if(bundle!=null){
            Log.d(TAG, "onCreate: "+bundle);
            String name1=bundle.getString("name");
            bitmap =bundle.getParcelable("photo");
            name.setText(name1);
            imageView.setImageBitmap(bitmap);


        }

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}