package tech.kjo.kjo_mind_care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileViewModel
import tech.kjo.kjo_mind_care.ui.navigation.KJOMindCareNavHost
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme

class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState = profileViewModel.uiState.collectAsState().value
            val useDarkTheme = uiState.darkMode

            KJOMindCareTheme(
                darkTheme = useDarkTheme,
                dynamicColor = false
            ) {
                KJOMindCareNavHost(profileViewModel = profileViewModel)
            }
        }
    }
}