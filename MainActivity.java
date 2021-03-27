package com.example.teampt2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PHYISCAL_ACTIVITY = 1; //추가
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView tvStepCount;
    //[bg]
    Button startstopbutton; //시작/정지버튼
    Button settingsbutton; //설정
    float counts; //현재 걸음 수
    float precounts; //
    boolean press_stop = false;

    //[nh] km, kcal 추가
    TextView tvStepkm;
    TextView tvStepkcal;


    //[bg] - reset btn
    public void onClickEvent(View view){
        counts = 0;
        precounts = 0;
        tvStepCount.setText("Step Count : " + (int)counts);

        //[nh] - km, kcal 초기화
        tvStepkm.setText(0+"km");
        tvStepkcal.setText(0+"kcal");
    }

    public void startstop(View view){
        if (press_stop == true) {
            press_stop = false;
            startstopbutton.setText("STOP");
        } else {
            press_stop = true;
            startstopbutton.setText("START");
        }
    }

    public void settingsbutton(View view){
        Intent intent = new Intent(getApplicationContext(), ptsettings.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M) //추가
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //추가
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }

        tvStepCount = (TextView)findViewById(R.id.tvStepCount);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //부팅시부터 측정, 앱 종료해도 카운팅
        if(stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }
        startstopbutton = (Button)findViewById(R.id.startstopbutton);


        //[nh]
        tvStepkm = (TextView)findViewById(R.id.tvkm);
        tvStepkcal = (TextView)findViewById(R.id.tvkcal);
    }

    @Override
    protected void onResume() {
        super.onResume();
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
            //sensorManager.unregisterListener(this); //끔(백그라운드) 상태에서 작동안하는 코드
    }

    //[bg]
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && press_stop == false) {
            if (precounts < 1) {
                precounts = (int) event.values[0];
            }
            counts = (int) event.values[0] - precounts;
            tvStepCount.setText("Step Count : " + (int)counts);


            //[nh]
            tvStepkm.setText(String.format("%.2f",event.values[0] * 70 * 0.00001)+"km");
            tvStepkcal.setText(String.format("%.2f",event.values[0] * 70 * 0.00001 * 40)+"kcal");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}