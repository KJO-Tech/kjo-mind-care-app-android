package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerStart(
    modifier: Modifier = Modifier,
    viewModel: MoodViewModel = hiltViewModel(),
    onRecordMoodClicked: () -> Unit = {},
    onNavigateToMoodEntry: () -> Unit
) {
    val uiState by viewModel.historyUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mood_tracker_title)) },
                actions = {
                    Button(onClick = onRecordMoodClicked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_plus_circle),
                                contentDescription = stringResource(R.string.add_new),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(text = stringResource(R.string.record_mood_button))
                        }
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    RecentEntries(
                        entries = uiState.moodEntries,
                        moodsMap = uiState.moodsMap,
                        modifier = Modifier.fillMaxWidth(),
                        onEntryClick = { onNavigateToMoodEntry() }
                    )
                }
            }
        }
    }
}
