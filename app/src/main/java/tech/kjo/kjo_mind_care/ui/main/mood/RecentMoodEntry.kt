package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Mood
import tech.kjo.kjo_mind_care.data.model.MoodEntry

@Composable
fun RecentEntries(
    entries: List<MoodEntry>,
    moodsMap: Map<String, Mood>,
    modifier: Modifier = Modifier,
    onEntryClick: (MoodEntry) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.recent_entries),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        val validEntries = entries.filter { moodsMap.containsKey(it.moodId) }
        if (validEntries.isEmpty()) {
            Text(
                text = "No entries found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                validEntries.forEach { entry ->
                    moodsMap[entry.moodId]?.let {
                        MoodEntryCard(entry, it, onClick = { onEntryClick(entry) })
                    }
                }
            }
        }
    }
}

@Composable
fun MoodEntryCard(
    entry: MoodEntry,
    mood: Mood,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(mood.color))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, color, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = mood.image,
                contentDescription = mood.localizedName(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mood.localizedName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = entry.getTimeAgo(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                if (entry.note.isNotBlank()) {
                    Text(
                        text = "\"${entry.note}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MoodDisplay(mood: Mood, modifier: Modifier = Modifier) {
    AsyncImage(
        model = mood.image,
        contentDescription = mood.localizedName(),
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
