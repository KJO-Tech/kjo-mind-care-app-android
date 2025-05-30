package tech.kjo.kjo_mind_care.ui.theme

import androidx.compose.ui.graphics.Color

val primaryLight = Light.primary
val onPrimaryLight = Light.primarySoft
val primaryContainerLight = Light.primary
val onPrimaryContainerLight = Light.primarySoft
val secondaryLight = Light.secondary
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Light.secondary
val onSecondaryContainerLight = Color(0xFFFCFCFF)
val tertiaryLight = Light.accent
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Light.accent
val onTertiaryContainerLight = Color(0xFF462E1A)
val errorLight = Light.error
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Light.error
val onErrorContainerLight = Color(0xFFFFF9F8)
val backgroundLight = Light.background
val onBackgroundLight = Light.text
val surfaceLight = Light.card
val onSurfaceLight = Light.text
val surfaceVariantLight = Light.surface
val onSurfaceVariantLight = Light.textSecondary
val outlineLight = Light.border
val outlineVariantLight = Light.divider
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF313030)
val inverseOnSurfaceLight = Color(0xFFF3F0EF)
val inversePrimaryLight = Color(0xFF9CD1C5)
val surfaceDimLight = Color(0xFFDCD9D9)
val surfaceBrightLight = Color(0xFFFCF9F8)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF6F3F2)
val surfaceContainerLight = Color(0xFFF0EDEC)
val surfaceContainerHighLight = Color(0xFFEBE7E7)
val surfaceContainerHighestLight = Color(0xFFE5E2E1)

val primaryDark = Dark.primary
val onPrimaryDark = Dark.primarySoft
val primaryContainerDark = Dark.primary
val onPrimaryContainerDark = Dark.primarySoft
val secondaryDark = Dark.secondary
val onSecondaryDark = Color(0xFF23323F)
val secondaryContainerDark = Dark.secondary
val onSecondaryContainerDark = Color(0xFF142430)
val tertiaryDark = Dark.accent
val onTertiaryDark = Color(0xFF432B17)
val tertiaryContainerDark = Dark.accent
val onTertiaryContainerDark = Color(0xFF462E1A)
val errorDark = Dark.error
val onErrorDark = Color(0xFF680015)
val errorContainerDark = Dark.error
val onErrorContainerDark = Color(0xFFFFF9F8)
val backgroundDark = Dark.background
val onBackgroundDark = Dark.text
val surfaceDark = Dark.card
val onSurfaceDark = Dark.text
val surfaceVariantDark = Dark.surface
val onSurfaceVariantDark = Dark.textSecondary
val outlineDark = Dark.border
val outlineVariantDark = Dark.divider
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE5E2E1)
val inverseOnSurfaceDark = Color(0xFF313030)
val inversePrimaryDark = Color(0xFF33675D)
val surfaceDimDark = Color(0xFF131313)
val surfaceBrightDark = Color(0xFF3A3939)
val surfaceContainerLowestDark = Color(0xFF0E0E0E)
val surfaceContainerLowDark = Color(0xFF1C1B1B)
val surfaceContainerDark = Color(0xFF201F1F)
val surfaceContainerHighDark = Color(0xFF2A2A2A)
val surfaceContainerHighestDark = Color(0xFF353534)

object Light {
    val primary = Color(0xFF669A8F)
    val primaryLight = Color(0xFF8BB3A8)
    val primaryDark = Color(0xFF4F7A70)
    val primarySoft = Color(0xFFF4F8F7)
    // Secondary
    val secondary = Color(0xFF7B8B9A)
    val accent = Color(0xFFB8957A)
    // Background
    val background = Color(0xFFF6F9F8)
    val backgroundAlt = Color(0xFFF0F5F4)
    val card = Color(0xFFFFFFFF)
    val surface = Color(0xFFFAFCFB)
    // Text
    val text = Color(0xFF2A3F3B)
    val textSecondary = Color(0xFF5A6B67)
    val textMuted = Color(0xFF8A9B97)
    // Utility
    val border = Color(0xFFD4E3E0)
    val divider = Color(0xFFE8F1EF)
    val shadow = Color(0x26669A8F)
    // Status
    val error = Color(0xFFD63447)
    val success = Color(0xFF4F9A85)
    val warning = Color(0xFFE6A45C)
    val info = Color(0xFF5B8DBE)
}

object Dark {
    val primary = Color(0xFF7BB3A8)
    val primaryLight = Color(0xFF9BC7BC)
    val primaryDark = Color(0xFF5F8D82)
    val primarySoft = Color(0xFF1E2826)
    // Secondary
    val secondary = Color(0xFF8B9BA0)
    val accent = Color(0xFFD4A87E)
    // Background
    val background = Color(0xFF181C1B)
    val backgroundAlt = Color(0xFF1F2423)
    val card = Color(0xFF242B29)
    val surface = Color(0xFF2A322F)
    // Text
    val text = Color(0xFFF4F8F7)
    val textSecondary = Color(0xFFB8C5C2)
    val textMuted = Color(0xFF8A9B97)
    // Utility
    val border = Color(0xFF3A453F)
    val divider = Color(0xFF2F3A35)
    val shadow = Color(0x66000000)
    // Status
    val error = Color(0xFFE74C5C)
    val success = Color(0xFF66C2A5)
    val warning = Color(0xFFF39C5A)
    val info = Color(0xFF74A3D4)
}