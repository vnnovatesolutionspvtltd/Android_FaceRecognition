package com.FaceRecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.FaceRecognition.adapter.AttendanceListAdapter;
import com.FaceRecognition.pojo.RecognitionPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AttandanceListActivity extends AppCompatActivity {

    private static final String TAG = "Attendanceactivity";
    RecyclerView listView;
    private ArrayList<RecognitionPojo> recognitionPojoList;
    SharedPreferences sharedPreferences;
    static int mYear, mMonth, mDay;
    static TextView date;
    public String currentdate;
    Context context;
    AttendanceListAdapter attendanceListAdapter;
   // FirebaseFirestore db;
    RecognitionPojo recognitionPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandance_list);
        context = this;
        listView = findViewById(R.id.attandance_userlist);
        date = findViewById(R.id.date);

       // db = FirebaseFirestore.getInstance();
        recognitionPojoList = new ArrayList<>();
       recognitionPojo=new RecognitionPojo();
        currentdate = new SimpleDateFormat("MMMM dd,yyyy", Locale.getDefault()).format(new Date());
        Log.d(TAG, "onCreate: " + currentdate);
        date.setText(currentdate + "");

//            db.collection("Register_Users")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                   // Log.d(TAG, "hello" +document.getId() + " => " + document.getData());
//
//                                    Map<String, Object> data = new HashMap<>();
//                                    data.put("registerdata",document.getData());
//                                    recognitionPojo=new RecognitionPojo();

//                                    Iterator myVeryOwnIterator = data.keySet().iterator();
//                                    while(myVeryOwnIterator.hasNext()) {
//                                        String key=(String)myVeryOwnIterator.next();
//                                        String value=(String)data.get(key);
//                                       // Toast.makeText(ctx, "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();
//                                        Log.d(TAG, "onComplete: "+"Key: "+key+" Value: "+value);
//                                    }

//                                    for (Map.Entry<String, Object> entry : data.entrySet()) {
//                                        Log.d(TAG, "onComplete: "+data);
                                       // Log.d(TAG, "onComplete: "+entry.getKey() + ":" + entry.getValue().toString());

                                        //recognitionPojo.setName(data.get("name").toString());


//                                        for(int i=0;i<entry.getValue().toString().length();i++){
//                                            Log.d(TAG, "onComplete: "+"Hello");
//                                        }
                                       // byte[] decodedString = Base64.decode(data.get("image").toString(), Base64.DEFAULT);
                                       //  Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                                   //     Log.d(TAG, "onComplete: "+name);
                                     //   Log.d(TAG, "onComplete: "+decodedByte);
                                      //  recognitionPojo.setName(name);
                                      //  recognitionPojo.setCrop(decodedByte);

                                //    }
                                 //   recognitionPojoList.add(recognitionPojo);

                                  //  Log.d(TAG, "onComplete: "+document.getData().get("email"));
                                  //  Log.d(TAG, "onComplete: "+data);
//                                    byte[] decodedString = Base64.decode(document.getData().get("image").toString(), Base64.DEFAULT);
//                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                   // Boolean date= Boolean.parseBoolean(data.get("").toString());
//                                    Bitmap crop=decodedByte;
//                                    String attendance_date=document.getData().get("image").toString();
//                                    String name=data.toString();
//                                    Log.d(TAG, "onComplete: "+name);
                                  //  recognitionPojo=new RecognitionPojo();
                                //   recognitionPojo.setName(name);
//                                   Log.d(TAG, "onComplete:image "+decodedByte);
//                                    Log.d(TAG, "onComplete: " +date);
//                                     Log.d(TAG, "onComplete:name " +name);



//                                }
//                            } else {
//                                Log.w(TAG, "Error getting documents.", task.getException());
//                            }
//                        }
//                    });




        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Attendance", "[]");
        try {
            //recognitionPojoList.clear();
            // JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    RecognitionPojo pjo = gson.fromJson(jsonObject1.toString(), RecognitionPojo.class);

                    recognitionPojoList.add(pjo);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: "+recognitionPojoList);
        attendanceListAdapter = new AttendanceListAdapter(this, recognitionPojoList);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(attendanceListAdapter);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateDialog();
            }
        });

    }


    public void showDateDialog() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(AttandanceListActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        try {
                            String date1 = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
                            Date newDate = spf.parse(date1);
                            spf = new SimpleDateFormat("MMMM dd,yyyy");
                            date1 = spf.format(newDate);
                            date.setText(date1);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("date", date.getText().toString());
                            attendanceListAdapter.notifyDataSetChanged();
                            editor.commit();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

   
}