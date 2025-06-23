package tech.kjo.kjo_mind_care.utils

import android.content.Context
import tech.kjo.kjo_mind_care.R
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object TimeAgoFormatter {

    fun format(dateTime: LocalDateTime, languageCode: String): String {
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now)
        val days = ChronoUnit.DAYS.between(dateTime, now)
        val months = ChronoUnit.MONTHS.between(dateTime, now)
        val years = ChronoUnit.YEARS.between(dateTime, now)

        val context: Context = StaticBlogData.applicationContext ?: return ""

        return when {
            minutes < 1 -> context.getString(R.string.time_ago_now)
            minutes < 60 -> if (minutes == 1L) context.getString(
                R.string.time_ago_minutes,
                minutes.toInt()
            ) else context.getString(R.string.time_ago_minutes, minutes.toInt())

            hours < 24 -> if (hours == 1L) context.getString(
                R.string.time_ago_hours,
                hours.toInt()
            ) else context.getString(R.string.time_ago_hours, hours.toInt())

            days < 30 -> if (days == 1L) context.getString(
                R.string.time_ago_days,
                days.toInt()
            ) else context.getString(R.string.time_ago_days, days.toInt())

            months < 12 -> if (months == 1L) context.getString(
                R.string.time_ago_months,
                months.toInt()
            ) else context.getString(R.string.time_ago_months, months.toInt())

            else -> if (years == 1L) context.getString(
                R.string.time_ago_years,
                years.toInt()
            ) else context.getString(R.string.time_ago_years, years.toInt())
        }
    }
}