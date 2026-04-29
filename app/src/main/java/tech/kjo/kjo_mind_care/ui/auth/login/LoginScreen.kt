package tech.kjo.kjo_mind_care.ui.auth.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import tech.kjo.kjo_mind_care.ui.components.ThemedTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.ThemedButton
import tech.kjo.kjo_mind_care.ui.components.ThemedPasswordTextField
import tech.kjo.kjo_mind_care.ui.auth.AuthScreenContainer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.utils.Resource
import kotlin.jvm.java

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.loginState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleSignInState by viewModel.googleSignInState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Resultado recibido: resultCode=${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    viewModel.signInWithGoogle(account)
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Error: Google account not available.") }
                }
            } catch (e: ApiException) {
                val errorMessage = when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> "Google Sign-In cancelled."
                    CommonStatusCodes.NETWORK_ERROR -> "Network error. Please try again."
                    CommonStatusCodes.API_NOT_CONNECTED -> "API not connected. Check Google Play Services."
                    CommonStatusCodes.DEVELOPER_ERROR -> "Developer error. Check SHA-1 and default_web_client_id."
                    CommonStatusCodes.SIGN_IN_REQUIRED -> "Sign-in required. User needs to re-authenticate."
                    CommonStatusCodes.INTERNAL_ERROR -> "Internal error with Google Sign-In."
                    else -> "Google Sign-In Error: ${e.message} (Code: ${e.statusCode})"
                }
                scope.launch { snackbarHostState.showSnackbar(errorMessage) }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Error inesperado al iniciar sesión con Google.") }
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Google Sign-In cancelled or failed.") }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is Resource.Success -> {
                scope.launch { snackbarHostState.showSnackbar("Login successful.") }
                onLoginSuccess()
                viewModel.resetState()
            }
            is Resource.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state?.message ?: "Unknown login error.") }
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(googleSignInState) {
        when (googleSignInState ) {
            is Resource.Success -> {
                scope.launch { snackbarHostState.showSnackbar("Inicio de sesión con Google exitoso.") }
                onLoginSuccess()
                viewModel.resetState()
            }
            is Resource.Error -> {
                scope.launch { snackbarHostState.showSnackbar(googleSignInState?.message ?: "Error desconocido al iniciar sesión con Google.") }
                viewModel.resetState()
            }
            else -> Unit
        }
    }

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
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.forgot_password),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = 4.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state is Resource.Error) {
            Text(
                text = (state as Resource.Error).message ?: "Error desconocido",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }

        val currentError = (state as? Resource.Error)?.message ?: (googleSignInState as? Resource.Error)?.message
        if (currentError != null) {
            Text(
                text = currentError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }

        ThemedButton(
            onClick = {
                viewModel.login(email, password)
            },
            text = stringResource(id = R.string.login_button),
            enabled = state !is Resource.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener { task ->
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                Color.Gray.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(26.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.login_with_google),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
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

        if (state is Resource.Loading) {
            CircularProgressIndicator(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp))
        }
    }
}
