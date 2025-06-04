package tech.kjo.kjo_mind_care.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.AppLogo
import tech.kjo.kjo_mind_care.ui.theme.Light
import tech.kjo.kjo_mind_care.ui.theme.logoFontFamily


@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit,
    onNavigateToMainApp: () -> Unit
) {
    Splash()

    LaunchedEffect(true) {
        // TODO: Hay que hacer un check de sesión antes de navegar y quitar el delay
        delay(1300L)
        onNavigateToWelcome()
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
        AppLogo(
            modifier = Modifier.size(120.dp),
            modifierImage = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = logoFontFamily,
            fontSize = 36.sp,
            color = Color.White
        )
    }
}