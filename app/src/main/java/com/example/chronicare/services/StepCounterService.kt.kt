package com.example.chronicare.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.chronicare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private var stepsAtStartOfDay = -1
    private var stepsToday = 0

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "StepCounterChannel"
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            stopSelf()
            return
        }

        loadSavedBaseline()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)

        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking your steps")
            .setContentText("Steps today: $stepsToday")
            .setSmallIcon(R.drawable.ic_directions_walk)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        val totalSinceBoot = event.values[0].toInt()

        // First reading of the day
        if (stepsAtStartOfDay == -1) {
            stepsAtStartOfDay = totalSinceBoot
            saveBaseline(stepsAtStartOfDay)
        }

        stepsToday = totalSinceBoot - stepsAtStartOfDay

        // Update notification
        updateNotification()

        // Save to Firebase
        uploadStepsToFirebase(stepsToday)
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking your steps")
            .setContentText("Steps today: $stepsToday")
            .setSmallIcon(R.drawable.ic_directions_walk)
            .setOngoing(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun uploadStepsToFirebase(steps: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val docRef = db.collection("users").document(userId)
            .collection("dailySteps").document(date)

        val data = mapOf(
            "steps" to steps,
            "timestamp" to System.currentTimeMillis()
        )

        docRef.set(data)
    }

    private fun saveBaseline(value: Int) {
        val prefs = getSharedPreferences("stepPrefs", MODE_PRIVATE)
        prefs.edit().putInt("baseline", value).apply()
    }

    private fun loadSavedBaseline() {
        val prefs = getSharedPreferences("stepPrefs", MODE_PRIVATE)
        stepsAtStartOfDay = prefs.getInt("baseline", -1)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
