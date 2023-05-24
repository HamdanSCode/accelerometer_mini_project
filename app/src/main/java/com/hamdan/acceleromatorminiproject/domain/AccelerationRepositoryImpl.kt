package com.hamdan.acceleromatorminiproject.domain

import com.hamdan.acceleromatorminiproject.data.AccelerationDao
import javax.inject.Inject

class AccelerationRepositoryImpl @Inject constructor(
    private val accelerationDao: AccelerationDao,
): AccelerationRepository {
    override suspend fun insert(score: List<Score>) {
        try {
            accelerationDao.nukeTable()
            accelerationDao.insert(score.toScoreEntityList())
        } catch (e: Exception) {
            throw Exception(DATABASE_ACCESS_FAILED)
        }
    }

    override suspend fun fetchHighScores(): List<Score> {
        return try {
            accelerationDao.fetchHighScores().toHighScoreList()
        } catch (e: Exception) {
            throw Exception(DATABASE_ACCESS_FAILED)
        }
    }

    override suspend fun clearHighScores() {
        try {
            accelerationDao.nukeTable()
        } catch (e: Exception) {
            throw Exception(DATABASE_ACCESS_FAILED)
        }
    }

    companion object {
        const val DATABASE_ACCESS_FAILED = "Failed to Access Database"
    }
}
