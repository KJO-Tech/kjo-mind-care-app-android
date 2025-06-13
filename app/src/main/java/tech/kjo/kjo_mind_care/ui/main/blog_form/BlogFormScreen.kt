package tech.kjo.kjo_mind_care.ui.main.blog_form

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.BlogStatus
import tech.kjo.kjo_mind_care.data.model.Category
import tech.kjo.kjo_mind_care.data.model.MediaType
import tech.kjo.kjo_mind_care.utils.getCurrentLanguageCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogFormScreen(
    blogId: String?,
    onBlogSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: BlogFormViewModel = viewModel(
        factory = BlogFormViewModelFactory(blogId = blogId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val currentLanguageCode = getCurrentLanguageCode()

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val mediaType =
                    if (context.contentResolver.getType(uri)?.startsWith("video") == true) {
                        MediaType.VIDEO
                    } else {
                        MediaType.IMAGE
                    }
                viewModel.onMediaSelected(uri, mediaType)
            }
        }
    )

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(
                message = if (uiState.blogId == null) context.getString(R.string.blog_created_success) else context.getString(
                    R.string.blog_updated_success
                ),
                withDismissAction = true
            )
            onBlogSaved()
            viewModel.resetSaveSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.blogId == null) stringResource(R.string.create_blog_title) else stringResource(
                            R.string.edit_blog_title
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar formulario")
                    }
                },
                windowInsets = if (uiState.blogId != null) WindowInsets(0.dp) else TopAppBarDefaults.windowInsets
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text(stringResource(R.string.blog_title_hint)) },
                    isError = uiState.titleError != null,
                    supportingText = { if (uiState.titleError != null) Text(uiState.titleError!!) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = viewModel::onContentChange,
                    label = { Text(stringResource(R.string.blog_content_hint)) },
                    isError = uiState.contentError != null,
                    supportingText = { if (uiState.contentError != null) Text(uiState.contentError!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                )
                Spacer(Modifier.height(16.dp))

                // Selector de Categoría (usando ExposedDropdownMenuBox)
                CategoryDropdownSelector(
                    selectedCategoryId = uiState.selectedCategoryId,
                    availableCategories = uiState.availableCategories,
                    onCategorySelected = { categoryId -> viewModel.onCategorySelected(categoryId) },
                    errorMessage = uiState.categoryError,
                    currentLanguageCode = currentLanguageCode
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    stringResource(R.string.blog_media_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            mediaPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(stringResource(R.string.select_media_button))
                    }
                    if (uiState.mediaUri != null || uiState.existingMediaUrl != null) {
                        Spacer(Modifier.size(8.dp))
                        OutlinedButton(onClick = viewModel::onClearMedia) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(R.string.clear_media_button))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                val mediaToDisplay =
                    uiState.mediaUri ?: uiState.existingMediaUrl?.let { Uri.parse(it) }
                if (mediaToDisplay != null) {
                    AsyncImage(
                        model = mediaToDisplay,
                        contentDescription = "Vista previa del medio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (uiState.blogId != null) {
                    Text(
                        stringResource(R.string.blog_status_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BlogStatus.entries.forEach { status ->
                            Row(
                                modifier = Modifier.weight(1f), // Distribuir el espacio equitativamente
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.status == status,
                                    onClick = { viewModel.onStatusSelected(status) }
                                )
                                Text(status.getLocalizedName())
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = viewModel::saveBlog,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            if (uiState.blogId == null) stringResource(R.string.publish_button) else stringResource(
                                R.string.save_changes_button
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownSelector(
    selectedCategoryId: String?,
    availableCategories: List<Category>,
    onCategorySelected: (String) -> Unit,
    errorMessage: String?,
    currentLanguageCode: String
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedCategoryName = availableCategories.find { it.id == selectedCategoryId }
        ?.getLocalizedName(currentLanguageCode)
        ?: stringResource(R.string.select_category_placeholder)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategoryName,
            onValueChange = { /* No-op, solo para mostrar */ },
            readOnly = true,
            label = { Text(stringResource(R.string.blog_category_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
            isError = errorMessage != null,
            supportingText = { if (errorMessage != null) Text(errorMessage) },
            modifier = Modifier
                .menuAnchor() // Importante para anclar el menú al TextField
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.getLocalizedName(currentLanguageCode)) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}