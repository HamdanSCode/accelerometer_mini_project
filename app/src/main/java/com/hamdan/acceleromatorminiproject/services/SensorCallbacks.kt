package com.hamdan.acceleromatorminiproject.services

import android.hardware.SensorEvent

interface SensorCallbacks {
    var onSensorChange: ((SensorEvent) -> Unit)?
}
