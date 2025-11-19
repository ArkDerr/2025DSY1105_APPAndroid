package cl.daeriquelme.appduoc_profe

import cl.daeriquelme.appduoc_profe.data.remote.ApiBackendService
import cl.daeriquelme.appduoc_profe.data.remote.dto.UsuarioDto
import cl.daeriquelme.appduoc_profe.data.remote.dto.UsuarioResp
import cl.daeriquelme.appduoc_profe.data.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class UsuarioRepositoryTest : StringSpec({

    val mockApi = mockk<ApiBackendService>()

    // ----------------------------------------------------------------------
    // crearUsuario()
    // ----------------------------------------------------------------------
    "crearUsuario debe devolver true cuando la API responde 200" { //Nombre test

        val dto = UsuarioDto( //Objeto a guardar
            rut = "1-9",
            nombre = "Daniel",
            mail = "test@mail.com",
            password = "1234",
            idrol = 2,
            idfirebase = "abc123"
        )

        coEvery { //Simula esta función suspend y devuelve lo que yo quiera. 200 OK
            mockApi.crearUsuario(dto)
        } returns Response.success(
            ResponseBody.create("text/plain".toMediaTypeOrNull(), "OK")
        )

        val repo = UsuarioRepository(api = mockApi) //Toda llamada llama al mock

        val result = repo.crearUsuario(dto) // Ejecuta la función a probar

        result shouldBe true //Assert valida que la creación de usuario devuelva true
    }

    // ----------------------------------------------------------------------
    // cargarPorFirebase()
    // ----------------------------------------------------------------------
    "cargarPorFirebase debe retornar UsuarioResp cuando la API responde 200" {

        val resp = UsuarioResp( //Objeto de respuesta esperado
            rut = "1-9",
            nombre = "Daniel",
            mail = "test@mail.com",
            idrol = 2,
            idfirebase = "abc123",
            imagen = null
        )

        coEvery {
            mockApi.getByFirebase("abc123") // Cuando busquen por idfirebase
        } returns Response.success(resp) //Devuelve 200 OK y el objeto resp

        val repo = UsuarioRepository(api = mockApi)

        val result = repo.cargarPorFirebase("abc123")

        result shouldBe resp
    }

    // ----------------------------------------------------------------------
    // actualizarNombre()
    // ----------------------------------------------------------------------
    "actualizarNombre debe retornar UsuarioResp cuando la API responde 200" {

        val resp = UsuarioResp(
            rut = "1-9",
            nombre = "NuevoNombre",
            mail = "mail@test.com",
            idrol = 2,
            idfirebase = "abc123",
            imagen = null
        )

        coEvery {
            mockApi.updateNombre("1-9", mapOf("nombre" to "NuevoNombre"))
        } returns Response.success(resp)

        val repo = UsuarioRepository(api = mockApi)

        val result = repo.actualizarNombre("1-9", "NuevoNombre")

        result shouldBe resp
    }

    // ----------------------------------------------------------------------
    // obtenerUsuarioPorFirebase()
    // ----------------------------------------------------------------------
    "obtenerUsuarioPorFirebase debe retornar null si la API devuelve error" {

        val errorBody = ResponseBody.create(
            "text/plain".toMediaTypeOrNull(),
            "Usuario no encontrado"
        )

        coEvery {
            mockApi.getByFirebase("abc123")
        } returns Response.error(404, errorBody)

        val repo = UsuarioRepository(api = mockApi)

        val result = repo.obtenerUsuarioPorFirebase("abc123")

        result shouldBe null
    }

    // ----------------------------------------------------------------------
    // subirImagen()
    // ----------------------------------------------------------------------
    "subirImagen debe retornar true cuando la API responde exitoso" {

        val tempFile = File.createTempFile("testimg", ".tmp")

        coEvery {
            mockApi.actualizarUsuarioConImagen(any(), any(), any())
        } returns Response.success(
            ResponseBody.create("text/plain".toMediaTypeOrNull(), "Usuario actualizado")
        )

        val repo = UsuarioRepository(api = mockApi)

        val result = repo.subirImagen("1-9", "abc123", tempFile)

        result shouldBe true
    }
})
