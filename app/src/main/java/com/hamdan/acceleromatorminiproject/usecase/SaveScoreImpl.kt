package com.hamdan.acceleromatorminiproject.usecase

import android.util.Log
import com.hamdan.acceleromatorminiproject.GlobalConstants.ERROR_LOG_TAG
import com.hamdan.acceleromatorminiproject.GlobalConstants.ERROR_SAVING_SCORE
import com.hamdan.acceleromatorminiproject.domain.AccelerationRepository
import com.hamdan.acceleromatorminiproject.domain.Score
import javax.inject.Inject

class SaveScoreImpl @Inject constructor(
    private val accelerationRepository: AccelerationRepository
) : SaveScoreUseCase {
    override suspend fun invoke(scores: List<Score>) {
        try {
            accelerationRepository.insert(scores)
        } catch (e: Exception) {
            Log.d(ERROR_LOG_TAG, ERROR_SAVING_SCORE)
        }
    }
}
