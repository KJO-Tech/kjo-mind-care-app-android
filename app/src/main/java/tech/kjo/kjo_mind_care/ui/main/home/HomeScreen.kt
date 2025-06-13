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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    userName: String = "Friend",
    navController: NavController
) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current

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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.welcome_title),
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoodSection(
                onMoodSelected = {
                },
                onDetailCheckInClicked = {
                    navController.navigate(Screen.MoodEntryDetail.route)
                }

            )

            Spacer(modifier = Modifier.height(32.dp))
            DailyActivitiesSection(
                activities = listOf(
                    ActivityItemData(
                        iconRes = R.drawable.ic_wind,
                        title = "Breathing Exercise",
                        description = "Practice deep breathing for 5 minutes",
                        progress = 0.7f
                    ),
                    ActivityItemData(
                        iconRes = R.drawable.ic_brain,
                        title = "Mindfulness Meditation",
                        description = "10 minute guided meditation",
                        progress = 0.3f
                    ),
                    ActivityItemData(
                        iconRes = R.drawable.ic_book,
                        title = "Journal",
                        description = "Write down your thoughts",
                        progress = 0f
                    )
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            WellnessTipSection(
                data = WellnessTipData(
                    iconRes = R.drawable.ic_idea,
                    title = "Practice Gratitude Daily",
                    description = "Take a few minutes each day to write down three things you're grateful for. This simple practice can shift your focus toward positive emotions and experiences, improving your overall mental well-being.",
                    callToAction = "Try this today"
                ),
                onActionClick = {

                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            CommunitySection(
                posts = listOf(
                    //Posts
                    CommunityPostData(
                        imageRes = R.drawable.ic_mood_happy,
                        title = "Finding Peace in Daily Routines",
                        author = "By Sarah Johnson",
                        timeAgo = "2 hours ago"
                    )

                ),
                onSeeAllClick = {
                    //Ir a ver mas
                },
                onPostClick = {
                    //Abrir post
                }
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

