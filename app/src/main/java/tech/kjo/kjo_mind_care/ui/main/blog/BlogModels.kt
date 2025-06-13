package tech.kjo.kjo_mind_care.ui.main.blog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tech.kjo.kjo_mind_care.R
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// TODO: Refactorizar este archivo a su propio paquete de model y también los nombres de los atributos y/o clases

// Modelo de Usuario (creador del blog/comentario)
data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val profileImageUrl: String? = null // URL de la imagen de perfil
)

// Modelo de Blog Post
data class BlogPost(
    val id: String,
    val title: String,
    val content: String,
    val author: User,
    val createdAt: LocalDateTime,
    val mediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean = false
) {
    @Composable // Ahora es un composable porque usa stringResource
    fun getTimeAgo(): String {
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(createdAt, now)
        val hours = ChronoUnit.HOURS.between(createdAt, now)
        val days = ChronoUnit.DAYS.between(createdAt, now)
        val months = ChronoUnit.MONTHS.between(createdAt, now)
        val years = ChronoUnit.YEARS.between(createdAt, now)

        return when {
            years > 0 -> stringResource(
                if (years == 1L) R.string.time_ago_year else R.string.time_ago_years,
                years
            )

            months > 0 -> stringResource(
                if (months == 1L) R.string.time_ago_month else R.string.time_ago_months,
                months
            )

            days > 0 -> stringResource(
                if (days == 1L) R.string.time_ago_day else R.string.time_ago_days,
                days
            )

            hours > 0 -> stringResource(
                if (hours == 1L) R.string.time_ago_hour else R.string.time_ago_hours,
                hours
            )

            minutes > 0 -> stringResource(
                if (minutes == 1L) R.string.time_ago_minute else R.string.time_ago_minutes,
                minutes
            )

            else -> stringResource(R.string.time_ago_now)
        }
    }
}


enum class MediaType {
    IMAGE, VIDEO
}

// Modelo de Comentario
data class Comment(
    val id: String,
    val author: User,
    val content: String,
    val createdAt: LocalDateTime,
    val isMine: Boolean = false,
    val replies: List<Comment> = emptyList()
) {
    @Composable
    fun getTimeAgo(): String {
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(createdAt, now)
        val hours = ChronoUnit.HOURS.between(createdAt, now)
        val days = ChronoUnit.DAYS.between(createdAt, now)
        val months = ChronoUnit.MONTHS.between(createdAt, now)
        val years = ChronoUnit.YEARS.between(createdAt, now)

        return when {
            years > 0 -> stringResource(
                if (years == 1L) R.string.time_ago_year else R.string.time_ago_years,
                years
            )

            months > 0 -> stringResource(
                if (months == 1L) R.string.time_ago_month else R.string.time_ago_months,
                months
            )

            days > 0 -> stringResource(
                if (days == 1L) R.string.time_ago_day else R.string.time_ago_days,
                days
            )

            hours > 0 -> stringResource(
                if (hours == 1L) R.string.time_ago_hour else R.string.time_ago_hours,
                hours
            )

            minutes > 0 -> stringResource(
                if (minutes == 1L) R.string.time_ago_minute else R.string.time_ago_minutes,
                minutes
            )

            else -> stringResource(R.string.time_ago_now)
        }
    }
}
