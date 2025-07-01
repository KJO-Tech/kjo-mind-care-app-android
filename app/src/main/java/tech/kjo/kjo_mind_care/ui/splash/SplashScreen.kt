package tech.kjo.kjo_mind_care.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.theme.Dark
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme
import tech.kjo.kjo_mind_care.ui.theme.Light
import tech.kjo.kjo_mind_care.ui.theme.logoFontFamily


@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit,
    onNavigateToMainApp: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            SplashUiState.Authenticated -> onNavigateToMainApp()
            SplashUiState.Unauthenticated -> onNavigateToWelcome()
            SplashUiState.Loading -> {}
            SplashUiState.NetworkError -> {}
            SplashUiState.GeneralError -> {}
        }
    }

    when (uiState) {
        SplashUiState.Loading,
        SplashUiState.Authenticated, // Para que el Splash se vea durante el delay
        SplashUiState.Unauthenticated -> Splash()

        SplashUiState.NetworkError -> NetworkErrorScreen(onTryAgain = { viewModel.checkSession() })
        SplashUiState.GeneralError -> GeneralErrorScreen(
            onTryAgain = { viewModel.checkSession() },
            onContactSupport = { /* TODO: Implementar navegación a soporte o mostrar información de contacto */ }
        )
    }
}

@Composable
fun Splash() {
    val backgroundColor = Light.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(16.dp),
            color = Light.primary
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_kjo),
                    contentDescription = stringResource(R.string.app_logo),
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = logoFontFamily,
            fontSize = 36.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = backgroundColor,
            trackColor = Color.White,
        )
    }
}

@Composable
fun NetworkErrorScreen(onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Light.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Light.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = stringResource(R.string.network_error_title),
                    tint = Dark.error,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.network_error_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Dark.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.network_error_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Light.text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onTryAgain) {
                    Text(stringResource(R.string.try_again_button), color = Light.text)
                }
            }
        }
    }
}

@Composable
fun GeneralErrorScreen(onTryAgain: () -> Unit, onContactSupport: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Light.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Light.card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = stringResource(R.string.general_error_title),
                    tint = Dark.error,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.general_error_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Dark.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.general_error_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Light.text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onTryAgain) {
                    Text(stringResource(R.string.try_again_button), color = Light.text)
                }
            }
        }
    }
}

@Preview
@Composable
fun SplashPreview() {
    KJOMindCareTheme {
        Splash()
    }
}

@Preview
@Composable
fun NetworkErrorPreview() {
    KJOMindCareTheme {
        NetworkErrorScreen(
            onTryAgain = { }
        )
    }
}

@Preview()
@Composable
fun GeneralErrorPreview() {
    KJOMindCareTheme(darkTheme = true) {
        GeneralErrorScreen(
            onTryAgain = { },
            onContactSupport = { }
        )
    }
}