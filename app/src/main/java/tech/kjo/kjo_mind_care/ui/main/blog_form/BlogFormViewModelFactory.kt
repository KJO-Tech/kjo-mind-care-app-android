package tech.kjo.kjo_mind_care.ui.main.blog_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

// Factory para inyectar savedStateHandle y el blogId
class BlogFormViewModelFactory(private val blogId: String? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BlogFormViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle() // Obtener SavedStateHandle
            savedStateHandle["blogId"] = blogId // Pasar el blogId a SavedStateHandle
            @Suppress("UNCHECKED_CAST")
            return BlogFormViewModel(savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}