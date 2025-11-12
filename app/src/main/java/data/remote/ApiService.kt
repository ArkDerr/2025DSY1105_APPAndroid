package cl.daeriquelme.appduoc_profe.data.remote

import cl.daeriquelme.appduoc_profe.data.remote.dto.FeriadoResponse
import retrofit2.http.GET
import cl.daeriquelme.appduoc_profe.data.remote.dto.UsuarioDto
import cl.daeriquelme.appduoc_profe.data.remote.dto.UsuarioResp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("holidays.json")
    suspend fun getFeriados(): FeriadoResponse

}