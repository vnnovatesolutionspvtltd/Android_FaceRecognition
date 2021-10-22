package com.FaceRecognition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCharacteristics;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.FaceRecognition.customview.OverlayView;
import com.FaceRecognition.env.BorderedText;
import com.FaceRecognition.env.ImageUtils;
import com.FaceRecognition.env.Logger;
import com.FaceRecognition.pojo.RecognitionPojo;
import com.FaceRecognition.tflite.SimilarityClassifier;
import com.FaceRecognition.tflite.TFLiteObjectDetectionAPIModel;
import com.FaceRecognition.tracking.MultiBoxTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class EditFaceActivity extends CameraActivity implements ImageReader.OnImageAvailableListener {

    private static final Logger LOGGER = new Logger();
    private static final String TAG = EditFaceActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView ivMenu;
    private TextView txtTitle;
    private Activity activity;

    // MobileFaceNet
    private static final int TF_OD_API_INPUT_SIZE = 112;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "mobile_face_net.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";

    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 720);
    //private static final int CROP_SIZE = 320;
    //private static final Size CROP_SIZE = new Size(320, 320);

    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    String label1 = "", prevLabel = "";
    boolean facedetect = false;
    SharedPreferences sharedPreferences;
    String current_date;
    private Integer sensorOrientation;
    private SimilarityClassifier detector;
    private long lastProcessingTimeMs;
    //private boolean adding = false;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private boolean computingDetection = false;
    //private Matrix cropToPortraitTransform;
    private boolean addPending = false;
    private boolean alreadyAdded = false;
    private long timestamp = 0;
    private int faceCount = 0;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private BorderedText borderedText;
    // Face detector
    private FaceDetector faceDetector;
    // here the preview image is drawn in portrait way
    private Bitmap portraitBmp = null;
    // here the face is cropped and drawn
    private Bitmap faceBmp = null;
    private FloatingActionButton fabAdd, fabUserList, fabswitchcamera;
    private ArrayList<RecognitionPojo> recognitionPojoList = new ArrayList<>();
    //FirebaseFirestore db;
    private TextView txtStatus;
    private AlertDialog addFaceDialog;
    private RecognitionPojo rp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_face);
        activity = this;

        Log.d(TAG, "onCreate: ");
        String faceJson = getIntent().getStringExtra("face");
        Log.d(TAG, "onCreate json: " + faceJson);
        Gson gson = new Gson();
        rp = gson.fromJson(faceJson, RecognitionPojo.class);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setImageResource(R.drawable.backicon);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Edit your Face");

//        fabswitchcamera = findViewById(R.id.login_switchcam);
        txtStatus = findViewById(R.id.txtStatus);

        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        current_date = new SimpleDateFormat("MMMM dd,yyyy", Locale.getDefault()).format(new Date());

        Log.d(TAG, "onCreate:date " + current_date);
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);

        faceDetector = detector;

    }


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(activity);


        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            //cropSize = TF_OD_API_INPUT_SIZE;

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
                        if (!rp.equals(pjo)) {
                            recognitionPojoList.add(pjo);
                            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(pjo.getId(), pjo.getTitle(), pjo.getDistance(), pjo.getLocation());
                            rec.setColor(pjo.getColor());
                            rec.setCrop(pjo.getCrop());
                            rec.setExtra(pjo.getExtra());
                            detector.register(pjo.getName(), rec);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);


        int targetW, targetH;
        if (sensorOrientation == 90 || sensorOrientation == 270) {
            targetH = previewWidth;
            targetW = previewHeight;
        } else {
            targetW = previewWidth;
            targetH = previewHeight;
        }
        int cropW = (int) (targetW / 2.0);
        int cropH = (int) (targetH / 2.0);

        croppedBitmap = Bitmap.createBitmap(cropW, cropH, Bitmap.Config.ARGB_8888);

        portraitBmp = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
        faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropW, cropH,
                        sensorOrientation, MAINTAIN_ASPECT);

