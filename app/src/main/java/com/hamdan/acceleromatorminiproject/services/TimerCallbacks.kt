package com.hamdan.acceleromatorminiproject.services

interface TimerCallbacks {
    var onTimerFinished: (() -> Unit)?
    var onTimeChanged: ((Long) -> Unit)?
    var onSetEnd: (() -> Unit)?
    var onSetRepeat: (() -> Unit)?
}
