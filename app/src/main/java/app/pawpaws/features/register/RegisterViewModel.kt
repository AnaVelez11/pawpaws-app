package app.pawpaws.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

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

    private val _registerResult =
        MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val registerResult: StateFlow<RequestResult<Unit>> = _registerResult


    fun onNameChange(value: String) {
        _name.value = ValidatedField(value)
    }

    fun onEmailChange(value: String) {
        _email.value = ValidatedField(value)
    }

    fun onPasswordChange(value: String) {
        _password.value = ValidatedField(value)
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = ValidatedField(value)
    }

    fun onCityChange(value: String) {
        _city.value = ValidatedField(value)
    }


    fun register() {

        val nameValue = _name.value.value
        val emailValue = _email.value.value
        val passwordValue = _password.value.value
        val confirmValue = _confirmPassword.value.value
        val cityValue = _city.value.value


        if (nameValue.isBlank()) {
            _name.value = ValidatedField(nameValue, "Ingresa tu nombre")
            return
        }

        if (!emailValue.contains("@")) {
            _email.value = ValidatedField(emailValue, "Correo inválido")
            return
        }

        if (passwordValue.length < 6) {
            _password.value = ValidatedField(passwordValue, "Mínimo 6 caracteres")
            return
        }

        if (passwordValue != confirmValue) {
            _confirmPassword.value =
                ValidatedField(confirmValue, "Las contraseñas no coinciden")
            return
        }

        if (cityValue.isBlank()) {
            _city.value =
                ValidatedField(cityValue, "Ingresa tu ciudad o código postal")
            return
        }

        _registerResult.value = RequestResult.Loading

        viewModelScope.launch {
            delay(1500)
            _registerResult.value = RequestResult.Success(Unit)
        }
    }
}