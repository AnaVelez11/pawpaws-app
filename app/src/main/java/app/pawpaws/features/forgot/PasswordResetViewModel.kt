package app.pawpaws.features.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.domain.repository.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _digit1 = MutableStateFlow(ValidatedField())
    private val _digit2 = MutableStateFlow(ValidatedField())
    private val _digit3 = MutableStateFlow(ValidatedField())
    private val _digit4 = MutableStateFlow(ValidatedField())
    private val _digit5 = MutableStateFlow(ValidatedField())

    val digit1: StateFlow<ValidatedField> = _digit1
    val digit2: StateFlow<ValidatedField> = _digit2
    val digit3: StateFlow<ValidatedField> = _digit3
    val digit4: StateFlow<ValidatedField> = _digit4
    val digit5: StateFlow<ValidatedField> = _digit5

    private val _newPassword = MutableStateFlow(ValidatedField())
    val newPassword: StateFlow<ValidatedField> = _newPassword

    private val _resetResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val resetResult: StateFlow<RequestResult<Unit>> = _resetResult

    fun onDigitChange(index: Int, value: String) {
        if (value.length > 1 || !value.all { it.isDigit() }) return
        when (index) {
            0 -> _digit1.value = ValidatedField(value)
            1 -> _digit2.value = ValidatedField(value)
            2 -> _digit3.value = ValidatedField(value)
            3 -> _digit4.value = ValidatedField(value)
            4 -> _digit5.value = ValidatedField(value)
        }
    }

    fun onNewPasswordChange(value: String) { _newPassword.value = ValidatedField(value) }

    fun confirmReset() {
        val fullCode = _digit1.value.value + _digit2.value.value +
                _digit3.value.value + _digit4.value.value + _digit5.value.value

        if (fullCode.length != 5) {
            _digit1.value = ValidatedField(_digit1.value.value, resourceProvider.getString(R.string.passwordreset_code_required))
            return
        }
        if (_newPassword.value.value.length < 6) {
            _newPassword.value = ValidatedField(_newPassword.value.value, resourceProvider.getString(R.string.passwordreset_password_min_length))
            return
        }

        _resetResult.value = RequestResult.Loading

        viewModelScope.launch {
            delay(1000)
            // Simulación: código "12345" es válido
            if (fullCode == "12345") {
                _resetResult.value = RequestResult.Success(Unit)
            } else {
                _resetResult.value = RequestResult.Error(resourceProvider.getString(R.string.passwordreset_code_invalid))
            }
        }
    }

    fun resetResult() { _resetResult.value = RequestResult.Idle }
}