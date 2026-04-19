package app.pawpaws.features.mapa

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Ubicacion
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    private val publicacionRepository: PublicacionRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    val publicaciones: StateFlow<List<Publicacion>> = publicacionRepository.publicaciones

    private val _publicacionSeleccionada = MutableStateFlow<Publicacion?>(null)
    val publicacionSeleccionada: StateFlow<Publicacion?> = _publicacionSeleccionada.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    fun seleccionar(publicacion: Publicacion?) { _publicacionSeleccionada.value = publicacion }

    fun onBusquedaChange(value: String) { _busqueda.value = value }

    fun ubicacionDe(idUbicacion: String): Ubicacion? =
        publicacionRepository.findUbicacionById(idUbicacion)
}