package app.pawpaws.features.publicar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.usecase.CrearPublicacionUseCase
import app.pawpaws.domain.usecase.EditarPublicacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicarViewModel @Inject constructor(
    private val crearPublicacionUseCase: CrearPublicacionUseCase,
    private val editarPublicacionUseCase: EditarPublicacionUseCase,
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _idPublicacionEditando = MutableStateFlow<String?>(null)
    val idPublicacionEditando: StateFlow<String?> = _idPublicacionEditando.asStateFlow()

    private val _motivoRechazo = MutableStateFlow<String?>(null)
    val motivoRechazo: StateFlow<String?> = _motivoRechazo.asStateFlow()

    private val _tipoPublicacion = MutableStateFlow(TipoPublicacion.ADOPCION)
    val tipoPublicacion: StateFlow<TipoPublicacion> = _tipoPublicacion.asStateFlow()

    private val _titulo = MutableStateFlow(ValidatedField())
    val titulo: StateFlow<ValidatedField> = _titulo.asStateFlow()

    private val _nombreMascota = MutableStateFlow(ValidatedField())
    val nombreMascota: StateFlow<ValidatedField> = _nombreMascota.asStateFlow()

    private val _raza = MutableStateFlow(ValidatedField())
    val raza: StateFlow<ValidatedField> = _raza.asStateFlow()

    private val _descripcion = MutableStateFlow(ValidatedField())
    val descripcion: StateFlow<ValidatedField> = _descripcion.asStateFlow()

    private val _edad = MutableStateFlow(ValidatedField())
    val edad: StateFlow<ValidatedField> = _edad.asStateFlow()

    private val _tipoMascota = MutableStateFlow(TipoMascota.PERRO)
    val tipoMascota: StateFlow<TipoMascota> = _tipoMascota.asStateFlow()

    private val _genero = MutableStateFlow("")
    val genero: StateFlow<String> = _genero.asStateFlow()

    private val _tamaño = MutableStateFlow("")
    val tamaño: StateFlow<String> = _tamaño.asStateFlow()

    private val _vacunasAlDia = MutableStateFlow(false)
    val vacunasAlDia: StateFlow<Boolean> = _vacunasAlDia.asStateFlow()

    private val _publicarResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val publicarResult: StateFlow<RequestResult<Unit>> = _publicarResult.asStateFlow()

    fun onTipoPublicacionChange(v: TipoPublicacion) { _tipoPublicacion.value = v }
    fun onTituloChange(v: String)           { _titulo.value        = ValidatedField(v) }
    fun onNombreMascotaChange(v: String)    { _nombreMascota.value = ValidatedField(v) }
    fun onRazaChange(v: String)             { _raza.value          = ValidatedField(v) }
    fun onDescripcionChange(v: String)      { _descripcion.value   = ValidatedField(v) }
    fun onEdadChange(v: String)             { _edad.value          = ValidatedField(v) }
    fun onTipoMascotaChange(v: TipoMascota) { _tipoMascota.value   = v }
    fun onGeneroChange(v: String)           { _genero.value        = v }
    fun onTamañoChange(v: String)           { _tamaño.value        = v }
    fun onVacunasChange(v: Boolean)         { _vacunasAlDia.value  = v }

    fun cargarParaEdicion(idPublicacion: String) {
        val pub = publicacionRepository.findById(idPublicacion) ?: return
        val mascota = mascotaRepository.findById(pub.idMascota)

        _idPublicacionEditando.value = idPublicacion
        _tipoPublicacion.value       = pub.tipoPublicacion
        _titulo.value                = ValidatedField(pub.titulo)
        _descripcion.value           = ValidatedField(pub.descripcion)

        mascota?.let {
            _nombreMascota.value = ValidatedField(it.nombre ?:"")
            _raza.value          = ValidatedField(it.raza ?:"")
            _edad.value          = ValidatedField(it.edad.toString())
            _tipoMascota.value   = it.tipo
            _genero.value        = it.genero
            _tamaño.value        = it.tamaño
            _vacunasAlDia.value  = it.vacunasAlDia ?:false
        }

        _motivoRechazo.value = resourceProvider.getString(R.string.publicar_simulated_rejection)
    }

    fun limpiarModoEdicion() {
        _idPublicacionEditando.value = null
        _motivoRechazo.value = null
    }

    private fun validar(): Int? {
        var hasError = false
        if (_titulo.value.value.isBlank()) {
            _titulo.value = ValidatedField(_titulo.value.value, resourceProvider.getString(R.string.publicar_error_title_required))
            hasError = true
        }
        if (_nombreMascota.value.value.isBlank()) {
            _nombreMascota.value = ValidatedField(_nombreMascota.value.value, resourceProvider.getString(R.string.publicar_error_name_required))
            hasError = true
        }
        if (_descripcion.value.value.isBlank()) {
            _descripcion.value = ValidatedField(_descripcion.value.value, resourceProvider.getString(R.string.publicar_error_desc_required))
            hasError = true
        }
        val edadInt = _edad.value.value.toIntOrNull()
        if (edadInt == null || edadInt < 0) {
            _edad.value = ValidatedField(_edad.value.value, resourceProvider.getString(R.string.publicar_error_age_invalid))
            hasError = true
        }
        if (hasError) return null
        return edadInt
    }

    fun publicar(idUsuario: String) {
        val edadInt = validar() ?: return
        viewModelScope.launch {
            _publicarResult.value = RequestResult.Loading
            _publicarResult.value = crearPublicacionUseCase.execute(
                titulo          = _titulo.value.value,
                descripcion     = _descripcion.value.value,
                nombreMascota   = _nombreMascota.value.value,
                raza            = _raza.value.value,
                edad            = edadInt,
                tipoMascota     = _tipoMascota.value,
                genero          = _genero.value,
                tamaño          = _tamaño.value,
                vacunasAlDia    = _vacunasAlDia.value,
                tipoPublicacion = _tipoPublicacion.value,
                idUsuario       = idUsuario,
                idUbicacion     = "u1",
                imagenes        = emptyList()
            )
        }
    }

    fun guardarCambios() {
        val idPublicacion = _idPublicacionEditando.value ?: return
        val edadInt = validar() ?: return
        viewModelScope.launch {
            _publicarResult.value = RequestResult.Loading
            _publicarResult.value = editarPublicacionUseCase.execute(
                idPublicacion   = idPublicacion,
                titulo          = _titulo.value.value,
                descripcion     = _descripcion.value.value,
                nombreMascota   = _nombreMascota.value.value,
                raza            = _raza.value.value,
                edad            = edadInt,
                tipoMascota     = _tipoMascota.value,
                genero          = _genero.value,
                tamaño          = _tamaño.value,
                vacunasAlDia    = _vacunasAlDia.value,
                tipoPublicacion = _tipoPublicacion.value
            )
        }
    }

    fun resetResult() { _publicarResult.value = RequestResult.Idle }
}
