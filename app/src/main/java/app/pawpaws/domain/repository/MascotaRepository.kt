package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Mascota
import kotlinx.coroutines.flow.StateFlow

interface MascotaRepository {
    val mascotas: StateFlow<List<Mascota>>
    fun save(mascota: Mascota)
    fun findById(id: String): Mascota?
    fun findByUsuario(idUsuario: String): List<Mascota>
    fun delete(id: String)
    fun update(mascota: Mascota)
}