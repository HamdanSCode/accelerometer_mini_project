package com.hamdan.acceleromatorminiproject.domain

interface AccelerationRepository {
    suspend fun insert(score: List<Score>)
    suspend fun fetchHighScores(): List<Score>
    suspend fun clearHighScores()
}
