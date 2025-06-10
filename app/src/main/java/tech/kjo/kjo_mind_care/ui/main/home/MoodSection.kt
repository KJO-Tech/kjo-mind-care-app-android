package tech.kjo.kjo_mind_care.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.kjo.kjo_mind_care.R

data class MoodOption(
    val iconRes: Int,
    val label: String
)

@Composable
fun MoodSection(
    onMoodSelected: (MoodOption) -> Unit = {},
    onDetailCheckInClicked: () -> Unit = {}
) {
    val moodOptions = listOf<MoodOption>(
        MoodOption(R.drawable.ic_mood_joyful, stringResource(id = R.string.mood_joyful)),
        MoodOption(R.drawable.ic_mood_happy, stringResource(id = R.string.mood_happy)),
        MoodOption(R.drawable.ic_mood_anxious, stringResource(id = R.string.mood_anxious)),
        MoodOption(R.drawable.ic_mood_neutral, stringResource(id = R.string.mood_neutral)),
        MoodOption(R.drawable.ic_mood_sad, stringResource(id = R.string.mood_sad))
    )
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.mood_question),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                moodOptions.forEachIndexed { index, mood ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedIndex = index
                            onMoodSelected(mood)
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            tonalElevation = if (selectedIndex == index) 8.dp else 0.dp,
                            shadowElevation = if (selectedIndex == index) 4.dp else 0.dp,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = mood.iconRes),
                                contentDescription = mood.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mood.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onDetailCheckInClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Registro detallado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}