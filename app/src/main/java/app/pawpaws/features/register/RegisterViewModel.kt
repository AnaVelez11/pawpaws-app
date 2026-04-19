package app.pawpaws.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.domain.model.enums.EstadoUsuario
import app.pawpaws.domain.model.enums.Rol
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _name = MutableStateFlow(ValidatedField())
    val name: StateFlow<ValidatedField> = _name

    private val _email = MutableStateFlow(ValidatedField())
    val email: StateFlow<ValidatedField> = _email

    private val _password = MutableStateFlow(ValidatedField())
    val password: StateFlow<ValidatedField> = _password

    private val _confirmPassword = MutableStateFlow(ValidatedField())
    val confirmPassword: StateFlow<ValidatedField> = _confirmPassword

    private val _city = MutableStateFlow(ValidatedField())
    val city: StateFlow<ValidatedField> = _city

    private val _registerResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val registerResult: StateFlow<RequestResult<Unit>> = _registerResult

    fun onNameChange(value: String) { _name.value = ValidatedField(value) }
    fun onEmailChange(value: String) { _email.value = ValidatedField(value) }
    fun onPasswordChange(value: String) { _password.value = ValidatedField(value) }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = ValidatedField(value) }
    fun onCityChange(value: String) { _city.value = ValidatedField(value) }

    fun register() {
        val nameVal = _name.value.value.trim()
        val emailVal = _email.value.value.trim()
        val passVal = _password.value.value
        val confirmVal = _confirmPassword.value.value

        var hasError = false

        if (nameVal.isBlank()) {
            _name.value = ValidatedField(nameVal, resourceProvider.getString(R.string.register_error_name_required))
            hasError = true
        }
        if (!emailVal.contains("@")) {
            _email.value = ValidatedField(emailVal, resourceProvider.getString(R.string.register_error_email_invalid))
            hasError = true
        }
        if (passVal.length < 6) {
            _password.value = ValidatedField(passVal, resourceProvider.getString(R.string.register_error_password_min))
            hasError = true
        }
        if (confirmVal != passVal) {
            _confirmPassword.value = ValidatedField(confirmVal, resourceProvider.getString(R.string.register_error_passwords_mismatch))
            hasError = true
        }
        if (hasError) return

        _registerResult.value = RequestResult.Loading

        viewModelScope.launch {
            val nuevoUsuario = Usuario(
                id = UUID.randomUUID().toString(),
                nombre = nameVal,
                email = emailVal,
                password = passVal,
                telefono = null,
                fotoPerfil = null,
                rol = Rol.USER,
                estado = EstadoUsuario.ACTIVO
            )
            usuarioRepository.save(nuevoUsuario)
            _registerResult.value = RequestResult.Success(Unit)
        }
    }

    fun resetResult() { _registerResult.value = RequestResult.Idle }
}
