package cl.daeriquelme.appduoc_profe.ui.vmfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.daeriquelme.appduoc_profe.data.local.AppDatabase
import cl.daeriquelme.appduoc_profe.repository.RecordatorioRepository
import cl.daeriquelme.appduoc_profe.ui.recordatorio.RecordatorioViewModel

class RecordatorioVMFactory(
    context: Context,
    private val uid: String
) : ViewModelProvider.Factory {

    private val repo by lazy {
        val db = AppDatabase.get(context)
        RecordatorioRepository(db.reminderDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordatorioViewModel(repo, uid) as T
    }
}
