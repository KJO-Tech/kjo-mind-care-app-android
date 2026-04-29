package tech.kjo.kjo_mind_care.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tech.kjo.kjo_mind_care.R

enum class BlogStatus {
    PENDING,
    PUBLISHED,
    DELETED;

    @Composable
    fun getLocalizedName(): String {
        return when (this) {
            PENDING -> stringResource(R.string.blog_status_pending)
            PUBLISHED -> stringResource(R.string.blog_status_published)
            DELETED -> stringResource(R.string.blog_status_deleted)
        }
    }
}