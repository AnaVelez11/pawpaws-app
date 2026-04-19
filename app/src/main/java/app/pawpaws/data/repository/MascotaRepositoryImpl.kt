package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.Mascota
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor() : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    init { _mascotas.value = seed() }

    override fun save(mascota: Mascota) { _mascotas.value += mascota }

    override fun findById(id: String): Mascota? =
        _mascotas.value.firstOrNull { it.id == id }

    override fun findByUsuario(idUsuario: String): List<Mascota> =
        _mascotas.value.filter { it.idUsuario == idUsuario }

    override fun delete(id: String) {
        _mascotas.value = _mascotas.value.filter { it.id != id }
    }
    override fun update(mascota: Mascota) {
        _mascotas.value = _mascotas.value.map { if (it.id == mascota.id) mascota else it }
    }

    private fun seed(): List<Mascota> = listOf(
        Mascota(
            id = "m1", nombre = "Max", tipo = TipoMascota.PERRO,
            raza = "Labrador", edad = 2, genero = "Macho",
            tamaño = "Grande", descripcion = "Muy juguetón y amigable.",
            vacunasAlDia = true, idUsuario = "1"
        ),
        Mascota(
            id = "m2", nombre = "Luna", tipo = TipoMascota.GATO,
            raza = "Siamés", edad = 1, genero = "Hembra",
            tamaño = "Pequeño", descripcion = "Tranquila y cariñosa.",
            vacunasAlDia = true, idUsuario = "2"
        ),
        Mascota(
            id = "m3", nombre = "Rocky", tipo = TipoMascota.PERRO,
            raza = "Pastor Alemán", edad = 3, genero = "Macho",
            tamaño = "Grande", descripcion = "Leal y protector.",
            vacunasAlDia = false, idUsuario = "1"
        ),
        Mascota(
            id = "m4", tipo = TipoMascota.PERRO, edad = 2, genero = "Macho",
            tamaño = "Pequeño", descripcion = "Encontrado cerca al centro",
            idUsuario = "3"
        )
    )
}