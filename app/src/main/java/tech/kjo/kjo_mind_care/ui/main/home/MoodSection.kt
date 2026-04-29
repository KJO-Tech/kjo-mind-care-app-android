package tech.kjo.kjo_mind_care.ui.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.Mood

@Composable
fun MoodSection(
    moods: List<Mood>,
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit,
    onLogMoodClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.mood_question),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(moods) { mood ->
                val isSelected = mood.id == selectedMood?.id
                val scale by animateFloatAsState(if (isSelected) 1.1f else 1.0f)
                val alpha by animateFloatAsState(if (selectedMood == null || isSelected) 1.0f else 0.6f)

                val color = try {
                    Color(android.graphics.Color.parseColor(mood.color))
                } catch (e: IllegalArgumentException) {
                    MaterialTheme.colorScheme.primary
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                ) {
                    Card(
                        onClick = { onMoodSelected(mood) },
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMood == null) color else if (isSelected) color else color.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = mood.image,
                                contentDescription = mood.localizedName(),
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onLogMoodClicked,
                enabled = selectedMood != null,
            ) {
                Text(text = stringResource(R.string.log_mood_button))
            }
        }

    }
}
