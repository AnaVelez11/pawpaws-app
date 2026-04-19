package app.pawpaws.features.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _email = MutableStateFlow(ValidatedField())
    val email: StateFlow<ValidatedField> = _email

    private val _recoveryResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val recoveryResult: StateFlow<RequestResult<Unit>> = _recoveryResult

    fun onEmailChange(value: String) { _email.value = ValidatedField(value) }

    fun sendRecoveryLink() {
        val emailValue = _email.value.value.trim()

        if (emailValue.isBlank()) {
            _email.value = ValidatedField(emailValue, resourceProvider.getString(R.string.passwordrecovery_email_required))
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _email.value = ValidatedField(emailValue, resourceProvider.getString(R.string.passwordrecovery_email_invalid))
            return
        }

        _recoveryResult.value = RequestResult.Loading

        viewModelScope.launch {
            delay(1000)
            // Simulación: cualquier correo con formato válido recibe el link
            _recoveryResult.value = RequestResult.Success(Unit)
        }
    }

    fun resetResult() { _recoveryResult.value = RequestResult.Idle }
}