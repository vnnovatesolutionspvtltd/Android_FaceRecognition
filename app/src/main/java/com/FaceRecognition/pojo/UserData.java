package com.FaceRecognition.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;

public class UserData {
    private String name;

    private String email;

    private String phone;

    private String date;



    private Bitmap crop;

    private String cropBase64;

    public UserData(String name, String email,String phone,String date,Bitmap crop) {
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.date=date;
        this.crop=crop;
    }

    public UserData() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCrop(Bitmap crop) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        crop.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        setCropBase64(encoded);
    }

    public Bitmap getCrop() {
        byte[] imageAsBytes = Base64.decode(cropBase64.getBytes(), Base64.DEFAULT);
        this.crop = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return this.crop;
    }

    public String getCropBase64() {
        return cropBase64;
    }

    public void setCropBase64(String cropBase64) {
        this.cropBase64 = cropBase64;
    }



}
