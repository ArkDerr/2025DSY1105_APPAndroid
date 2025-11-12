package cl.daeriquelme.appduoc_profe.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.daeriquelme.appduoc_profe.data.remote.dto.UsuarioDto
import cl.daeriquelme.appduoc_profe.data.repository.UsuarioRepository
import cl.daeriquelme.appduoc_profe.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class RegistrarseUiState(
    val rut: String = "",
    val nombre: String = "",
    val rol: String = "",         // se convierte a Int
    val email: String = "",
    val password: String = "",
    val imagenFile: File? = null, // archivo temporal de cámara o galería (ya comprimido)
    val loading: Boolean = false,
    val ok: Boolean = false,
    val msg: String? = null
)

class RegistrarseViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(RegistrarseUiState())
    val ui: StateFlow<RegistrarseUiState> = _ui

    fun onRut(v: String) = _ui.update { it.copy(rut = v) }
    fun onNombre(v: String) = _ui.update { it.copy(nombre = v) }
    fun onRol(v: String) = _ui.update { it.copy(rol = v) }
    fun onEmail(v: String) = _ui.update { it.copy(email = v) }
    fun onPass(v: String) = _ui.update { it.copy(password = v) }
    fun onImagenFile(f: File?) = _ui.update { it.copy(imagenFile = f) }
    fun consumeMsg() = _ui.update { it.copy(msg = null) }

    fun registrar() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, ok = false, msg = null) }

        try {
            // 1) Registrar usuario en Firebase
            val firebaseUser = authRepo.signUp(_ui.value.email, _ui.value.password)
                ?: throw IllegalStateException("No se pudo registrar en Firebase")
            val uid = firebaseUser.uid!!

            // 2) Enviar datos al backend (POST sin imagen)
            val dto = UsuarioDto(
                rut = _ui.value.rut,
                nombre = _ui.value.nombre,
                mail = _ui.value.email,
                password = _ui.value.password,
                idrol = _ui.value.rol.toIntOrNull() ?: 1,
                idfirebase = uid
            )
            val ok = userRepo.crearUsuario(dto)
            if (!ok) throw IllegalStateException("Fallo al guardar usuario en backend")

            // 3) Subir imagen si existe (PUT multipart)
            _ui.value.imagenFile?.let { file ->
                userRepo.subirImagen(
                    rut = _ui.value.rut,
                    idFirebase = uid,
                    file = file
                )
            }

            _ui.update { it.copy(loading = false, ok = true, msg = "Registro exitoso") }

        } catch (e: Exception) {
            _ui.update {
                it.copy(
                    loading = false,
                    ok = false,
                    msg = "Error al registrar: ${e.message}"
                )
            }
        }
    }
}
