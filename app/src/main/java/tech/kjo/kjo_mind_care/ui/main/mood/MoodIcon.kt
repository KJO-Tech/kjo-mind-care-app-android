package tech.kjo.kjo_mind_care.ui.main.mood


import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import tech.kjo.kjo_mind_care.data.model.MoodType

@Composable
fun MoodIcon(moodType: MoodType, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = moodType.iconResId),
        contentDescription = stringResource(id = moodType.nameResId),
        modifier = modifier,
        tint = Color.Unspecified
    )
}