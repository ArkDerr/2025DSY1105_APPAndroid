package cl.daeriquelme.appduoc_profe.ui.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.daeriquelme.appduoc_profe.model.Producto
import cl.daeriquelme.appduoc_profe.model.productosDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 🔽 imports nuevos:
import com.google.firebase.auth.FirebaseAuth
import cl.daeriquelme.appduoc_profe.data.repository.UsuarioRepository

data class PrincipalUiState(
    val email: String? = null,
    val nombre: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel : ViewModel() {

    // ---------- Estado general ----------
    private val _ui = MutableStateFlow(PrincipalUiState())
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    // ---------- Fuente y filtros ----------
    private val fuente: List<Producto> = productosDemo
    val categorias: List<String> = listOf("Todos") + fuente.map { it.categoria }.distinct()
    private val _categoriaSel = MutableStateFlow("Todos")
    val categoriaSel: StateFlow<String> = _categoriaSel.asStateFlow()
    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    // ---------- Repositorio backend ----------
    private val userRepo = UsuarioRepository()

    init {
        // Al iniciar, toma el usuario actual de Firebase y trata de traer su "nombre" desde el backend
        viewModelScope.launch {
            try {
                val fbUser = FirebaseAuth.getInstance().currentUser
                val uid = fbUser?.uid
                val email = fbUser?.email

                // Actualiza el email de inmediato para que no quede nulo
                _ui.value = _ui.value.copy(email = email)

                if (uid != null) {
                    // Llama al backend: GET /api/v1/Usuarios/firebase/{idFirebase}
                    val resp = userRepo.obtenerUsuarioPorFirebase(uid)
                    // Actualiza nombre si viene
                    _ui.value = _ui.value.copy(nombre = resp?.nombre)
                }
            } catch (e: Exception) {
                // Si falla, no botamos la app; solo dejamos registro en error
                _ui.value = _ui.value.copy(error = e.message ?: "No fue posible cargar el nombre")
            }
        }
    }

    // ---------- Acciones ----------
    fun setCategoria(cat: String) {
        _categoriaSel.value = cat
        aplicarFiltro()
    }

    /** Carga/recarga la grilla (desde productosDemo). */
    fun cargarProductos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                aplicarFiltro()
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(error = e.message ?: "Error al cargar productos")
            } finally {
                _ui.value = _ui.value.copy(loading = false)
            }
        }
    }

    /** Reset al tocar Inicio: categoría base + recarga. */
    fun refreshHome() {
        _categoriaSel.value = "Todos"
        cargarProductos()
    }

    fun logout() {
        _ui.value = _ui.value.copy(loading = true)
        viewModelScope.launch {
            // Tu lógica real de logout iría aquí
            _ui.value = _ui.value.copy(loading = false, loggedOut = true)
        }
    }

    // ---------- Helper ----------
    private fun aplicarFiltro() {
        val cat = _categoriaSel.value
        _productosFiltrados.value = if (cat == "Todos") {
            fuente
        } else {
            fuente.filter { it.categoria == cat }
        }
    }
}
