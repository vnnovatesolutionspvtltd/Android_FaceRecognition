package com.FaceRecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.FaceRecognition.pojo.RecognitionPojo;
import com.FaceRecognition.pojo.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
  //  TextView textView;
    CircleImageView circleImageView;
    Button addface, register;
    EditText name, email, phone;
    TextView gender, date;
    int mYear, mMonth, mDay;
    SharedPreferences sharedPreferences;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String MobilePattern = "[0-9]{10}";
    private ArrayList<UserData> userDataArrayList = new ArrayList<>();
    public ArrayList<RecognitionPojo> recognitionPojoArrayList = new ArrayList<>();
    Bitmap bitmap;
   // FirebaseFirestore db;
    String id;
    Float distance;
    Object extra;
    RectF location;
    Integer color;
    String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
      //  textView = findViewById(R.id.txt_login);
        name = findViewById(R.id.user_name);
//        email = findViewById(R.id.user_email);
//        phone = findViewById(R.id.user_phone);
        circleImageView = findViewById(R.id.profile_image);
        addface = findViewById(R.id.btn_faceid);
        register = findViewById(R.id.btn_register);
       // date = findViewById(R.id.user_birthdate);
//        String text = "<font color=#000080>Already have an account?</font> <font color=#0000FF>Login</font>";
//        textView.setText(Html.fromHtml(text));
        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
       // db = FirebaseFirestore.getInstance();


        addface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, DetectorActivity.class);
                startActivityForResult(intent, 2);
            }
        });

//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                mDay = c.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.show();
//            }
//
//        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        //  Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                bitmap = data.getParcelableExtra("img");
                  id=data.getStringExtra("id");
               distance= data.getFloatExtra("distance",0);
               location=data.getParcelableExtra("location");
              color= data.getIntExtra("color",0);
               extra=data.getSerializableExtra("extra");
                Log.d(TAG, "onActivityResult: "+id);
                Log.d(TAG, "onActivityResult: " + distance);
                Log.d(TAG, "onActivityResult: "+color);
                Log.d(TAG, "onActivityResult: " + extra);
                circleImageView.setImageBitmap(data.getParcelableExtra("img"));
                name.setText(data.getStringExtra("name"));
            }

        }
    }

    public void validation() {
        Log.d(TAG, "validation:" + bitmap);

         if(bitmap==null){
            Toast.makeText(this, "Please Add Your Face", Toast.LENGTH_SHORT).show();
        }
         else if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
//        else if (TextUtils.isEmpty(email.getText().toString())) {
//            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
//        } else if (!TextUtils.isEmpty(email.getText().toString()) && !email.getText().toString().matches(emailPattern)) {
//            Toast.makeText(this, "Please Enter valid Email Address", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(phone.getText().toString())) {
//            Toast.makeText(this, "Please Enter Phone", Toast.LENGTH_SHORT).show();
//        } else if (!TextUtils.isEmpty(phone.getText().toString()) && !phone.getText().toString().matches(MobilePattern)) {
//            Toast.makeText(this, "Please Enter Valid Phone", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(date.getText().toString())) {
//            Toast.makeText(this, "Please Enter Birth Date", Toast.LENGTH_SHORT).show();
//        }
        else {

            //Add data in firebase


//            try {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
//                byte[] b = baos.toByteArray();
//                String encoded = Base64.encodeToString(b, Base64.DEFAULT);
//
//
//                Map<String, Object> user = new HashMap<>();
//                user.put("name", name.getText().toString());
//                user.put("email",email.getText().toString() );
//                user.put("date",date.getText().toString());
//                user.put("phone",phone.getText().toString());
//                user.put("id",id);
//                user.put("color",color);
//                user.put("distance",distance);
//                user.put("image",encoded);
//                user.put("location",location);
//                //user.put("extra", extra);
//                db.collection("Register_Users")
//                        .add(user)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                Toast.makeText(RegisterActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "Error adding document", e);
//                            }
//                        });
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(TAG, "validation:e " + e);
//            }


            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String recStr = sharedPreferences.getString("UserData", "[]");

                Log.d(TAG, "validation: "+recStr);
                JSONArray jsonArray = new JSONArray();
                jsonArray = new JSONArray(recStr);


                RecognitionPojo recognitionPojo=new RecognitionPojo();
                recognitionPojo.setCrop(bitmap);
                recognitionPojo.setName(name.getText().toString());
               // recognitionPojo.setEmail(email.getText().toString());
               // recognitionPojo.setPhone(phone.getText().toString());
               // recognitionPojo.setDate(date.getText().toString());
                recognitionPojoArrayList.add(recognitionPojo);
                String json = gson.toJson(recognitionPojo, RecognitionPojo.class);
                JSONObject jsonObject = new JSONObject(json);
                jsonArray.put(jsonObject);
                editor.putString("UserData", jsonArray.toString());
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(RegisterActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                finish();
        }
    }


}