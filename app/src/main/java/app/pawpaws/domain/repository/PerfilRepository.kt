package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Insignia
import app.pawpaws.domain.model.models.PerfilStats

interface PerfilRepository {
    fun getStats(): PerfilStats
    fun getInsignias(): List<Insignia>
}