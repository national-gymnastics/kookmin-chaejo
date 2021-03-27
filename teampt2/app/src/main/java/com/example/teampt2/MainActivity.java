package com.example.teampt2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import static com.example.teampt2.R.id.settingsbutton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PHYISCAL_ACTIVITY = 1; //추가
    SensorManager sensorManager;
    Sensor stepCountSensor;

    //[bg]
    Button startstopbutton; //시작/정지버튼
    float counts; //현재 걸음 수
    float precounts; //
    float goalcounts = 10000; //목표 걸음 수
    float percent; //현재 걸음 수치
    double kmstep; //거리 계산값
    double kmkcal; //칼로리 계산값
    boolean press_stop = false; //시작/정지 버튼 (초기값 = 누르지 않음 상태)

    //[nh] km, kcal 추가
    TextView tvStepCount;
    TextView tvStepkm;
    TextView tvStepkcal;
    TextView tvpercent;

    //리셋 버튼
    public void onClickEvent(View view){
        counts = 0;
        precounts = 0;
        percent = 0;
        tvStepCount.setText("걸음 수 : " + (int)counts + "/" + (int)goalcounts);
        //[nh] - km, kcal 초기화
        tvStepkm.setText(0+"km");
        tvStepkcal.setText(0+"kcal");
        tvpercent.setText(percent + "%");
    }

    //시작/정지 버튼
    public void startstop(View view){
        if (press_stop == true) {
            press_stop = false;
            startstopbutton.setText("STOP");
            startstopbutton.setBackgroundColor(Color.RED);
        } else {
            press_stop = true;
            startstopbutton.setText("START");
            startstopbutton.setBackgroundColor(Color.BLUE);
        }
    }

    //설정 버튼
    public void settingsbutton(View view){
        Intent intent = new Intent(getApplicationContext(), ptsettings.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M) //추가
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //센서 권한부여
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
        tvpercent = (TextView)findViewById(R.id.tvpercent);

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
            counts = (int) event.values[0] - precounts; //걸음수 측정 및 저장
            tvStepCount.setText((int)counts + "/" + (int)goalcounts);
            percent = counts * 100 / goalcounts; //퍼센트값 계산
            tvpercent.setText(percent + "%");

            //[nh]
            kmstep = event.values[0] * 70 * 0.000001; //거리 계산
            tvStepkm.setText(String.format("%.2f",kmstep)+"km");
            kmkcal = event.values[0] * 70 * 0.00001 * 40; //칼로리 계산
            tvStepkcal.setText(String.format("%.2f",kmkcal)+"kcal");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}