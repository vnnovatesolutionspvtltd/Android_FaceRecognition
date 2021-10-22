package com.FaceRecognition.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;

//import com.google.firebase.firestore.DocumentReference;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.FaceRecognition.tflite.SimilarityClassifier;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RecognitionPojo implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

//    public DocumentReference getCreator() {
//        return creator;
//    }
//
//    public void setCreator(DocumentReference creator) {
//        this.creator = creator;
//    }
//
//    private DocumentReference creator;



//    public RecognitionPojo(String name, String email,String phone,String date) {
//        this.name = name;
//        this.email = email;
//        this.phone=phone;
//        this.date=date;
//       // this.crop=crop;
//    }



    @SerializedName("name")
    @Expose
    private String name;


    public RecognitionPojo(){

    }

    public RecognitionPojo(String name,boolean current_date, String attendance_date,Bitmap crop) {
        this.current_date = current_date;
        this.attendance_date = attendance_date;
        this.name=name;
        this.crop=crop;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SerializedName("email")
    @Expose
    private String email;

    public boolean isCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(boolean current_date) {
        this.current_date = current_date;
    }

    @SerializedName("current_date")
    @Expose
    private boolean current_date;


    public String getAttendance_date() {
        return attendance_date;
    }

    public void setAttendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    private String attendance_date="";
    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    @SerializedName("attendant")
    @Expose
    private String attendant;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("password")
    @Expose
    private String password;


    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("distance")
    @Expose
    private Float distance;

    @SerializedName("extra")
    @Expose
    private float[][] extra;

    @SerializedName("location")
    @Expose
    private RectF location;

    @SerializedName("color")
    @Expose
    private Integer color;

    @SerializedName("crop")
    @Expose
    private Bitmap crop;

    @SerializedName("cropBase64")
    @Expose
    private String cropBase64;



    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public void setExtra(float[][] extra) {
        this.extra = extra;
    }

    public Object getExtra() {
        Log.d("TAG", "getExtra: "+extra);
        return this.extra;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getDistance() {
        return distance;
    }

    public RectF getLocation() {
        return new RectF(location);
    }

    public void setLocation(RectF location) {
        this.location = location;
    }

    public Integer getColor() {
        return this.color;
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
