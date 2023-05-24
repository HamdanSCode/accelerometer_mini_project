package com.hamdan.acceleromatorminiproject.usecase

import com.hamdan.acceleromatorminiproject.domain.Score

interface FetchHighScoresUseCase {
    suspend fun invoke(): List<Score>
}
