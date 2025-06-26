package dev.nanday.nutrients_ai_tracker.ui.shared

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SimpleOkDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        text = text,
        confirmButtonText = "Ok",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun SimpleYesCancelDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        text = text,
        confirmButtonText = "Sì",
        dismissButtonText = "Annulla",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
private fun Dialog(
    text: String,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(confirmButtonText)
            }
        },
        dismissButton = dismissButtonText?.let {
            {
                TextButton(onClick = { onDismiss() }) {
                    Text(dismissButtonText)
                }
            }
        }
    )
}