/*

package com.example.shake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ar.sceneform.math.Quaternion;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private TextView orientationValuesTextView;
    private SensorManager sensorManager;
    private Sensor sensor;
    float[] imageVector = new float[3];
    private ImageView imageView;

    private ViewGroup.LayoutParams layoutParams;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        layoutParams = imageView.getLayoutParams();
        randomizeImagePosition();
        SwitchCompat gyroSwitch = findViewById(R.id.gyroSwitch);
        orientationValuesTextView = findViewById(R.id.orientationValuesTextView);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (sensor != null) {
            sensorManager.registerListener(orientationListener(), sensor, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        }



        gyroSwitch.setOnCheckedChangeListener((gyroToggle, isChecked) -> {
            if (isChecked) {
                orientationValuesTextView.setVisibility(View.VISIBLE);
            } else {
                orientationValuesTextView.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void randomizeImagePosition() {
        for (int i = 0; i < imageVector.length; i++) {
            int value = new Random().nextInt(685 * 2 + 1) - 685;
            imageVector[i] = value < 0 ? - value : value;
        }
        imageView.setX(imageVector[0]);
        imageView.setY(imageVector[1]);
        imageView.setZ(imageVector[2]);

    }


    float circumference = 2 * (float) Math.PI * 2.0f;
        protected SensorEventListener orientationListener() {

        return new SensorEventListener() {


            @Override
            public void onSensorChanged(SensorEvent gameRotation) {

                imageView.setTranslationX(imageVector[1] + (imageVector[0] * gameRotation.values[1] * circumference));
                imageView.setTranslationY(imageVector[0] + (imageVector[1] * gameRotation.values[0] * circumference));


                layoutParams.width = 300; // önskad bredd i pixlar
                layoutParams.height = 300; // önskad höjd i pixlar
                imageView.setLayoutParams(layoutParams);

                orientationValuesTextView.setText(
                        String.format(Locale.ROOT,
                                "x: %.3f y: %.3f  z: %.3f\n imageX: %d\n imageY: %d\nimageZ: %d",
                                gameRotation.values[0], gameRotation.values[1] , gameRotation.values[2],
                                (int) imageView.getX(),  (int) imageView.getY(), (int) imageView.getZ()));

                }


            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }
}

*/
package com.example.shake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView orientationValuesTextView, pointView;

    private int point = 0;
    private ImageView imageView;
    private final float[] referenceOrientation = new float[3];
    private final float[] imageVector = new float[3];
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        orientationValuesTextView = findViewById(R.id.orientationValuesTextView);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();


        if (savedInstanceState == null) {
            FragmentManager frm = getSupportFragmentManager();
            frm.beginTransaction().add(R.id.frame, PointFragment.class, null).commit();

        }


        layoutParams.height = 500;
        layoutParams.width = 500;
        randomizeImagePosition();

        SwitchCompat gyroSwitch = findViewById(R.id.gyroSwitch);
        gyroSwitch.setOnCheckedChangeListener((gyroToggle, isChecked) -> {
            if (isChecked) {
                orientationValuesTextView.setVisibility(View.VISIBLE);
            } else {
                orientationValuesTextView.setVisibility(View.INVISIBLE);
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorManager.getRotationMatrixFromVector(referenceOrientation, new float[9]);

        if (rotationSensor != null && acceleratorSensor != null) {
            sensorManager.registerListener(orientationListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(accMeterListener, acceleratorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        imageView.setOnClickListener(v -> {
            updatePoints();
            Toast.makeText(MainActivity.this, "OUCH! YOU GOT ME.", Toast.LENGTH_SHORT).show();
            randomizeImagePosition();
        });
    }


    private void updatePoints() {
        pointView = findViewById(R.id.pointView);
        pointView.setText(String.format(Locale.ROOT,"Point: %d", ++point));
    }

    private void randomizeImagePosition() {

        for (int i = 0; i < imageVector.length; i++) {
            int value = random.nextInt(1371);
            imageVector[i] = -value * 10;
        }
        imageView.setTranslationX(imageView.getX() * imageVector[0] * 10);
        imageView.setTranslationY(imageView.getY() * imageVector[1] * 10);
        imageView.setTranslationZ(imageView.getZ() * imageVector[2] * 10);

    }

    private final SensorEventListener accMeterListener = new SensorEventListener() {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            for (float f : sensorEvent.values) {
                if (f > 40) {
                    blinkColors();
                    showToast();
                    return;
                }
            }
        }

        private void blinkColors() {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            handler.postDelayed(() -> getWindow().getDecorView().setBackgroundColor(Color.GRAY), 1000);
            handler.postDelayed(() -> getWindow().getDecorView().setBackgroundColor(Color.BLACK), 2000);

        }


        private void showToast() {
            Toast.makeText(MainActivity.this, "HEY!! Take it easy", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private final SensorEventListener orientationListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            float yRotation = orientationValues[1] - referenceOrientation[1];
            float xRotation = orientationValues[2] - referenceOrientation[2];

            float circumference = 3 * (float) Math.PI * 2.0f;
            float newY = imageVector[2] * (float) (Math.sin(orientationValues[1]) * circumference);
            float newX = imageVector[1] * (float) (Math.sin(orientationValues[2]) * circumference);

            imageView.setTranslationX(newX / 100);
            imageView.setTranslationY(newY / 100);

            orientationValuesTextView.setText(
                    String.format(Locale.ROOT,
                            "x: %.3f y: %.3f  z: %.3f",
                            orientationValues[0], yRotation, xRotation));

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
