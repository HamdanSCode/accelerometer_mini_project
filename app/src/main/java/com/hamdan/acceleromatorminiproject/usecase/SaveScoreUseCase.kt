package com.hamdan.acceleromatorminiproject.usecase

import com.hamdan.acceleromatorminiproject.domain.Score

interface SaveScoreUseCase {
    suspend fun invoke(scores: List<Score>)
}
