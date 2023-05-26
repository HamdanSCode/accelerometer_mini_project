package com.hamdan.acceleromatorminiproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccelerationDao {

    @Insert
    suspend fun insert(scoreEntity: List<ScoreEntity>)

    @Query(
        """
        DELETE FROM ${ScoreEntity.SCORE_TABLE_NAME}
        """
    )
    suspend fun nukeTable()

    @Query(
        """
            SELECT * FROM ${ScoreEntity.SCORE_TABLE_NAME} 
            ORDER BY ${ScoreEntity.ELAPSED_TIME} DESC, 
            ${ScoreEntity.DATE_ACQUIRED} ASC
            LIMIT $HIGH_SCORE_LIST_SIZE
        """
    )
    suspend fun fetchHighScores(): List<ScoreEntity>

    companion object {
        const val HIGH_SCORE_LIST_SIZE = 10
    }
}
