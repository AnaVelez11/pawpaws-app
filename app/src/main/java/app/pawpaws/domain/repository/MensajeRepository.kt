package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.BurbujaMensaje
import app.pawpaws.domain.model.models.Mensaje
import kotlinx.coroutines.flow.StateFlow

interface MensajeRepository {
    val mensajes: StateFlow<List<Mensaje>>
    fun getConversacion(chatId: String): List<BurbujaMensaje>
    fun enviarMensaje(chatId: String, texto: String)

}