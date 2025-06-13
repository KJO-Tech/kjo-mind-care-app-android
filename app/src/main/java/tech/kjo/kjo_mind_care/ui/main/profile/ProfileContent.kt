package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.ui.components.LogoutButton
import tech.kjo.kjo_mind_care.R

@Composable
internal fun ProfileContent(
    state: ProfileUiState,
    modifier: Modifier = Modifier,
    onEditProfile: () -> Unit,
    onAccountSettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onHelpSupport: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        ProfileHeader(
            photoUrl = state.photoUrl,
            name = state.name,
            email = state.email,
            onEditProfile = onEditProfile
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileStats(
            stats = listOf(
                StatItem(stringResource(R.string.check_ins), state.checkIns),
                StatItem(stringResource(R.string.posts), state.posts),
                StatItem(stringResource(R.string.badges), state.badges)
            )
        )

        ProfileSettings(
            notificationsEnabled = state.notifications,
            darkModeEnabled = state.darkMode,
            onAccountSettings = onAccountSettings,
            onToggleNotifications = onToggleNotifications,
            onToggleDarkMode = onToggleDarkMode
        )


        ProfileSupport(
            onHelpSupport = onHelpSupport,
            onAbout = onAbout
        )

        Spacer(modifier = Modifier.height(24.dp))


        LogoutButton(onLogout = onLogout)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
