package com.hamdan.acceleromatorminiproject.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.hamdan.acceleromatorminiproject.MainPageContent
import com.hamdan.acceleromatorminiproject.SensorViewModel
import com.hamdan.acceleromatorminiproject.TimerService
import com.hamdan.acceleromatorminiproject.data.Acceleration
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // assumption is absolute 0 relative movement so we start with an acceleration set of 0
    private val acceleration = Array(3) { 0f }

    private var boundService: TimerService? = null
    private var isBound: Boolean = false

    val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, TimerService::class.java)
        setContent {
            MainPageContent(
                state = sensorViewModel.state.value,
                onReadyClicked = {
                    if (isBound) {
                        unbindService()
                        bindService()
                    } else {
                        bindService()
                    }
                    startForegroundService(intent)
                },
                onErrorDismiss = {
                    sensorViewModel.errorDialogDismissed()
                },
            )
            if (sensorViewModel.state.value is SensorViewModel.SensorState.Success || sensorViewModel.state.value is SensorViewModel.SensorState.Error) {
                unbindService()
            }
        }
    }

    private val bindingConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            sensorViewModel.viewModelScope.launch {
                boundService = binder.getService()
                isBound = true
                boundService?.onTimeChanged = {
                    sensorViewModel.updateTime(it)
                }
                boundService?.onSensorChange = {
                    acceleration[0] = it.values[0]
                    acceleration[1] = it.values[1]
                    acceleration[2] = it.values[2]
                    sensorViewModel.checkAcceleration(
                        Acceleration(
                            acceleration[0],
                            acceleration[1],
                            acceleration[2]
                        )
                    ) {
                        unbindService()
                    }
                }
                // change state after updateing what the service needs to do when it emits events
                sensorViewModel.readyClicked(
                    Acceleration(
                        acceleration[0],
                        acceleration[1],
                        acceleration[2]
                    ),
                )
                // start service at the same time as we enter the counting state by clicking ready
                boundService?.start()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.v("Service", "SERVICE TOLD TO STOP")
            boundService?.end()
        }
    }

    // bind the service to the activity
    private fun bindService() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, bindingConnection, Context.BIND_AUTO_CREATE)
        }
    }

    // tell the service to stop and also end any actions its taking
    private fun unbindService() {
        if (isBound) {
            boundService?.end()
            unbindService(bindingConnection)
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (sensorViewModel.state.value is SensorViewModel.SensorState.Counting) {
            bindService()
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN) {
            // If the activity was launched from the launcher, finish this instance
            // if it's not the root of the task (i.e., not the original instance).
            if (!isTaskRoot) {
                finish()
            }
        }
    }
}
