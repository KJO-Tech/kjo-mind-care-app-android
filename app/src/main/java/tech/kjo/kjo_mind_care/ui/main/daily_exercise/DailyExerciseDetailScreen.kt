package tech.kjo.kjo_mind_care.ui.main.daily_exercise

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.ExerciseContentType
import tech.kjo.kjo_mind_care.ui.components.YouTubePlayerWebView
import tech.kjo.kjo_mind_care.ui.components.getYouTubeVideoId
import tech.kjo.kjo_mind_care.utils.Resource
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyExerciseDetailScreen(
    exerciseId: String,
    onNavigateBack: () -> Unit,
    viewModel: DailyExerciseDetailViewModel = hiltViewModel()
) {
    val exerciseState by viewModel.exerciseState.collectAsState()
    val context = LocalContext.current
    val currentLanguage = Locale.getDefault().language

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exercise_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (exerciseState) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_exercise))
                }
                is Resource.Success -> {
                    val exercise = (exerciseState as Resource.Success).data
                    if (exercise != null) {
                        val title = exercise.localizedTitle[currentLanguage] ?: exercise.localizedTitle["en"] ?: stringResource(R.string.default_exercise_title)
                        val description = exercise.localizedDescription[currentLanguage] ?: exercise.localizedDescription["en"] ?: stringResource(R.string.default_exercise_description)
                        val contentText = exercise.localizedContentText[currentLanguage] ?: exercise.localizedContentText["en"] ?: stringResource(R.string.default_exercise_content_text)

                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Renderizar contenido según el tipo
                        when (exercise.contentType) {
                            ExerciseContentType.VIDEO -> {
                                val videoId = getYouTubeVideoId(exercise.contentUrl)
                                if (videoId != null) {
                                    YouTubePlayerWebView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        videoId = videoId
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.invalid_video_url),
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.contentUrl))
                                        context.startActivity(intent)
                                    }) {
                                        Text(stringResource(R.string.open_in_browser))
                                    }
                                }
                            }
                            ExerciseContentType.AUDIO -> {
                                Text(
                                    text = stringResource(R.string.audio_content),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.contentUrl))
                                    context.startActivity(intent)
                                }) {
                                    Text(stringResource(R.string.listen_audio))
                                }
                            }
                            ExerciseContentType.TEXT -> {
                                Text(
                                    text = stringResource(R.string.text_content),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = contentText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            ExerciseContentType.GAME -> {
                                Text(
                                    text = stringResource(R.string.game_content),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.game_description, title),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.contentUrl))
                                    context.startActivity(intent)
                                }) {
                                    Text(stringResource(R.string.start_game))
                                }
                            }
                        }
                    } else {
                        Text(stringResource(R.string.exercise_not_found_error))
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(R.string.error_loading_exercise, (exerciseState as Resource.Error).message ?: "Unknown error"),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> { /* Initial state or other states */ }
            }
        }
    }
}