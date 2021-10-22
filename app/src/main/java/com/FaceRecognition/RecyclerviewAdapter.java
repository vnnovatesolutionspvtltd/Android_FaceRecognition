package com.FaceRecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FaceRecognition.pojo.RecognitionPojo;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    @NonNull
    List<RecognitionPojo> recognitionPojoList=new ArrayList<RecognitionPojo>();
    private Context context;

    public RecyclerviewAdapter(@NonNull List<RecognitionPojo> recognitionPojoList, Context context) {
        this.recognitionPojoList = recognitionPojoList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.recyclelist,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecognitionPojo recognitionPojo=recognitionPojoList.get(position);

    }

    @Override
    public int getItemCount() {
        return recognitionPojoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       public ImageView imageView1,imageView2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1=itemView.findViewById(R.id.img_true);
            imageView2=itemView.findViewById(R.id.img_false);
        }
    }
}
