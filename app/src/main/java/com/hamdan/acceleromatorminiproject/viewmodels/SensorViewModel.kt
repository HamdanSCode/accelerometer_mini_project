package com.hamdan.acceleromatorminiproject

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamdan.acceleromatorminiproject.GlobalConstants.TIMER_DURATION_MS
import com.hamdan.acceleromatorminiproject.data.Acceleration
import com.hamdan.acceleromatorminiproject.data.majorDifference
import com.hamdan.acceleromatorminiproject.domain.Score
import com.hamdan.acceleromatorminiproject.usecase.FetchHighScoresUseCase
import com.hamdan.acceleromatorminiproject.usecase.SaveScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val fetchHighScoresUseCase: FetchHighScoresUseCase,
    private val saveScoreUseCase: SaveScoreUseCase,
) : ViewModel() {
    sealed interface SensorState {
        object Ready : SensorState
        data class Counting(
            val acceleration: Acceleration,
            val elapsedTime: Long = 0,
        ) : SensorState

        data class ScoreScreen(
            val scoreList: List<Score>
        ) : SensorState

        object Loading : SensorState
        object Error : SensorState
        object Success : SensorState
        data class Failure(val elapsedTime: Long) : SensorState
    }

    private val _state: MutableState<SensorState> = mutableStateOf(SensorState.Ready)
    val state: MutableState<SensorState>
        get() = _state
    private var sensorState: SensorState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    fun highScoreButtonClicked() {
        sensorState = SensorState.Loading
        viewModelScope.launch {
            sensorState = SensorState.ScoreScreen(
                scoreList = fetchHighScoresUseCase.invoke()
            )
        }
    }

    fun readyClicked(acceleration: Acceleration) {
        startCount(acceleration)
    }

    private fun startCount(acceleration: Acceleration) {
        sensorState = SensorState.Counting(acceleration)
    }

    fun updateTime(newTime: Long) {
        if (state.value is SensorState.Counting) {
            val elapsedTime = (state.value as SensorState.Counting).elapsedTime
            if (elapsedTime >= TIMER_DURATION_MS) {
                succeed()
                viewModelScope.launch {
                    val score =
                        Score(dateAcquired = System.currentTimeMillis(), elapsedTime = elapsedTime)
                    fetchHighScoresUseCase.invoke()
                        .plus(score)
                        .sortedWith(compareByDescending<Score> { it.elapsedTime }
                            .thenBy { it.dateAcquired })
                        .apply { if (this.size > 10) this.dropLast(1) }
                        .also { saveScoreUseCase.invoke(it) }
                }
            } else {
                state.value = (state.value as SensorState.Counting).copy(elapsedTime = newTime)
            }
        }
    }

    private fun succeed() {
        sensorState = SensorState.Success
    }

    fun checkAcceleration(acceleration: Acceleration, onFail: () -> Unit) {
        sensorState.asCountingOrNull()?.let {
            if (acceleration.majorDifference(it.acceleration)) {
                onFail()
                failed()
            }
        }
    }

    fun errorDialogDismissed() {
        reset()
    }

    private fun reset() {
        sensorState = SensorState.Ready
    }

    private fun failed() {
        if (state.value is SensorState.Counting) {
            sensorState =
                SensorState.Failure((state.value as SensorState.Counting).elapsedTime).also {
                    viewModelScope.launch {
                        val score =
                            Score(dateAcquired = System.currentTimeMillis(), elapsedTime = it.elapsedTime)
                        fetchHighScoresUseCase.invoke()
                            .plus(score)
                            .sortedWith(compareByDescending<Score> { it.elapsedTime }
                                .thenBy { it.dateAcquired })
                            .apply { if (this.size > 10) this.dropLast(1) }
                            .also { saveScoreUseCase.invoke(it) }
                    }
                }
        } else {
            throw Exception("Shouldn't be being sent here if not counting")
        }
    }
}

fun SensorViewModel.SensorState.asCountingOrNull(): SensorViewModel.SensorState.Counting? {
    return this as? SensorViewModel.SensorState.Counting
}

fun SensorViewModel.SensorState.asSuccessOrNull(): SensorViewModel.SensorState.Success? {
    return this as? SensorViewModel.SensorState.Success
}

fun SensorViewModel.SensorState.asFailureOrNull(): SensorViewModel.SensorState.Failure? {
    return this as? SensorViewModel.SensorState.Failure
}

fun SensorViewModel.SensorState.asErrorOrNull(): SensorViewModel.SensorState.Error? {
    return this as? SensorViewModel.SensorState.Error
}

fun SensorViewModel.SensorState.asReadyOrNull(): SensorViewModel.SensorState.Ready? {
    return this as? SensorViewModel.SensorState.Ready
}
