package app.pawpaws.domain.usecase

import app.pawpaws.domain.repository.PerfilRepository
import javax.inject.Inject

class ObtenerPerfilDataUseCase @Inject constructor(
    private val repository: PerfilRepository
) {
    fun execute() = Pair(
        repository.getStats(),
        repository.getInsignias()
    )
}