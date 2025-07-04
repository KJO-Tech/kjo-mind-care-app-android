package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MoodOption(
    val title: String,
    val description: String,
    val iconResId: Int,
    val titleRes: Int
)

data class MoodOptionResource(
    val titleRes: Int,
    val descRes: Int,
    val iconRes: Int
)