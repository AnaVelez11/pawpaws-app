package app.pawpaws.features.mensaje

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.model.models.BurbujaMensaje
import app.pawpaws.domain.model.models.Mensaje
import app.pawpaws.domain.repository.MensajeRepository
import app.pawpaws.domain.usecase.ObtenerMensajesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MensajesViewModel @Inject constructor(
    private val useCase: ObtenerMensajesUseCase,
    private val repository: MensajeRepository,

) : ViewModel() {

    private val _activos    = MutableStateFlow<List<Mensaje>>(emptyList())
    val activos:    StateFlow<List<Mensaje>> = _activos.asStateFlow()

    private val _noLeidos   = MutableStateFlow<List<Mensaje>>(emptyList())
    val noLeidos:   StateFlow<List<Mensaje>> = _noLeidos.asStateFlow()

    private val _archivados = MutableStateFlow<List<Mensaje>>(emptyList())
    val archivados: StateFlow<List<Mensaje>> = _archivados.asStateFlow()

    // Chat abierto
    private val _conversacion = MutableStateFlow<List<BurbujaMensaje>>(emptyList())
    val conversacion: StateFlow<List<BurbujaMensaje>> = _conversacion.asStateFlow()

    private val _chatActual = MutableStateFlow<Mensaje?>(null)
    val chatActual: StateFlow<Mensaje?> = _chatActual.asStateFlow()

    init { cargar() }

    fun cargar() {
        val (a, n, ar) = useCase.execute()
        _activos.value    = a
        _noLeidos.value   = n
        _archivados.value = ar
    }

    fun abrirChat(chatId: String) {
        val todos = _activos.value + _archivados.value
        _chatActual.value  = todos.firstOrNull { it.chatId == chatId }
        _conversacion.value = repository.getConversacion(chatId)
        // Marcar como leído
        _activos.value = _activos.value.map {
            if (it.chatId == chatId) it.copy(leido = true) else it
        }
        _noLeidos.value = _noLeidos.value.filter { it.chatId != chatId }
    }

    fun enviarMensaje(chatId: String, texto: String) {
        if (texto.isBlank()) return
        repository.enviarMensaje(chatId, texto)
        _conversacion.value = repository.getConversacion(chatId)
        cargar()
    }
}