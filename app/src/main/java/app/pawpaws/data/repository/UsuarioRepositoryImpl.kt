package app.pawpaws.data.repository

import app.pawpaws.core.utils.resources.ImageResources
import app.pawpaws.domain.model.enums.EstadoUsuario
import app.pawpaws.domain.model.enums.Rol
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor() : UsuarioRepository {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    override val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    init {
        _usuarios.value = seed()
    }

    override fun save(usuario: Usuario) {
        _usuarios.value += usuario
    }

    override fun findById(id: String): Usuario? =
        _usuarios.value.firstOrNull { it.id == id }

    override fun login(email: String, password: String): Usuario? =
        _usuarios.value.firstOrNull { it.email == email && it.password == password }

    override fun update(usuario: Usuario) {
        _usuarios.value = _usuarios.value.map { if (it.id == usuario.id) usuario else it }
    }

    override fun delete(userId: String) {
        _usuarios.value = _usuarios.value.filter { it.id != userId }
    }

    private fun seed(): List<Usuario> = listOf(
        Usuario(
            id = "1",
            nombre = "Juan Galvis",
            email = "juan@pawpaws.com",
            password = "111111",
            telefono = "3001234567",
            fotoPerfil = ImageResources.USER_JUAN,
            rol = Rol.USER,
            estado = EstadoUsuario.ACTIVO
        ),
        Usuario(
            id = "2",
            nombre = "Ana Vélez",
            email = "ana@pawpaws.com",
            password = "222222",
            telefono = "3109876543",
            fotoPerfil = ImageResources.USER_ANA,
            rol = Rol.USER,
            estado = EstadoUsuario.ACTIVO
        ),
        Usuario(
            id = "3",
            nombre = "Isabella García",
            email = "admin@pawpaws.com",
            password = "333333",
            telefono = "3205551234",
            fotoPerfil = ImageResources.USER_ADMIN,
            rol = Rol.ADMIN,
            estado = EstadoUsuario.ACTIVO
        )
    )
}