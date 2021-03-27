package com.example.teampt2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PHYISCAL_ACTIVITY = 1; //추가
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView tvStepCount;

    //km, kcal 추가
   TextView tvStepkm;
   TextView tvStepkcal;

    @RequiresApi(api = Build.VERSION_CODES.M) //추가
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        } //추가

        tvStepCount = (TextView)findViewById(R.id.tvStepCount);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); //부팅시부터 측정, 앱 종료해도 카운팅
        if(stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }


        //
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            tvStepCount.setText("Step Count : " + String.valueOf(event.values[0]));

            //
            tvStepkm.setText(String.format("%.2f",event.values[0] * 70 * 0.00001)+"km");
            tvStepkcal.setText(String.format("%.2f",event.values[0] * 70 * 0.00001 * 40)+"kcal");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}