package com.hamdan.acceleromatorminiproject.view.compose

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hamdan.acceleromatorminiproject.R
import com.hamdan.acceleromatorminiproject.SensorViewModel
import com.hamdan.acceleromatorminiproject.domain.Score
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun HighScoreScreen(
    state: SensorViewModel.SensorState,
    leavePage: () -> Unit,
) {
    when (state) {
        is SensorViewModel.SensorState.ScoreScreen -> {
            ScoreList(scoreList = state.scoreList, onBackClicked = leavePage)
        }

        is SensorViewModel.SensorState.Error -> {
            ErrorDialog(leavePage)
        }

        else -> {
            Loading()
        }
    }
}

@Composable
fun Loading() {
    StandardWrapper {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(.5f)
        )
    }
}

@Composable
fun ErrorDialog(leavePage: () -> Unit) {
    AlertDialog(
        onDismissRequest = { leavePage() },
        title = {
            Text(text = stringResource(id = R.string.alert_title))
        },
        text = {
            Text(text = stringResource(id = R.string.alert_body))
        },
        confirmButton = {
            Text(
                text = stringResource(id = R.string.alert_confirm),
                modifier = Modifier
                    .clickable { leavePage() },
            )
        },
    )
}

@Composable
fun ScoreList(
    scoreList: List<Score>,
    onBackClicked: () -> Unit,
) {
    StandardWrapper {
        scoreList.forEach {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = DateUtils.formatElapsedTime(it.elapsedTime ?: 0L))
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = formatDate(it.dateAcquired ?: 0L))
            }
        }
        Button(onClick = {
            onBackClicked()
        }) {
            Text(text = stringResource(id = R.string.back_to_game))
        }
    }
}

fun formatDate(milliseconds: Long): String {
    val instant = Instant.ofEpochMilli(milliseconds)
    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val date = zonedDateTime.toLocalDate()
    val formatter =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault())
    return date.format(formatter)
}
