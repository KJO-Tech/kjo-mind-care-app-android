package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import tech.kjo.kjo_mind_care.R

@Composable
fun MoodEntryDetail(
    onCancel: () -> Unit = {},
    onSave: (mood: MoodOption?, note: String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
//            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextButton(onClick = onCancel) {
//                Text("Atras", color = MaterialTheme.colorScheme.primary)
//            }
//            Spacer(modifier = Modifier.weight(1f))
//        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        val moodOptionResources = listOf(
            MoodOptionResource(
                R.string.mood_joyful_title,
                R.string.mood_joyful_desc,
                R.drawable.ic_mood_joyful
            ),
            MoodOptionResource(
                R.string.mood_neutral_title,
                R.string.mood_neutral_desc,
                R.drawable.ic_mood_neutral
            ),
            MoodOptionResource(
                R.string.mood_tired_title,
                R.string.mood_tired_desc,
                R.drawable.ic_mood_tired
            ),
            MoodOptionResource(
                R.string.mood_sad_title,
                R.string.mood_sad_desc,
                R.drawable.ic_mood_sad
            ),
            MoodOptionResource(
                R.string.mood_anxious_title,
                R.string.mood_anxious_desc,
                R.drawable.ic_mood_anxious
            ),
            MoodOptionResource(
                R.string.mood_angry_title,
                R.string.mood_angry_desc,
                R.drawable.ic_mood_angry
            ),
            MoodOptionResource(
                R.string.mood_frustrated_title,
                R.string.mood_frustrated_desc,
                R.drawable.ic_mood_frustrated
            )
        )
        val moodOptions = moodOptionResources.map {
            MoodOption(
                title = stringResource(it.titleRes),
                description = stringResource(it.descRes),
                iconResId = it.iconRes
            )
        }
        var selectedMood by remember { mutableStateOf<MoodOption?>(null) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(moodOptions) { mood ->
                MoodOptionItem(
                    moodOption = mood,
                    isSelected = mood == selectedMood,
                    onSelect = { selectedMood = mood }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        var noteText by remember { mutableStateOf(TextFieldValue("")) }

        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = {
                Text("How are you feeling today? What's on your mind?")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
//            colors = TextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                cursorColor = MaterialTheme.colorScheme.primary,
//                focusedContainerColor = MaterialTheme.colorScheme.surface,
//                unfocusedContainerColor = MaterialTheme.colorScheme.surface
//            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onSave(selectedMood, noteText.text)
                },
                enabled = selectedMood != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}


@Composable
private fun MoodOptionItem(
    moodOption: MoodOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = moodOption.iconResId),
                contentDescription = moodOption.title,
                modifier = Modifier.size(26.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = moodOption.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = moodOption.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2
            )
        }
    }
}