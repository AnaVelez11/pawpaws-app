package app.pawpaws.features.admin.usuario

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> = adminRepository.usuarios

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChange(q: String) { _query.value = q }

    fun filtrados(): List<Usuario> {
        val texto = _query.value.trim().lowercase()
        return if (texto.isBlank()) usuarios.value
        else usuarios.value.filter {
            it.nombre.lowercase().contains(texto) || it.email.lowercase().contains(texto)
        }
    }

    fun activar(id: String) { adminRepository.activarUsuario(id) }

    /**
     * @param motivo    Motivo seleccionado del radio group
     * @param mensaje   Mensaje adicional opcional
     */
    fun advertir(
        id: String,
        motivo: String = "",
        mensaje: String = ""
    ) {
        adminRepository.advertirUsuario(id)
        // El motivo y mensaje quedan disponibles para extender AdminRepository en el futuro
    }

    /**
     * @param duracion  "24 horas" | "3 días" | "7 días"
     * @param motivo    Motivo seleccionado
     * @param detalles  Campo adicional opcional
     */
    fun suspender(
        id: String,
        duracion: String = "24 horas",
        motivo: String = "",
        detalles: String = ""
    ) {
        adminRepository.suspenderUsuario(id)
    }

    /**
     * @param motivo         Motivo seleccionado
     * @param justificacion  Campo obligatorio
     */
    fun suspenderPermanente(
        id: String,
        motivo: String = "",
        justificacion: String = ""
    ) {
        adminRepository.suspenderUsuarioPermanente(id)
    }
}