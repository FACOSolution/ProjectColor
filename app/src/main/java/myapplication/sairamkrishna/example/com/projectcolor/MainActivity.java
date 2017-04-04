package myapplication.sairamkrishna.example.com.projectcolor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity /*implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 */{


    public static final String TAG = "MainActivity";

    //Flag to verify a photo capture
    private boolean capture = false;

    //TextView to show the coordinates of touchscreen and his color
    TextView touchedXY,invertedXY,imgSize,colorRGB;

    //ImageView with a image to verify
    ImageView imgSource2;


    //Loading Opencv
    static{

        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV NOT LOAD");
        }else{
            Log.d(TAG, "Opencv Loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        //imageview = (ImageView)findViewById(R.id.imageview);


        //Listening the capture button
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });



        //Initializing the variables from screen

        touchedXY = (TextView)findViewById(R.id.xy);
        invertedXY = (TextView)findViewById(R.id.invertedxy);
        imgSize = (TextView)findViewById(R.id.size);
        colorRGB = (TextView)findViewById(R.id.colorrgb);
        imgSource2 = (ImageView)findViewById(R.id.source2);



        imgSource2.setOnTouchListener(imgSourceOnTouchListener);


/*

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color = (TextView) findViewById(R.id.touch_color);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        */

    }

/*
    public Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
    */

    //TAKE CAMERA ALGORITHM
    @Override
    //Where a picture has captured this method set the image to bitmap and update the flag "capture"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        capture = true;
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
    //    Bitmap bitmap2 = BITMAP_RESIZER(bitmap,0,0);
        imgSource2.setImageBitmap(bitmap);
    }



    //While the picture was touched this method update the pixel color
        View.OnTouchListener imgSourceOnTouchListener
                = new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (capture == true) {

                    float eventX = event.getX();
                    float eventY = event.getY();
                    float[] eventXY = new float[]{eventX, eventY};

                    Matrix invertMatrix = new Matrix();
                    ((ImageView) view).getImageMatrix().invert(invertMatrix);

                    invertMatrix.mapPoints(eventXY);
                    int x = Integer.valueOf((int) eventXY[0]);
                    int y = Integer.valueOf((int) eventXY[1]);

                    touchedXY.setText(
                            "touched position: "
                                    + String.valueOf(eventX) + " / "
                                    + String.valueOf(eventY));
                    invertedXY.setText(
                            "touched position: "
                                    + String.valueOf(x) + " / "
                                    + String.valueOf(y));

                    Drawable imgDrawable = ((ImageView) view).getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

                    imgSize.setText(
                            "drawable size: "
                                    + String.valueOf(bitmap.getWidth()) + " / "
                                    + String.valueOf(bitmap.getHeight()));

                    //Limit x, y range within bitmap
                    if (x < 0) {
                        x = 0;
                    } else if (x > bitmap.getWidth() - 1) {
                        x = bitmap.getWidth() - 1;
                    }

                    if (y < 0) {
                        y = 0;
                    } else if (y > bitmap.getHeight() - 1) {
                        y = bitmap.getHeight() - 1;
                    }

                    int touchedRGB = bitmap.getPixel(x, y);

                    colorRGB.setText("touched color: " + "#" + Integer.toHexString(touchedRGB));
                    colorRGB.setTextColor(touchedRGB);

                    return true;

                }
                return true;
            }


        };


}
