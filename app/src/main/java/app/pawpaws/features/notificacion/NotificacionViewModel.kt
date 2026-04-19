package app.pawpaws.features.notificacion

import androidx.lifecycle.ViewModel
import app.pawpaws.R
import app.pawpaws.domain.model.models.Notificacion
import app.pawpaws.domain.repository.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotificacionViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(listaFake())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    fun marcarComoLeida(id: String) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == id) it.copy(leida = true) else it
        }
    }

    fun marcarTodasLeidas() {
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
    }

    fun hayNoLeidas(): Boolean = _notificaciones.value.any { !it.leida }

    private fun listaFake() = listOf(
        Notificacion(
            id          = UUID.randomUUID().toString(),
            titulo      = resourceProvider.getString(R.string.notificaciones_fake_title_1),
            descripcion = resourceProvider.getString(R.string.notificaciones_fake_desc_1),
            fecha       = resourceProvider.getString(R.string.notificaciones_fake_time_1),
            leida       = false,
            idUsuario   = "user_1"
        ),
        Notificacion(
            id          = UUID.randomUUID().toString(),
            titulo      = resourceProvider.getString(R.string.notificaciones_fake_title_2),
            descripcion = resourceProvider.getString(R.string.notificaciones_fake_desc_2),
            fecha       = resourceProvider.getString(R.string.notificaciones_fake_time_2),
            leida       = true,
            idUsuario   = "user_1"
        )
    )
}
