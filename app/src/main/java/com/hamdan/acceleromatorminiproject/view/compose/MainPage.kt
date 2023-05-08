package com.hamdan.acceleromatorminiproject

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hamdan.acceleromatorminiproject.GlobalConstants.TIMER_DURATION_MS
import com.hamdan.acceleromatorminiproject.view.compose.StandardWrapper

@Composable
fun MainPageContent(
    state: SensorViewModel.SensorState,
    onReadyClicked: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    when (state) {
        is SensorViewModel.SensorState.Counting -> {
            StandardWrapper {
                Text(text = stringResource(id = R.string.timer_heading))
                Text(
                    text = stringResource(
                        id = R.string.timer_text,
                        (TIMER_DURATION_MS - state.elapsedTime) / 1000.0
                    )
                )
            }
        }

        SensorViewModel.SensorState.Error -> {
            AlertDialog(
                onDismissRequest = { onErrorDismiss() },
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
                            .clickable { onErrorDismiss() },
                    )
                },
            )
        }

        is SensorViewModel.SensorState.Failure -> StandardWrapper {
            //Text(text = "You held still for: ${String.format("%.2f", state.elapsedTime / 1000.0)}S")
            Text(
                text = stringResource(
                    id = R.string.failed_time_text,
                    state.elapsedTime.toFloat() / 1000
                )
            )
            Button(onClick = {
                onReadyClicked()
            }) {
                Text(text = stringResource(id = R.string.failed_try_again))
            }
        }

        SensorViewModel.SensorState.Ready -> {
            StandardWrapper {
                Text(text = stringResource(id = R.string.ready_prompt))
                Button(onClick = {
                    onReadyClicked()
                }) {
                    Text(text = stringResource(id = R.string.ready_start_text))
                }
            }
        }

        SensorViewModel.SensorState.Success -> {
            StandardWrapper {
                Text(text = stringResource(id = R.string.success_text))
                Button(onClick = {
                    onReadyClicked()
                }) {
                    Text(text = stringResource(id = R.string.success_try_again))
                }
            }
        }

        else -> throw Exception("Error illegal state")
    }
}
