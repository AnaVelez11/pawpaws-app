package app.pawpaws.data.repository

import app.pawpaws.core.utils.resources.ImageResources
import app.pawpaws.domain.model.models.BurbujaMensaje
import app.pawpaws.domain.model.models.Mensaje
import app.pawpaws.domain.repository.MensajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MensajeRepositoryImpl @Inject constructor() : MensajeRepository {

    private val _mensajes = MutableStateFlow<List<Mensaje>>(seedMensajes())
    override val mensajes: StateFlow<List<Mensaje>> = _mensajes.asStateFlow()

    // Conversaciones quemadas por chatId
    private val conversaciones = mutableMapOf(
        "chat_1" to seedConversacion1(),
        "chat_2" to seedConversacion2(),
        "chat_3" to seedConversacion3()
    )

    override fun getConversacion(chatId: String): List<BurbujaMensaje> =
        conversaciones[chatId] ?: emptyList()

    override fun enviarMensaje(chatId: String, texto: String) {
        val nueva = BurbujaMensaje(
            id     = UUID.randomUUID().toString(),
            texto  = texto,
            esMio  = true,
            hora   = horaActual(),
            leido  = false
        )
        val actual = conversaciones[chatId]?.toMutableList() ?: mutableListOf()
        actual.add(nueva)
        conversaciones[chatId] = actual

        // Actualizar último mensaje en la lista
        _mensajes.value = _mensajes.value.map { m ->
            if (m.chatId == chatId) m.copy(ultimoMensaje = texto, leido = true) else m
        }
    }

    private fun horaActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%02d:%02d".format(cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    }

    // ── Seeds ──────────────────────────────────────────────────────────────────

    private fun seedMensajes() = listOf(
        Mensaje(
            chatId        = "chat_1",
            nombre        = "Sarah Jenkins",
            ultimoMensaje = "El golden retriever todavía está disponible para...",
            timestamp     = System.currentTimeMillis() - 300_000,
            leido         = false,
            archivado     = false,
            fotoPerfil    = ImageResources.USER_ANA
        ),
        Mensaje(
            chatId        = "chat_2",
            nombre        = "Carlos Ruiz",
            ultimoMensaje = "¿Puedo pasar mañana a conocerlo?",
            timestamp     = System.currentTimeMillis() - 3_600_000,
            leido         = true,
            archivado     = false,
            fotoPerfil    = ImageResources.USER_JUAN
        ),
        Mensaje(
            chatId        = "chat_3",
            nombre        = "Isabella García",
            ultimoMensaje = "Perfecto, nos vemos allí. ¡Gracias! 😊",
            timestamp     = System.currentTimeMillis() - 86_400_000,
            leido         = true,
            archivado     = true,
            fotoPerfil    = ImageResources.USER_ADMIN
        )
    )

    private fun seedConversacion1() = listOf(
        BurbujaMensaje("m1_1", "¡Hola! He visto vuestra publicación. ¿Sigue disponible el perrito para adopción? 🐾", false, "13:41"),
        BurbujaMensaje("m1_2", "¡Hola! 😊 Sí, todavía está buscando un hogar responsable. Se llama 'Max'.", true, "14:12"),
        BurbujaMensaje("m1_3", "¡Que bien! Me gustaría ir a conocerlo alguna tarde. ¿Sería posible?", false, "14:13"),
        BurbujaMensaje("m1_4", "¡Por supuesto! Estamos en el refugio de 14:00 a 20:00. ¿Te viene bien a las 17:30?", true, "15:48"),
        BurbujaMensaje("m1_5", "Perfecto, nos vemos allí. ¡Gracias! 😊", false, "15:52")
    )

    private fun seedConversacion2() = listOf(
        BurbujaMensaje("m2_1", "Buenas, vi que tienes un gato en adopción.", false, "10:00"),
        BurbujaMensaje("m2_2", "Sí, se llama Luna. Tiene 2 años y es muy tranquila.", true, "10:05"),
        BurbujaMensaje("m2_3", "¿Puedo pasar mañana a conocerlo?", false, "10:10"),
        BurbujaMensaje("m2_4", "Claro, ¿a qué hora te queda bien?", true, "10:15")
    )

    private fun seedConversacion3() = listOf(
        BurbujaMensaje("m3_1", "Hola, encontré a tu perro cerca del parque.", false, "09:00"),
        BurbujaMensaje("m3_2", "¡Gracias! ¿Puedes enviarme una foto?", true, "09:05"),
        BurbujaMensaje("m3_3", "Claro, ahora te la mando.", false, "09:06"),
        BurbujaMensaje("m3_4", "Perfecto, nos vemos allí. ¡Gracias! 😊", true, "09:20")
    )
}