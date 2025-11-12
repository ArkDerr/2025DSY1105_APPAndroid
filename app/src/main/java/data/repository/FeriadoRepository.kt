package cl.daeriquelme.appduoc_profe.data.repository

import cl.daeriquelme.appduoc_profe.data.remote.RetrofitClient
import cl.daeriquelme.appduoc_profe.model.Feriado

class FeriadoRepository {

    suspend fun obtenerFeriados(): List<Feriado> {
        val resp = RetrofitClient.api.getFeriados()
        return resp.data.map {
            Feriado(
                date = it.date,
                title = it.title,
                type = it.type,
                inalienable = it.inalienable,
                extra = it.extra
            )
        }
    }
}
