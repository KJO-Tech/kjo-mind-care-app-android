package tech.kjo.kjo_mind_care.data.model

data class Emergency(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val video: String = "",
    val contactos: List<String> = emptyList(),
    val links: List<String> = emptyList()
)