package com.example.myvoice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * this service will be activated after the phone is shaked and it will start a BCR that will open the app no matter which screen you on
 */
public class MySensorService extends Service implements SensorEventListener {
    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    public static boolean openClass = false;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    /**
     * check if the phone can use the accelerometer sensor and if it can the app set an accelerometer
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }
    /**
     * when the service is started register the sensor and make him available to use
     * @param intent the intent that open this service
     * @param flags says if the service live outside the app
     * @param startId
     * @return the flags
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;

    }
    /**
     * bind the service into activity
     * and it is not in use in my app
     * @param intent not in use
     * @return not in use
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * when the sensor feel a change
     * the app checks if the user shaked his phone more than usual(more that the stats i chose) and activate the service to open the app if it happens
     * @param event the info of what happened
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;


//        Log.d("AccService: itamar,", ""+deltaX);

        if(deltaX > 0 || deltaY > 0 || deltaZ > 5 ) {
            Log.d("ggAccService: niv,", "pending intent");
//
//            PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.blah.blah.somemessage"), PendingIntent.FLAG_IMMUTABLE );
//            AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
//
//            manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 100*5, pintent );
//

//            Intent intent = new Intent();
//            intent.setAction("com.blah.blah.somemessage");
//            // add this line to have intent delivered explicitly to your app
//            // use package name of your ReceiveBroadcast project
//            intent.setPackage("com.example.myvoice");
//            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            sendBroadcast(intent);

                if(MySensorService.openClass) {
                    MySensorService.openClass = false;

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

        }
    }
    /**
     * not in use
     * @param sensor not in use
     * @param accuracy not in use
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
