package com.hamdan.acceleromatorminiproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hamdan.acceleromatorminiproject.data.ScoreEntity.Companion.SCORE_TABLE_NAME

@Entity(tableName = SCORE_TABLE_NAME)
data class ScoreEntity(
    @ColumnInfo(DATE_ACQUIRED) var dateAcquired: Long?,
    @ColumnInfo(ELAPSED_TIME) var elapsedTime: Long?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    companion object {
        const val SCORE_TABLE_NAME = "score_table"
        const val DATE_ACQUIRED = "score_date_acquired"
        const val ELAPSED_TIME = "score_elapsed_time"
    }
}
