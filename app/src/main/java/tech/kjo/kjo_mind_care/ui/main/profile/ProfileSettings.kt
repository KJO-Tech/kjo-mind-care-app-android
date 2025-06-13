package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R

@Composable
fun ProfileSettings(
    notificationsEnabled: Boolean,
    darkModeEnabled: Boolean,
    onAccountSettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.account_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.account_settings)) },
            leadingContent = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable(onClick = onAccountSettings)

        )
        Spacer(modifier = Modifier.height(16.dp))
        ListItem(
            headlineContent = { Text(stringResource(R.string.notifications)) },
            leadingContent = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListItem(
            headlineContent = { Text(stringResource(R.string.dark_mode)) },
            leadingContent = { Icon(Icons.Outlined.DarkMode, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = onToggleDarkMode
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        )
    }
}