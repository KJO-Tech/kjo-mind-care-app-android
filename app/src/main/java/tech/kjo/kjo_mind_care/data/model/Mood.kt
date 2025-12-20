package tech.kjo.kjo_mind_care.data.model

import androidx.annotation.Keep
import java.util.Locale

@Keep
data class Mood(
    val id: String = "",
    val name: Map<String, String> = emptyMap(),
    val description: Map<String, String> = emptyMap(),
    val image: String = "",
    val color: String = "",
    val isActive: Boolean = false,
    val value: Int = 0
) {
    fun localizedName(): String {
        val currentLanguage = Locale.getDefault().language
        return name[currentLanguage] ?: name["en"] ?: ""
    }

    fun localizedDescription(): String {
        val currentLanguage = Locale.getDefault().language
        return description[currentLanguage] ?: description["en"] ?: ""
    }
}
