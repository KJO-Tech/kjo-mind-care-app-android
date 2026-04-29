package tech.kjo.kjo_mind_care

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileViewModel
import tech.kjo.kjo_mind_care.ui.navigation.KJOMindCareNavHost
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    private var currentDeepLinkIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar currentDeepLinkIntent con el intent que lanzó la actividad
        currentDeepLinkIntent = intent

        setContent {
            val uiState = profileViewModel.uiState.collectAsState().value
            val useDarkTheme = uiState.darkMode

            KJOMindCareTheme(
                darkTheme = useDarkTheme,
                dynamicColor = false
            ) {
                KJOMindCareNavHost(
                    profileViewModel = profileViewModel,
                    deepLinkIntent = currentDeepLinkIntent
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        currentDeepLinkIntent = intent // Actualiza el State para que Compose reaccione
    }
}