package tech.kjo.kjo_mind_care.ui.auth.register


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.ThemedButton
import tech.kjo.kjo_mind_care.ui.components.ThemedPasswordTextField
import tech.kjo.kjo_mind_care.ui.components.ThemedTextField
import tech.kjo.kjo_mind_care.ui.auth.AuthScreenContainer


@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AuthScreenContainer(
        title = stringResource(id = R.string.register_title),
        subtitle = stringResource(id = R.string.register_subtitle),
        onBack = onNavigateBack,
    ) {
        ThemedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = stringResource(id = R.string.register_fullname)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.register_email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedPasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.register_password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedPasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(id = R.string.register_confirm_password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ThemedButton(
            onClick = {
                //Logica de registro y pasarle el callback de onRegistrationSuccess
                onRegistrationSuccess()
            },
            text = stringResource(id = R.string.register_button)
        )

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