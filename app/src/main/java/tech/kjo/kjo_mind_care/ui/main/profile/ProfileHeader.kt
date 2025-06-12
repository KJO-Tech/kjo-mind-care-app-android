package tech.kjo.kjo_mind_care.ui.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R

@Composable
fun ProfileHeader(
    photoUrl: String,
    name: String,
    email: String,
    onEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(98.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.titleMedium)
        Text(email, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onEditProfile) {
            Text(stringResource(R.string.edit_profile))
        }
    }
}