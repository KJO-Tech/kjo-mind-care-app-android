package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoodScreen(
    modifier: Modifier = Modifier,
    onRecordMoodClicked: () -> Unit = {},
    onCancelEntry: () -> Unit = {},
    onSaveEntry: (MoodOption?, String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            MoodOverviewScreen(
                onRecordMoodClicked = onRecordMoodClicked,
                onNavigateToMoodEntry = { /* opcional */ }
            )
        }

        item {
            MoodEntryScreen(
                onCancel = onCancelEntry,
                onSave = onSaveEntry
            )
        }
    }
}