package com.hamdan.acceleromatorminiproject.domain

import com.hamdan.acceleromatorminiproject.data.ScoreEntity

data class Score(
    val dateAcquired: Long?,
    val elapsedTime: Long?,
)

fun Score.toHighScoreEntity() = ScoreEntity(
    dateAcquired = dateAcquired,
    elapsedTime = elapsedTime,
)

fun ScoreEntity.toHighScore() = Score(
    dateAcquired = dateAcquired,
    elapsedTime = elapsedTime,
)

fun List<ScoreEntity>.toHighScoreList() = this.map { it.toHighScore() }

fun List<Score>.toScoreEntityList() = this.map { it.toHighScoreEntity() }
