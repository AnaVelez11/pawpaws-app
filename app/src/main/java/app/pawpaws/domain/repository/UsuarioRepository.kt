package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UsuarioRepository {
    val usuarios: StateFlow<List<Usuario>>
    fun save(usuario: Usuario)
    fun findById(id: String): Usuario?
    fun login(email: String, password: String): Usuario?
    fun update(usuario: Usuario)
    fun delete(userId: String)
}