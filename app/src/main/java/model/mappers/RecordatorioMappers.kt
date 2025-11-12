package cl.daeriquelme.appduoc_profe.model.mappers

import cl.daeriquelme.appduoc_profe.data.local.RecordatorioEntity
import cl.daeriquelme.appduoc_profe.model.Recordatorio

fun RecordatorioEntity.toDto() = Recordatorio(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)

fun Recordatorio.toEntity() = RecordatorioEntity(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)
