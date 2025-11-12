package cl.daeriquelme.appduoc_profe.model

data class Recordatorio(
    val id: Long = 0L,
    val uid: String,
    val createdAt: String,
    val message: String
)
