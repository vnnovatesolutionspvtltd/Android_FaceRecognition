package com.FaceRecognition.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FaceRecognition.R;
import com.FaceRecognition.pojo.RecognitionPojo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    public interface OnClickListener {
        void OnEditListener(int position);
        void OnRemoveListener(int position);
    }

    private Context context;
    private OnClickListener listener;
    private List<RecognitionPojo> recognitionPojoList;
    private static final String TAG = UserListAdapter.class.getSimpleName();

    public UserListAdapter(Context context, List<RecognitionPojo> recognitionPojoList, OnClickListener listener) {
        this.context = context;
        this.recognitionPojoList = recognitionPojoList;
        this.listener = listener;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.user_adapter, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecognitionPojo recognitionPojo = recognitionPojoList.get(position);

        holder.civUserImage.setImageBitmap(recognitionPojo.getCrop());
        holder.txtUsername.setText(recognitionPojo.getName());
    }

    @Override
    public int getItemCount() {
        return recognitionPojoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civUserImage;
        ImageButton ivEdit, ivRemove;
        TextView txtUsername;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civUserImage = itemView.findViewById(R.id.civUserImage);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            txtUsername = itemView.findViewById(R.id.txtUsername);

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnEditListener(getAdapterPosition());
                }
            });

            ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnRemoveListener(getAdapterPosition());
                }
            });
        }
    }
}