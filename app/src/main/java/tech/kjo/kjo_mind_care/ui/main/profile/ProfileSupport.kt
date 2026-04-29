package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R

@Composable
fun ProfileSupport(
    onHelpSupport: () -> Unit,
    onAbout: () -> Unit
) {
    Text(
        stringResource(R.string.support_section),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )

    ListItem(
        headlineContent = { Text(stringResource(R.string.help_support)) },
        leadingContent = { Icon(Icons.Outlined.HelpOutline, null) },
        trailingContent = { Icon(Icons.Outlined.KeyboardArrowRight, null) },
        modifier = Modifier
            .clickable(onClick = onHelpSupport)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
    Spacer(modifier = Modifier.height(16.dp))

    ListItem(
        headlineContent = { Text(stringResource(R.string.about)) },
        leadingContent = { Icon(Icons.Outlined.Info, null) },
        trailingContent = { Icon(Icons.Outlined.KeyboardArrowRight, null) },
        modifier = Modifier
            .clickable(onClick = onAbout)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
}