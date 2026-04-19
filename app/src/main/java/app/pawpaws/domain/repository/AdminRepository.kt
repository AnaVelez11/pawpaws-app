package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.HistorialAccion
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Reporte
import app.pawpaws.domain.model.models.Usuario
import kotlinx.coroutines.flow.StateFlow

interface AdminRepository {

    // Publicaciones
    val publicacionesPendientes: StateFlow<List<Publicacion>>
    fun aprobarPublicacion(id: String)
    fun rechazarPublicacion(id: String, motivo: String)

    // Reportes
    val reportes: StateFlow<List<Reporte>>
    fun resolverReporte(id: String)
    fun descartarReporte(id: String)

    // Usuarios
    val usuarios: StateFlow<List<Usuario>>
    fun suspenderUsuario(id: String)
    fun suspenderUsuarioPermanente(id: String)
    fun activarUsuario(id: String)
    fun advertirUsuario(id: String)

    // Historial
    val historial: StateFlow<List<HistorialAccion>>

    // Stats
    fun totalAprobadas(): Int
    fun totalRechazadas(): Int
}