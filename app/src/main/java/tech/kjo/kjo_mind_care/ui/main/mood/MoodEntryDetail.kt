package tech.kjo.kjo_mind_care.ui.main.mood

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Mood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodEntryDetail(
    moodId: String?,
    onCancel: () -> Unit = {},
    viewModel: MoodViewModel = hiltViewModel(),
    onMoodSaved: () -> Unit
) {
    val moodsUiState by viewModel.moodsUiState.collectAsState()
    val historyUiState by viewModel.historyUiState.collectAsState()
    val saveMoodResult by viewModel.saveMoodResult.collectAsState(initial = null)
    val context = LocalContext.current

    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var noteText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(moodId, moodsUiState.moods) {
        if (moodId != null && moodsUiState.moods.isNotEmpty()) {
            selectedMood = moodsUiState.moods.find { it.id == moodId }
        }
    }

    LaunchedEffect(saveMoodResult) {
        saveMoodResult?.let {
            it.onSuccess {
                Toast.makeText(context, "Mood saved successfully", Toast.LENGTH_SHORT).show()
                onMoodSaved()
            }.onFailure { throwable ->
                Toast.makeText(
                    context,
                    "Error: ${throwable.message ?: "Unknown error"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.record_mood_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.mood_question),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Adjust height as needed
            ) {
                items(moodsUiState.moods) { mood ->
                    MoodSelectItem(
                        mood = mood,
                        isSelected = mood.id == selectedMood?.id,
                        onSelect = { selectedMood = mood }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text(stringResource(R.string.mood_note_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                enabled = !historyUiState.isSaving,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text(
                        text = stringResource(R.string.cancel_button),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        selectedMood?.let {
                            viewModel.saveMood(it.id, noteText.text)
                        }
                    },
                    enabled = selectedMood != null && !historyUiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (historyUiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.save_button),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodSelectItem(
    mood: Mood,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(mood.color))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onSelect,
        modifier = Modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        border = if (isSelected) BorderStroke(2.dp, color) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = mood.image,
                contentDescription = mood.localizedName(),
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mood.localizedName(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
