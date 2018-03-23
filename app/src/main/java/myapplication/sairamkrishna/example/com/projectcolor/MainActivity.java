package myapplication.sairamkrishna.example.com.projectcolor;

import android.content.Context;
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
import android.view.SurfaceHolder;
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
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import static android.R.id.input;

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
       // setContentView(new DrawingView(this));

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



    }


    class DrawingView extends SurfaceView {

        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public DrawingView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.BLACK);
                    canvas.drawCircle(event.getX(), event.getY(), 50, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            return false;
        }
    }


    //TAKE CAMERA ALGORITHM
    @Override
    //Where a picture has captured this method set the image to bitmap and update the flag "capture"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        capture = true;
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
    //    Bitmap bitmap2 = BITMAP_RESIZER(bitmap,0,0);


        Mat tmp = new Mat (bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, tmp);
       // Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_BGR2Lab);


        Utils.matToBitmap(tmp,bitmap);

      //  imgSource2.setImageBitmap(bitmap);


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

                    int color = bitmap.getPixel(x, y);

                    int A = (color >> 24) & 0xff; // or color >>> 24
                    int R = (color >> 16) & 0xff;
                    int G = (color >>  8) & 0xff;
                    int B = (color      ) & 0xff;

                    String str = "A = " + String.valueOf(A) + ", R = " + String.valueOf(R) + ", G = " + String.valueOf(G) + ", B = " + String.valueOf(B);

                    Log.d("COR:", str);

                    colorRGB.setText("touched color: " + "#" + Integer.toHexString(color));
                    colorRGB.setTextColor(color);

                    return true;

                }
                return true;
            }


        };


}
