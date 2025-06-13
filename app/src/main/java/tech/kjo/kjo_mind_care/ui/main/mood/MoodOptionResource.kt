package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MoodOptionResource(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val iconRes: Int
)

data class MoodOption(
    val title: String,
    val description: String,
    @DrawableRes val iconResId: Int
)