//    frameToCropTransform =
//            ImageUtils.getTransformationMatrix(
//                    previewWidth, previewHeight,
//                    previewWidth, previewHeight,
//                    sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);


        Matrix frameToPortraitTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        targetW, targetH,
                        sensorOrientation, MAINTAIN_ASPECT);


        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }


    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;

        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        InputImage image = InputImage.fromBitmap(croppedBitmap, 0);
        faceDetector
                .process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {
                        Log.d(TAG, "onSuccess: " + faces);
                        if (faces.size() == 0) {
                            faceCount = 0;
                            if (addFaceDialog == null || !addFaceDialog.isShowing())
                                txtStatus.setText("Scan your face");
                            updateResults(currTimestamp, new LinkedList<>());
                            return;
                        }
                        faceCount++;
                        if (alreadyAdded) {
                            txtStatus.setText("It's already registered!");
                            faceCount = 0;
                        } else if (faceCount <= 5 && (addFaceDialog == null || !addFaceDialog.isShowing())) {
                            txtStatus.setText("Processing..." + faceCount);
                        }
                        runInBackground(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        onFacesDetected(currTimestamp, faces, addPending);
                                        if (faceCount > 5)
                                            addPending = false;
                                    }
                                });
                    }

                });


    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }


    // Face Processing
    private Matrix createTransform(
            final int srcWidth,
            final int srcHeight,
            final int dstWidth,
            final int dstHeight,
            final int applyRotation) {

        Matrix matrix = new Matrix();
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

            // Rotate around origin.
            matrix.postRotate(applyRotation);
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;

        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }

        return matrix;

    }

    private void showAddFaceDialog(SimilarityClassifier.Recognition rec) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText("Adding your face...");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
        ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
        TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
        EditText etName = dialogLayout.findViewById(R.id.dlg_input);
        tvTitle.setText("Add Face");
        ivFace.setImageBitmap(rec.getCrop());
        etName.setHint("Input name");
        Log.d(TAG, "AddFaceDialog " + rec.toString());
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int i) {

            }
        });
        builder.setNegativeButton("Re-Take", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int i) {

                faceCount = 0;
                addPending = true;
            }
        });
        builder.setView(dialogLayout);
        addFaceDialog = builder.create();
        addFaceDialog.show();

        addFaceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(activity, "Please enter your name.", Toast.LENGTH_SHORT).show();
                } else {
                    addFaceDialog.dismiss();
                    addFace(name, rec);
                }
            }
        });
    }

    private void addFace(String name, SimilarityClassifier.Recognition rec) {

        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String recStr = sharedPreferences.getString("Recognition", "[]");
            JSONArray jsonArray = new JSONArray();
            jsonArray = new JSONArray(recStr);

            Log.d(TAG, "onClick:recStr  " + recStr);
            RecognitionPojo rp = new RecognitionPojo();
            rp.setCrop(rec.getCrop());
            rp.setColor(rec.getColor());
            rp.setDistance(rec.getDistance());
            rp.setExtra((float[][]) rec.getExtra());
            rp.setId(rec.getId());
            rp.setLocation(rec.getLocation());
            rp.setTitle(rec.getTitle());
            rp.setName(name);

            recognitionPojoList.add(rp);

            String json = gson.toJson(rp, RecognitionPojo.class);

            JSONObject jsonObject = new JSONObject(json);
            jsonArray.put(jsonObject);

            //  Log.d(TAG, "onClick: getCrop: " + rec.getCrop());
            Log.d(TAG, "onClick:jsonObject " + rec.getDistance());
            Log.d(TAG, "onClick:jsonObject " + rec.getLocation());
            editor.putString("Recognition", jsonArray.toString());
            editor.apply();

            detector.register(name, rec);
//            Intent intent = new Intent();
//            intent.putExtra("img", rec.getCrop());
//            intent.putExtra("name", name);
            //  intent.putExtra("recognize", String.valueOf(jsonObject));
//            intent.putExtra("color", rec.getColor());
//            intent.putExtra("location", rec.getLocation());
//            intent.putExtra("extra", (float[][]) rec.getExtra());
//            intent.putExtra("id", rec.getId());
//            intent.putExtra("distance", rec.getDistance());
//                intent.putExtra("location",rec.getLocation());
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            setResult(Activity.RESULT_OK,intent);
            activity.onBackPressed();


//                    Log.d(TAG, "onClick:rec "+rec.getExtra().toString());
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
//                    byte[] b = baos.toByteArray();
//                    String encoded = Base64.encodeToString(b, Base64.DEFAULT);
//
//
//                    Map<String, Object> user = new HashMap<>();
//                    user.put("name",name);
//                    user.put("color",rec.getColor());
//                    user.put("distance",rec.getDistance());
//                  //  user.put("Extra",rec.getExtra());
//                    user.put("id",rec.getId());
//                    user.put("location",rec.getLocation());
//                    user.put("image",encoded);
//
//                    db.collection("Add Face")
//                            .add(user)
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Error adding document", e);
//                                }
//                            });
//
//
//
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateResults(long currTimestamp, final List<SimilarityClassifier.Recognition> mappedRecognitions) {
        Log.d(TAG, "SimilarityClassifier.Recognition2 " + mappedRecognitions);
        tracker.trackResults(mappedRecognitions, currTimestamp);
        trackingOverlay.postInvalidate();
        computingDetection = false;
        //adding = false;


        if (mappedRecognitions.size() > 0) {
            LOGGER.i("Adding results");
            SimilarityClassifier.Recognition rec = mappedRecognitions.get(0);
            Log.d(TAG, "SimilarityClassifier.Recognition3 " + rec);
            if (rec.getExtra() != null && faceCount > 5) {
                showAddFaceDialog(rec);
            }

        }

        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        showFrameInfo(previewWidth + "x" + previewHeight);
                        showCropInfo(croppedBitmap.getWidth() + "x" + croppedBitmap.getHeight());
                        showInference(lastProcessingTimeMs + "ms");
                    }
                });

    }

    private void onFacesDetected(long currTimestamp, List<Face> faces, boolean add) {

        try {
            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
            final Canvas canvas = new Canvas(cropCopyBitmap);
            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);

            float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
            switch (MODE) {
                case TF_OD_API:
                    minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                    break;
            }

            final List<SimilarityClassifier.Recognition> mappedRecognitions =
                    new LinkedList<SimilarityClassifier.Recognition>();

            Log.d(TAG, "SimilarityClassifier.Recognition4 " + mappedRecognitions);
            //final List<Classifier.Recognition> results = new ArrayList<>();

            // Note this can be done only once
            int sourceW = rgbFrameBitmap.getWidth();
            int sourceH = rgbFrameBitmap.getHeight();
            int targetW = portraitBmp.getWidth();
            int targetH = portraitBmp.getHeight();
            Matrix transform = createTransform(
                    sourceW,
                    sourceH,
                    targetW,
                    targetH,
                    sensorOrientation);
            final Canvas cv = new Canvas(portraitBmp);

            // draws the original image in portrait mode.
            cv.drawBitmap(rgbFrameBitmap, transform, null);

            final Canvas cvFace = new Canvas(faceBmp);

            boolean saved = false;

            for (Face face : faces) {

                LOGGER.i("FACE" + face.toString());
                LOGGER.i("Running detection on face " + currTimestamp);
                //results = detector.recognizeImage(croppedBitmap);

                final RectF boundingBox = new RectF(face.getBoundingBox());

                //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
                final boolean goodConfidence = true; //face.get;
                if (boundingBox != null && goodConfidence) {

                    // maps crop coordinates to original
                    cropToFrameTransform.mapRect(boundingBox);

                    // maps original coordinates to portrait coordinates
                    RectF faceBB = new RectF(boundingBox);
                    transform.mapRect(faceBB);

                    // translates portrait to origin and scales to fit input inference size
                    //cv.drawRect(faceBB, paint);
                    float sx = ((float) TF_OD_API_INPUT_SIZE) / faceBB.width();
                    float sy = ((float) TF_OD_API_INPUT_SIZE) / faceBB.height();
                    Matrix matrix = new Matrix();
                    matrix.postTranslate(-faceBB.left, -faceBB.top);
                    matrix.postScale(sx, sy);

                    cvFace.drawBitmap(portraitBmp, matrix, null);

                    //canvas.drawRect(faceBB, paint);

                    String label = "";
                    float confidence = -1f;
                    Integer color = Color.BLUE;
                    Object extra = null;
                    Bitmap crop = null;

                    try {
                        if (add) {
                            crop = Bitmap.createBitmap(portraitBmp,
                                    (int) faceBB.left,
                                    (int) faceBB.top,
                                    (int) faceBB.width(),
                                    (int) faceBB.height());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final long startTime = SystemClock.uptimeMillis();

                    final List<SimilarityClassifier.Recognition> resultsAux = detector.recognizeImage(faceBmp, add);
                    Log.d(TAG, "SimilarityClassifier.Recognition5 " + resultsAux);
                    Log.d(TAG, "onFacesDetected: " + faceBmp.toString());
                    lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                    if (resultsAux.size() > 0) {

                        SimilarityClassifier.Recognition result = resultsAux.get(0);
                        Log.d(TAG, "SimilarityClassifier.Recognition6 " + result);
                        extra = result.getExtra();
//          Object extra = result.getExtra();
//          if (extra != null) {
//            LOGGER.i("embeeding retrieved " + extra.toString());
//          }

                        float conf = result.getDistance();
                        Log.d(TAG, "Confidence " + conf);
                        if (conf < 1.0f) {

                            confidence = conf;
                            label = result.getTitle();
                            if (result.getId().equals("0")) {
                                color = Color.GREEN;

                                alreadyAdded = true;

                            } else {
                                color = Color.RED;
                            }
                        } else {
                            alreadyAdded = false;
                        }


                    }

                    if (getCameraFacing() == CameraCharacteristics.LENS_FACING_FRONT) {

                        // camera is frontal so the image is flipped horizontally
                        // flips horizontally
                        Matrix flip = new Matrix();
                        if (sensorOrientation == 90 || sensorOrientation == 270) {
                            flip.postScale(1, -1, previewWidth / 2.0f, previewHeight / 2.0f);
                        } else {
                            flip.postScale(-1, 1, previewWidth / 2.0f, previewHeight / 2.0f);
                        }
                        //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                        flip.mapRect(boundingBox);

                    }

                    final SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                            "0", label, confidence, boundingBox);
                    Log.d(TAG, "SimilarityClassifier.Recognition7 " + result);
                    result.setColor(color);
                    result.setLocation(boundingBox);
                    result.setExtra(extra);
                    result.setCrop(crop);
                    mappedRecognitions.add(result);
                    Log.d(TAG, "onFacesDetected:mappedRecognitions  " + mappedRecognitions);
                }


            }

            //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

            updateResults(currTimestamp, mappedRecognitions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogLayout = inflater.inflate(R.layout.userlistdialog, null);
//
//        ListView listView=dialogLayout.findViewById(R.id.user_list);
//        CustomListAdapter customListAdapter =new CustomListAdapter(activity, recognitionPojoList);
//        listView.setAdapter(customListAdapter);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dlg, int i) {
//
//
//                dlg.dismiss();
//            }
//        });
//        builder.setView(dialogLayout);
//        builder.show();

    }


}