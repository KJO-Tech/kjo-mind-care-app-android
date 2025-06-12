package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

data class ProfileUiState(
    val photoUrl: String = "",
    val name: String = "–",
    val email: String = "–",
    val checkIns: Int = 0,
    val posts: Int = 0,
    val badges: Int = 0,
    val notifications: Boolean = true,
    val darkMode: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onEditProfile: () -> Unit = {},
    onAccountSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
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
            onLogout = onLogout
        )
    }
}
