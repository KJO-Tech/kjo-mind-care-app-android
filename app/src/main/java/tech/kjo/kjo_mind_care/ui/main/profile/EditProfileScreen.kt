package tech.kjo.kjo_mind_care.ui.main.profile


import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.ui.components.PermissionDialog
import tech.kjo.kjo_mind_care.utils.PermissionUtils


@Composable
fun EditProfileScreen(
    onProfileSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    photoUrl: String = "",
    name: String = "",
    email: String = ""
) {
    var currentPhotoUrl by remember { mutableStateOf(photoUrl) }
    var currentName by remember { mutableStateOf(name) }
    var currentEmail by remember { mutableStateOf(email) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            currentPhotoUrl = it.toString()
        }
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasAnyPermission = permissions.values.any { it }

        if (hasAnyPermission) {
            imagePickerLauncher.launch("image/*")
        } else {
            println("DEBUG: All permissions denied")
        }
    }

    val singlePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            println("DEBUG: Permission denied")
        }
    }

    fun requestImagePermission() {
        val hasPermission = PermissionUtils.hasImagePermission(context)

        if (hasPermission) {
            imagePickerLauncher.launch("image/*")
        } else {
            showPermissionDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = currentPhotoUrl.ifEmpty { R.drawable.ic_mood_joyful },
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { requestImagePermission() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tap_to_change_photo),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = currentName,
            onValueChange = { currentName = it },
            label = { Text(stringResource(R.string.name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancel_button))
            }

            Button(
                onClick = onProfileSaved,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save_button))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    PermissionDialog(
        showDialog = showPermissionDialog,
        title = stringResource(R.string.permission_needed_title),
        message = stringResource(R.string.image_permission_message),
        onDismiss = { showPermissionDialog = false },
        onConfirm = {
            showPermissionDialog = false

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    multiplePermissionLauncher.launch(PermissionUtils.getImagePermissions())
                }
                else -> {
                    singlePermissionLauncher.launch(PermissionUtils.getPrimaryImagePermission())
                }
            }
        },
        confirmText = stringResource(R.string.grant_permission),
        showCancel = true,
        cancelText = stringResource(R.string.cancel_button)
    )
}