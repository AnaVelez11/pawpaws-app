package app.pawpaws.domain.usecase

import app.pawpaws.domain.model.models.Mensaje
import app.pawpaws.domain.repository.MensajeRepository
import javax.inject.Inject

class ObtenerMensajesUseCase @Inject constructor(
    private val repository: MensajeRepository
) {
    /**
     * Retorna Triple(activos, noLeidos, archivados)
     */
    fun execute(): Triple<List<Mensaje>, List<Mensaje>, List<Mensaje>> {
        val todos = repository.mensajes.value
        return Triple(
            todos.filter  { !it.archivado },
            todos.filter  { !it.archivado && !it.leido },
            todos.filter  { it.archivado }
        )
    }
}