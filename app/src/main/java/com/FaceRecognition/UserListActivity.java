package com.FaceRecognition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.FaceRecognition.adapter.UserListAdapter;
import com.FaceRecognition.pojo.RecognitionPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "UserListActivity";
    RecyclerView rclUsers;
    ImageView ivMenu;
    TextView txtTitle;
    TextView txtNoData;
    ArrayList<RecognitionPojo> recognitionPojoList;
    SharedPreferences sharedPreferences;
    Activity activity;
    UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        activity = this;

        ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setImageResource(R.drawable.backicon);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Registered Users");

        rclUsers = findViewById(R.id.rclUsers);
        txtNoData = findViewById(R.id.txtNoData);

        recognitionPojoList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("Recognition", "[]");
        Log.d(TAG, "onCreate: " + json);
        try {
            recognitionPojoList.clear();
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

        Log.d(TAG, "onCreate: " + recognitionPojoList);
        userListAdapter = new UserListAdapter(activity, recognitionPojoList, new UserListAdapter.OnClickListener() {
            @Override
            public void OnEditListener(int position) {
                goToEditFace(position);
            }

            @Override
            public void OnRemoveListener(int position) {
                showRemoveDialog(position);
            }
        });
        rclUsers.setLayoutManager(new LinearLayoutManager(activity));
        rclUsers.setAdapter(userListAdapter);
        checkList();

    }

    private void goToEditFace(int position) {

        Intent intent = new Intent(activity, EditFaceActivity.class);
//        Gson gson = new Gson();
//        String faceJson = gson.toJson(recognitionPojoList.get(position), RecognitionPojo.class);
//        Log.d(TAG, "goToEditFace: " + faceJson);
        intent.putExtra("face", recognitionPojoList.get(position));
        startActivity(intent);
    }

    private void showRemoveDialog(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Remove Face");
        builder.setMessage("Are you sure want to remove this face?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recognitionPojoList.remove(position);
                userListAdapter.notifyDataSetChanged();
                checkList();
                updateUserList();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void checkList() {
        if (recognitionPojoList.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            rclUsers.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            rclUsers.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserList() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray();

        for (RecognitionPojo rp: recognitionPojoList) {
            try {
                String json = gson.toJson(rp, RecognitionPojo.class);
                JSONObject jsonObject = new JSONObject(json);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString("Recognition", jsonArray.toString());
        editor.apply();

    }

    @Override
    public void onBackPressed() {
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}