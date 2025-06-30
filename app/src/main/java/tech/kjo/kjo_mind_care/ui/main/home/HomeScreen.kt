package tech.kjo.kjo_mind_care.ui.main.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import tech.kjo.kjo_mind_care.utils.loadDrawableResByName
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentLanguage = Locale.getDefault().language

    // Launcher para solicitar el permiso de notificación
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, puedes enviar notificaciones
        } else {
            // Permiso denegado, notifica al usuario si las notificaciones son importantes
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
//        homeViewModel.fetchDailyActivities()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.welcome_message, uiState.userName),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notifications)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoodSection(
                onMoodSelected = { /* Actualizar estado de ánimo */ },
                onDetailCheckInClicked = {
                    navController.navigate(Screen.MoodEntryDetail.route)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoadingActivities) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.activitiesError != null) {
                Text(
                    text = stringResource(R.string.activities_error_message, uiState.activitiesError ?: "Unknown error"),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (uiState.selectedDailyActivities.isNotEmpty()) {
                DailyActivitiesSection(
                    activities = uiState.selectedDailyActivities.map {
                        val activityTitle = it.exercise.localizedTitle[currentLanguage] ?: it.exercise.localizedTitle["en"] ?: stringResource(R.string.default_exercise_title)
                        val activityDescription = it.exercise.localizedDescription[currentLanguage] ?: it.exercise.localizedDescription["en"] ?: stringResource(R.string.default_exercise_description)

                        ActivityItemData(
                            iconRes = context.loadDrawableResByName(it.category.iconResName),
                            title = activityTitle,
                            description = activityDescription,
                            progress = 0f,
                            exerciseId = it.exercise.id
                        )
                    },
                    onActivityClick = { exerciseId ->
                        navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                    }
                )
            } else {
                Text(
                    text = stringResource(R.string.no_activities_found),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            WellnessTipSection(
                data = WellnessTipData(
                    iconRes = R.drawable.ic_idea,
                    title = stringResource(R.string.wellness_tip_gratitude_title),
                    description = stringResource(R.string.wellness_tip_gratitude_description),
                    callToAction = stringResource(R.string.wellness_tip_gratitude_cta)
                ),
                onActionClick = { /* Acción al clickear el tip */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
            CommunitySection(
                posts = listOf(
                    CommunityPostData(
                        imageRes = R.drawable.ic_mood_happy,
                        title = stringResource(R.string.community_post_peace_title),
                        author = stringResource(R.string.community_post_peace_author),
                        timeAgo = stringResource(R.string.community_post_peace_time_ago)
                    )
                ),
                onSeeAllClick = { /* Navegar a ver más posts */ },
                onPostClick = { /* Abrir post específico */ }
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}