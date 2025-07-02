package tech.kjo.kjo_mind_care.ui.main.profile

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue


data class ProfileUiState(
    val photoUrl: String = "",
    val name: String = "–",
    val email: String = "–",
    val checkIns: Int = 0,
    val posts: Int = 0,
    val badges: Int = 0,
    val notifications: Boolean = true,
    val darkMode: Boolean = true,
    val isLoggingOut : Boolean = false,
    val logoutError: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditProfile: () -> Unit = {},
    onAccountSettings: () -> Unit = {},
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val logoutEvent by viewModel.logoutEvent.collectAsState(initial = Result.success(Unit))
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect { result ->
            result.onSuccess {
                onNavigateToLogin()
            }.onFailure { throwable ->
                Toast.makeText(context, throwable.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(

    ) { padding ->
        ProfileContent(
            state = uiState,
            modifier = Modifier.padding(padding),
            onEditProfile = onEditProfile,
            onAccountSettings = onAccountSettings,
            onToggleNotifications = { enabled -> viewModel.toggleNotifications(enabled) },
            onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
            onHelpSupport = { /*Logica*/ },
            onAbout = {/*Logica*/ },
            onLogout = viewModel::logout,
            isLoggingOut = uiState.isLoggingOut
        )
    }
}
