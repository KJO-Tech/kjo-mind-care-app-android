package tech.kjo.kjo_mind_care.ui.main.blog_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class BlogDetailViewModelFactory(private val blogId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BlogDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            savedStateHandle["blogId"] = blogId // Pasar el blogId
            @Suppress("UNCHECKED_CAST")
            return BlogDetailViewModel(savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}