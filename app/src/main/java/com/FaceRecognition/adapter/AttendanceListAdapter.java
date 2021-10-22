package com.FaceRecognition.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.FaceRecognition.R;
import com.FaceRecognition.pojo.RecognitionPojo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.ViewHolder> {

    private  Context context;
    private List<RecognitionPojo> recognitionPojoList;
    private static final String TAG = "Customlistadapter";
    String date;
    SharedPreferences sharedPreferences;

    public AttendanceListAdapter(Context context, List<RecognitionPojo> recognitionPojoList) {
        this.context = context;
        this.recognitionPojoList = recognitionPojoList;
        sharedPreferences=context.getSharedPreferences("USER",MODE_PRIVATE);
    }

   // @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.attendance_adapter,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecognitionPojo recognitionPojo=recognitionPojoList.get(position);
        holder.circleImageView.setImageBitmap(recognitionPojo.getCrop());
        holder.name.setText(recognitionPojo.getName());
        date=sharedPreferences.getString("date","");
        Log.d(TAG, "onBindViewHolder: "+recognitionPojo.getAttendance_date());
        Log.d(TAG, "onBindViewHolder: "+recognitionPojo.isCurrent_date());
        Log.d(TAG, "onBindViewHolder:date "+date);

        if(recognitionPojo.getAttendance_date().equals(date) && recognitionPojo.isCurrent_date()){
            holder.imageView1.setVisibility(View.VISIBLE);
            holder.imageView2.setVisibility(View.GONE);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#3BB54A"));

        }else {
            holder.imageView1.setVisibility(View.GONE);
            holder.imageView2.setVisibility(View.VISIBLE);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#800000"));
        }

    }

    @Override
    public int getItemCount() {
        return recognitionPojoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ImageView imageView1,imageView2;
        TextView name;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.userimg);
            imageView1=itemView.findViewById(R.id.img_true);
            imageView2=itemView.findViewById(R.id.img_false);
            name=itemView.findViewById(R.id.username);
            linearLayout=itemView.findViewById(R.id.linearlayout);
        }
    }
}
