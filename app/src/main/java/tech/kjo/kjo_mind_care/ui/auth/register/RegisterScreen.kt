package tech.kjo.kjo_mind_care.ui.auth.register


import android.R.attr.password
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.ThemedButton
import tech.kjo.kjo_mind_care.ui.components.ThemedPasswordTextField
import tech.kjo.kjo_mind_care.ui.components.ThemedTextField
import tech.kjo.kjo_mind_care.ui.auth.AuthScreenContainer


@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {

    val state = viewModel.state

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegistrationSuccess()
        }
    }

    AuthScreenContainer(
        title = stringResource(id = R.string.register_title),
        subtitle = stringResource(id = R.string.register_subtitle),
        onBack = onNavigateBack,
    ) {
        ThemedTextField(
            value = state.fullName,
            onValueChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it))},
            label = stringResource(id = R.string.register_fullname)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
            label = stringResource(id = R.string.register_email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedPasswordTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
            label = stringResource(id = R.string.register_password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedPasswordTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
            label = stringResource(id = R.string.register_confirm_password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ThemedButton(
            onClick = {
                viewModel.onEvent(RegisterEvent.Submit)
            },
            text = stringResource(id = R.string.register_button),
            enabled = !state.isLoading
        )

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.register_terms),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}