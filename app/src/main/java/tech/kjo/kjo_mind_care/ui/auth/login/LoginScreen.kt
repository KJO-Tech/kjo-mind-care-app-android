package tech.kjo.kjo_mind_care.ui.auth.login

import tech.kjo.kjo_mind_care.ui.components.ThemedTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.ThemedButton
import tech.kjo.kjo_mind_care.ui.components.ThemedPasswordTextField
import tech.kjo.kjo_mind_care.ui.auth.AuthScreenContainer

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScreenContainer(
        title = stringResource(id = R.string.login_title),
        subtitle = stringResource(id = R.string.login_subtitle),
        onBack = null
    ) {
        ThemedTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.login_email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ThemedPasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.login_password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ThemedButton(
            onClick = {
                // Logica de login y pasarle el callback de onLoginSuccess
                onLoginSuccess()
            },
            text = stringResource(id = R.string.login_button)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.login_no_account),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.login_register),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
