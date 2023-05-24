package com.hamdan.acceleromatorminiproject.usecase

import com.hamdan.acceleromatorminiproject.domain.AccelerationRepository
import com.hamdan.acceleromatorminiproject.domain.Score
import javax.inject.Inject

class FetchHighScoresImpl @Inject constructor(
    private val accelerationRepository: AccelerationRepository
) : FetchHighScoresUseCase {
    override suspend fun invoke(): List<Score> = try {
        accelerationRepository.fetchHighScores()
    } catch (e: Exception) {
        emptyList()
    }
}
