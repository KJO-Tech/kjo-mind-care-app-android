package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R


@Composable
fun MoodTrackerStart(
    modifier: Modifier = Modifier,
    viewModel: MoodViewModel = hiltViewModel(),
    onRecordMoodClicked: () -> Unit = {},
    onNavigateToMoodEntry: () -> Unit
) {
    val uiState: MoodViewModel.MoodHistoryUiState by viewModel.historyUiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mood Tracker",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = onRecordMoodClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus_circle),
                        contentDescription = "Añadir",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(text = "Record Mood", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            var selectedOption by remember { mutableStateOf("Week") }
            ChoiceChip(
                selected = selectedOption == "Week",
                onClick = { selectedOption = "Week"  },
                label = { Text("Week") },
                modifier = Modifier.padding(end = 8.dp)
            )
            ChoiceChip(
                selected = selectedOption == "Month",
                onClick = { selectedOption = "Month" },
                label = { Text("Month") }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mood Trends",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        MoodChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        MoodInsights(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        RecentEntries(
            entries = uiState.moodEntries.take(5),
            modifier = Modifier.fillMaxWidth(),
            onEntryClick = {  }
        )
    }

    if (uiState.isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxWidth().height(150.dp))
    } else if (uiState.error != null) {
        Text(
            text = "Error: ${uiState.error}",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
    }

}


@Composable
private fun ChoiceChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color =
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        tonalElevation = if (selected) 4.dp else 0.dp,
        modifier = modifier
            .height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight()
        ) {
            label()
        }
    }
}