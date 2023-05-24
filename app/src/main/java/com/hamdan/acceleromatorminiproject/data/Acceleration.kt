package com.hamdan.acceleromatorminiproject.data

import com.hamdan.acceleromatorminiproject.GlobalConstants

data class Acceleration(val xAxis: Float, val yAxis: Float, val zAxis: Float)

fun Acceleration.majorDifference(acceleration: Acceleration): Boolean {
    return (this.xAxis - acceleration.xAxis < -GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE || this.xAxis + acceleration.xAxis > GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE) || (this.yAxis - acceleration.yAxis < -GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE || this.yAxis + acceleration.yAxis > GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE) || (this.zAxis - acceleration.zAxis < -GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE || this.zAxis + acceleration.zAxis > GlobalConstants.MINIMUM_ACCELERATION_TOLERANCE)
}