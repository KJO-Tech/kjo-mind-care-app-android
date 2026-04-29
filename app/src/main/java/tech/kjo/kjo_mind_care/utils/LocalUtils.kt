package tech.kjo.kjo_mind_care.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun getCurrentLanguageCode(): String {
    return LocalConfiguration.current.locale.language // Retorna "es", "en", etc.
}