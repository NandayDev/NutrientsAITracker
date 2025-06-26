@file:OptIn(ExperimentalMaterial3Api::class)

package dev.nanday.nutrients_ai_tracker.ui.date_picker

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar
import java.util.TimeZone

@Composable
fun DateChoiceDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier, viewModel: DateChoiceViewModel = hiltViewModel()) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = viewModel.selectedDate.time)
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Converti millis in UTC a Date locale
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.timeInMillis = millis
                        // Per evitare problemi di timezone, aggiungi un giorno se necessario
                        // (il DatePicker a volte restituisce il giorno precedente in UTC)
                        // Questo è un workaround comune.
                        val localCalendar = Calendar.getInstance()
                        localCalendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        viewModel.selectedDate = localCalendar.time
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}