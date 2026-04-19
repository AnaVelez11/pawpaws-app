package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.Revision
import app.pawpaws.domain.repository.RevisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RevisionRepositoryImpl @Inject constructor() : RevisionRepository {
    private val _revisiones = MutableStateFlow<List<Revision>>(emptyList())
    override val revisiones: StateFlow<List<Revision>> = _revisiones.asStateFlow()

    override fun save(revision: Revision) { _revisiones.value += revision }
    override fun findByPublicacion(idPublicacion: String): List<Revision> =
        _revisiones.value.filter { it.idPublicacion == idPublicacion }
}