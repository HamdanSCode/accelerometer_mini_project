package com.hamdan.acceleromatorminiproject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hamdan.acceleromatorminiproject.services.SensorCallbacks
import com.hamdan.acceleromatorminiproject.services.Timer
import com.hamdan.acceleromatorminiproject.services.TimerCallbacks
import com.hamdan.acceleromatorminiproject.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerService : Service(), Timer, TimerCallbacks, SensorEventListener, SensorCallbacks {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    //region Sensor callbacks
    private var _onSensorChange: ((SensorEvent) -> Unit)? = null
    override var onSensorChange: ((SensorEvent) -> Unit)?
        get() = _onSensorChange
        set(value) {
            _onSensorChange = value
        }
    //endregion

    //region Timer callbacks
    private var _onTimerFinished: (() -> Unit)? = null
    override var onTimerFinished: (() -> Unit)?
        get() = _onTimerFinished
        set(value) {
            _onTimerFinished = value
        }

    private var _timeChanged: ((Long) -> Unit)? = null
    override var onTimeChanged: ((Long) -> Unit)?
        get() = _timeChanged
        set(value) {
            _timeChanged = value
        }

    private var _onSetEnd: (() -> Unit)? = null
    override var onSetEnd: (() -> Unit)?
        get() = _onSetEnd
        set(value) {
            _onSetEnd = value
        }

    private var _onSetRepeat: (() -> Unit)? = null
    override var onSetRepeat: (() -> Unit)?
        get() = _onSetRepeat
        set(value) {
            _onSetRepeat = value
        }
    //endregion

    //region Binding
    override fun onBind(intent: Intent): IBinder = binder

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
    //endregion

    //region Lifecycle
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle("Test Timer")
            .setContentText("Test Timer is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
        startForeground(CHANNEL_ID, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
    //endregion

    //region Timer logic
    private var timerJob: Job? = null
    private var isTimerRunning = false
    private var startedTimerTimestamp = 0L

    override fun start() {
        Log.d("Service", "SERVICE START")
        initializeSensor()
        startTimer()
    }

    /**
     * To start the sensor for the service we have to get a sensor manager and sensor
     */
    private fun initializeSensor() {
        Log.v("Service", "Initializing sensor")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // when service stops we are also stopping the sensor
    override fun end() {
        Log.d("Service", "SERVICE STOP")
        isTimerRunning = false
        sensorManager.unregisterListener(this)
        sensor = null
        try {
            timerJob?.cancel()
        } catch (e: Exception) {
            Log.d("Service", "Issue cancelling timer job!")
        }
        this.stopForeground(STOP_FOREGROUND_REMOVE)
        this.stopSelf()
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            startedTimerTimestamp = System.currentTimeMillis()
            isTimerRunning = true
            timerJob = scope.launch {
                while (isTimerRunning) {
                    val delta = System.currentTimeMillis() - startedTimerTimestamp
                    onTimeChanged?.invoke(delta)
                    delay(5)
                }
            }
        }
    }
    //endregion

    //region Sensor relevant overrides
    override fun onSensorChanged(event: SensorEvent) {
        _onSensorChange?.invoke(event)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    //endregion

    //region notification
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
    //endregion

    companion object {
        const val NOTIFICATION_NAME = "Accelerometer Notification"
        const val NOTIFICATION_CHANNEL = "Timer Service"
        const val CHANNEL_ID = 103
    }
}
