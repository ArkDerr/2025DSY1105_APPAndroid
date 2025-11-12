package cl.daeriquelme.appduoc_profe.ui.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.daeriquelme.appduoc_profe.data.media.MediaRepository
import cl.daeriquelme.appduoc_profe.repository.auth.FirebaseAuthDataSource
import cl.daeriquelme.appduoc_profe.ui.profile.ProfileViewModel

class ProfileVMFactory(
    private val authDs: FirebaseAuthDataSource,
    private val mediaRepo: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = when (modelClass) {
            ProfileViewModel::class.java -> ProfileViewModel(authDs, mediaRepo)
            else -> error("VM no soportado: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}
