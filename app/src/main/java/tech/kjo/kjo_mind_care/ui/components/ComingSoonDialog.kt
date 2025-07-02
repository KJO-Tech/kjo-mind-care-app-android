package tech.kjo.kjo_mind_care.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R

/**
 * Un diálogo que informa al usuario que una característica está en desarrollo.
 * Se muestra automáticamente una vez por sesión (mientras la actividad no sea destruida completamente)
 * cuando se compone por primera vez.
 *
 * @param modifier Modificador para este Composable.
 */
@Composable
fun ComingSoonDialog(modifier: Modifier = Modifier) {
    var showDialog by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        if (showDialog) {
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        imageVector = Icons.Default.Build,
                        contentDescription = stringResource(id = R.string.dialog_feature_soon_title),
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.dialog_feature_soon_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.dialog_feature_soon_message),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = R.string.dialog_feature_soon_positive_button))
                }
            },
            modifier = modifier
        )
    }
}