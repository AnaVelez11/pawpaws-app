package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.Insignia
import app.pawpaws.domain.model.models.PerfilStats
import app.pawpaws.domain.repository.PerfilRepository
import java.util.UUID
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor() : PerfilRepository {

    override fun getStats(): PerfilStats {
        return PerfilStats(
            activas = 12,
            pendientes = 5,
            completadas = 48,
            rechazadas = 2
        )
    }

    override fun getInsignias(): List<Insignia> {
        return listOf(
            Insignia(UUID.randomUUID().toString(), "Dog Lover", ""),
            Insignia(UUID.randomUUID().toString(), "Rescatista", ""),
            Insignia(UUID.randomUUID().toString(), "Voluntario", ""),
            Insignia(UUID.randomUUID().toString(), "Protector", "")
        )
    }
}