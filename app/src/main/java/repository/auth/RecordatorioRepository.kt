package cl.daeriquelme.appduoc_profe.repository

import cl.daeriquelme.appduoc_profe.data.local.ReminderDao
import cl.daeriquelme.appduoc_profe.model.Recordatorio
import cl.daeriquelme.appduoc_profe.model.mappers.toDto
import cl.daeriquelme.appduoc_profe.model.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordatorioRepository(
    private val dao: ReminderDao
) {
    fun observe(uid: String): Flow<List<Recordatorio>> =
        dao.observeByUid(uid).map { list -> list.map { it.toDto() } }

    suspend fun insert(recordatorio: Recordatorio): Long = dao.insert(recordatorio.toEntity())

    suspend fun update(recordatorio: Recordatorio) = dao.update(recordatorio.toEntity())

    suspend fun delete(recordatorio: Recordatorio) = dao.delete(recordatorio.toEntity())

    suspend fun findById(id: Long): Recordatorio? = dao.findById(id)?.toDto()
}
