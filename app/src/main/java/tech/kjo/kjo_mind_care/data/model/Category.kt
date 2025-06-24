package tech.kjo.kjo_mind_care.data.model



data class Category(
    val id: String = "",
    val nameTranslations: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
) {
    fun getLocalizedName(languageCode: String): String {
        return nameTranslations[languageCode] ?:
        nameTranslations["en"] ?:
        nameTranslations.values.firstOrNull() ?:
        "Unnamed Category"
    }
}
