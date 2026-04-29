package tech.kjo.kjo_mind_care.ui.main.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.enums.NotificationStatus
import tech.kjo.kjo_mind_care.ui.main.notifications.components.NotificationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back_button)
                        )
                    }
                },
                actions = {
                    if (uiState.notifications.any { it.status == NotificationStatus.NEW }) {
                        TextButton(onClick = { viewModel.showMarkAllAsReadConfirmation(true) }) {
                            Text(stringResource(R.string.mark_all_as_read))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refreshNotifications,
        ) {
            when {
                uiState.isLoading && !uiState.isRefreshing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_notifications_yet),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notifications, key = { it.id }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onNotificationClick = {
                                    viewModel.markNotificationAsRead(notification.id)
                                    if (notification.targetRoute.isNotBlank()) {
                                        onNavigateToRoute(notification.targetRoute)
                                    }
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    if (uiState.showMarkAllAsReadConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showMarkAllAsReadConfirmation(false) },
            title = { Text(stringResource(R.string.mark_all_as_read)) },
            text = { Text(stringResource(R.string.all_notifications_read)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.markAllAsRead()
                    viewModel.showMarkAllAsReadConfirmation(false)
                }) {
                    Text(stringResource(R.string.mark_all_as_read))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showMarkAllAsReadConfirmation(false) }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }
}
