package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Revision
import kotlinx.coroutines.flow.StateFlow

interface RevisionRepository {
    val revisiones: StateFlow<List<Revision>>
    fun save(revision: Revision)
    fun findByPublicacion(idPublicacion: String): List<Revision>
}