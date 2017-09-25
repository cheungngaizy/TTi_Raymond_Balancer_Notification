package com.example.ptencheu.balancer;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener , View.OnTouchListener{

    //clockwise is positive, anti-clockwise is negative.
    float raw_yaw = 0;
    //tilt front is negative, tilt back is positive
    float raw_pitch = 0;
    //tile left is positive, tilt right is negative
    float raw_roll = 0;

    TextView mTextView_Debug;
    ImageView mImageViewBG;
    Button mButtonRESET;
    Canvas mCanvas;
    Handler mHandler_drawing;
    ImageView rotation_circle;
    ImageView fixed_circle;

    ImageView Ryobi_0;
    ImageView Ryobi_90;
    ImageView Ryobi_m90;
    ImageView Ryobi_180;



    private SensorManager sensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FindViews();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SetSensor();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //解除感應器註冊
        sensorManager.unregisterListener(this);
    }

    private void FindViews()
    {
        mTextView_Debug = (TextView) findViewById(R.id.mTextView_debug);
        mTextView_Debug.setTextColor(Color.WHITE);

        mImageViewBG = (ImageView)findViewById(R.id.imageView);

        mButtonRESET = (Button)findViewById(R.id.mButton_RESET);
        mButtonRESET.setOnClickListener(this);
        mButtonRESET.setOnTouchListener(this);

        rotation_circle = (ImageView)findViewById(R.id.rotation_circle);
        rotation_circle.setScaleType(ImageView.ScaleType.CENTER);

        fixed_circle = (ImageView)findViewById(R.id.base_circle);

        View mView_Drawable =  new mDrawableLib(this);

        Bitmap bitmap = Bitmap.createBitmap(2392,1440, Bitmap.Config.ARGB_8888);


        mImageViewBG.setImageBitmap(bitmap);

        mCanvas = new Canvas(bitmap);
        color_init();
        mView_Drawable.draw(mCanvas);

        mHandler_drawing = new Handler();
        mHandler_drawing.post(runnable);

        Ryobi_0 = (ImageView)findViewById(R.id.imageView_ryobi_up);
        Ryobi_0.setVisibility(View.INVISIBLE);
        Ryobi_90 = (ImageView)findViewById(R.id.imageView_Ryobi_90);
        Ryobi_90.setVisibility(View.INVISIBLE);
        Ryobi_m90 = (ImageView)findViewById(R.id.imageView_Ryobi_m90);
        Ryobi_m90.setVisibility(View.INVISIBLE);
        Ryobi_180 = (ImageView)findViewById(R.id.imageView_Ryobi_180);
        Ryobi_180.setVisibility(View.INVISIBLE);

    }

    protected void SetSensor()
    {
        List sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size()>0)
        {
            sensorManager.registerListener(this, (Sensor) sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    boolean Logo_Set_Once = false;
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        float[] values = event.values;
        raw_yaw = values [0];
        raw_pitch = values [2];
        raw_roll = values[1];
        mRoll = raw_roll - mStartRoll;
        mPitch = raw_pitch - mStartPitch;
        mYaw = raw_yaw - mStartYaw;

        //mTextView_Debug.setText("yaw：" + mYaw + "\n" + "roll:" +mRoll + "\n" + "pitch:"+mPitch);

        if(Logo_Set_Once == false){
            if(raw_roll < 0 && Math.abs(raw_pitch)<20){
                Ryobi_0.setVisibility(View.VISIBLE);
                Ryobi_90.setVisibility(View.INVISIBLE);
                Ryobi_m90.setVisibility(View.INVISIBLE);
                Ryobi_180.setVisibility(View.INVISIBLE);
            }
            else if (raw_roll> 0 && Math.abs(raw_pitch)<20){
                Ryobi_180.setVisibility(View.VISIBLE);
                Ryobi_90.setVisibility(View.INVISIBLE);
                Ryobi_m90.setVisibility(View.INVISIBLE);
                Ryobi_0.setVisibility(View.INVISIBLE);
            }
            else if (raw_pitch> 0 && Math.abs(raw_roll)<20){
                Ryobi_180.setVisibility(View.INVISIBLE);
                Ryobi_90.setVisibility(View.INVISIBLE);
                Ryobi_m90.setVisibility(View.VISIBLE);
                Ryobi_0.setVisibility(View.INVISIBLE);
            }
            else if (raw_pitch< 0 && Math.abs(raw_roll)<20){
                Ryobi_180.setVisibility(View.INVISIBLE);
                Ryobi_90.setVisibility(View.VISIBLE);
                Ryobi_m90.setVisibility(View.INVISIBLE);
                Ryobi_0.setVisibility(View.INVISIBLE);
            }

            Logo_Set_Once = true;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    float mYaw = 0;
    float mRoll = 0;
    float mPitch = 0;

    float mStartYaw = 0;
    float mStartRoll = 0;
    float mStartPitch = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mButton_RESET:{

                //Toast.makeText(this,"Pressed",Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }

    Paint mPaint_Line_indicator;
    void color_init(){

        mPaint_Line_indicator = new Paint();
        mPaint_Line_indicator.setColor(getResources().getColor(R.color.mPaint_Line_indicator_color));
        mPaint_Line_indicator.setStyle(Paint.Style.STROKE);
        mPaint_Line_indicator.setStrokeWidth(15);

    }

    public void imageTransformation(float this_pitch, float this_roll){

//        float rotation_angle = 10;
//        float scale = 0.0f;
//
//        double get_angle = Math.atan2(this_roll,-this_pitch);
//
//        rotation_angle = -(float) Math.toDegrees((get_angle));
//
//        scale = (float) Math.hypot(this_roll,this_pitch)/20 + 0.4f;
//
//        if(scale > 0.9){scale = 0.9f;}
//
//        rotation_circle.setScaleX(scale);
//        rotation_circle.setScaleY(scale);
//        rotation_circle.setRotation(rotation_angle);


        if(Math.abs(this_pitch) > 5 || Math.abs(this_roll) > 5){

            rotation_circle.setImageResource(R.drawable.not_in_center);

            float scale = 1;

            if(Math.hypot(this_roll,this_pitch) > 25){
                scale = 25/(float)Math.hypot(this_roll,this_pitch);
            }

            if(this_roll > 0) {
                rotation_circle.setX((getWindowManager().getDefaultDisplay().getWidth() / 2 - rotation_circle.getWidth() / 2 - (this_roll*scale) * 10));
            }
            else rotation_circle.setX((getWindowManager().getDefaultDisplay().getWidth() / 2 - rotation_circle.getWidth() / 2 - (this_roll*scale) * 10));

            if(this_pitch > 0) {
                rotation_circle.setY((getWindowManager().getDefaultDisplay().getHeight() / 2 - rotation_circle.getHeight() / 2 + (this_pitch*scale) * 10));
            }
            else rotation_circle.setY((getWindowManager().getDefaultDisplay().getHeight() / 2 - rotation_circle.getHeight() / 2 + (this_pitch*scale) * 10));

        }
        else {

            rotation_circle.setImageResource(R.drawable.in_circle);
            rotation_circle.setX(getWindowManager().getDefaultDisplay().getWidth()/2 - rotation_circle.getWidth()/2 - this_roll*10);
            rotation_circle.setY(getWindowManager().getDefaultDisplay().getHeight()/2 - rotation_circle.getHeight()/2 + this_pitch*10);

        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //drawLine_Indicator(mPitch,mRoll);
            imageTransformation(mPitch,mRoll);
            mHandler_drawing.postDelayed(this, 50);
        }
    };

//
//    //clockwise is positive, anti-clockwise is negative.
//    float raw_yaw = 0;


//    //tilt front is negative, tilt back is positive
//    float raw_pitch = 0;
//    //tile left is positive, tilt right is negative
//    float raw_roll = 0;

    private void drawLine_Indicator(float this_pitch, float this_roll){

        float end_X_point = 0;
        float end_Y_point = 0;
        float X_origin = 2392/2;
        float Y_origin = 1440/2;

        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        this_pitch = this_pitch;
        this_roll = -this_roll;

        //
        if(this_pitch >= 4 && this_pitch <8){//left upper corner

        }
        else if(this_pitch >= 8 && this_pitch < 12){

        }
        else if(this_pitch >= 12 && this_pitch < 16){

        }

        //drawing line for indication only
        end_X_point = X_origin + this_roll*50;
        end_Y_point = Y_origin + this_pitch*50;


        mCanvas.drawLine(X_origin,Y_origin,end_X_point,end_Y_point,mPaint_Line_indicator);

    }


    long start_time = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:{
                start_time = System.currentTimeMillis();

                rotation_circle.setImageResource(R.drawable.not_in_center);


                mStartYaw = raw_yaw;
                mStartRoll = raw_roll;
                mStartPitch = raw_pitch;

                Logo_Set_Once = false;
                break;
            }

            case MotionEvent.ACTION_UP:{

                long time_diff = System.currentTimeMillis() - start_time;
                if(time_diff >  2000){
                    mStartRoll = 0;
                    mStartPitch = 0;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE:{

                long time_diff = System.currentTimeMillis() - start_time;
                if(time_diff >  2000){
                    mStartRoll = 0;
                    mStartPitch = 0;
                }

                break;
            }

        }


        return false;
    }
}
