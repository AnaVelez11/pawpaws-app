package app.pawpaws.data.repository

import app.pawpaws.domain.model.enums.AccionAdmin
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.EstadoReporte
import app.pawpaws.domain.model.enums.EstadoUsuario
import app.pawpaws.domain.model.enums.Rol
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.HistorialAccion
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Reporte
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor() : AdminRepository {

    private val _publicacionesPendientes = MutableStateFlow<List<Publicacion>>(seedPublicaciones())
    override val publicacionesPendientes: StateFlow<List<Publicacion>> = _publicacionesPendientes.asStateFlow()

    private val _reportes = MutableStateFlow<List<Reporte>>(seedReportes())
    override val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(seedUsuarios())
    override val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _historial = MutableStateFlow<List<HistorialAccion>>(seedHistorial())
    override val historial: StateFlow<List<HistorialAccion>> = _historial.asStateFlow()

    private val hoy get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // ── Publicaciones ──────────────────────────────────────────────────────────

    override fun aprobarPublicacion(id: String) {
        _publicacionesPendientes.value = _publicacionesPendientes.value.filter { it.id != id }
        registrar(AccionAdmin.APROBADA, "admin_3", id, "Publicación aprobada")
    }

    override fun rechazarPublicacion(id: String, motivo: String) {
        _publicacionesPendientes.value = _publicacionesPendientes.value.filter { it.id != id }
        registrar(AccionAdmin.RECHAZADA, "admin_3", id, "Rechazada: $motivo")
    }

    // ── Reportes ───────────────────────────────────────────────────────────────

    override fun resolverReporte(id: String) {
        _reportes.value = _reportes.value.map {
            if (it.id == id) it.copy(estado = EstadoReporte.RESUELTO) else it
        }
    }

    override fun descartarReporte(id: String) {
        _reportes.value = _reportes.value.map {
            if (it.id == id) it.copy(estado = EstadoReporte.DESCARTADO) else it
        }
    }

    // ── Usuarios ───────────────────────────────────────────────────────────────

    override fun suspenderUsuario(id: String) {
        actualizarEstadoUsuario(id, EstadoUsuario.SUSPENDIDO)
        registrar(AccionAdmin.SUSPENSION_TEMPORAL, "admin_3", id, "Usuario suspendido temporalmente")
    }

    override fun suspenderUsuarioPermanente(id: String) {
        actualizarEstadoUsuario(id, EstadoUsuario.INACTIVO)
        registrar(AccionAdmin.SUSPENSION_PERMANENTE, "admin_3", id, "Usuario suspendido permanentemente")
    }

    override fun activarUsuario(id: String) {
        actualizarEstadoUsuario(id, EstadoUsuario.ACTIVO)
    }

    override fun advertirUsuario(id: String) {
        registrar(AccionAdmin.ADVERTENCIA, "admin_3", id, "Advertencia enviada al usuario")
    }

    private fun actualizarEstadoUsuario(id: String, estado: EstadoUsuario) {
        _usuarios.value = _usuarios.value.map {
            if (it.id == id) it.copy(estado = estado) else it
        }
    }

    // ── Stats ──────────────────────────────────────────────────────────────────

    override fun totalAprobadas(): Int =
        _historial.value.count { it.accion == AccionAdmin.APROBADA }

    override fun totalRechazadas(): Int =
        _historial.value.count { it.accion == AccionAdmin.RECHAZADA }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun registrar(
        accion: AccionAdmin,
        idModerador: String,
        idAfectado: String,
        descripcion: String
    ) {
        val entrada = HistorialAccion(
            id                = UUID.randomUUID().toString(),
            accion            = accion,
            idModerador       = idModerador,
            idUsuarioAfectado = idAfectado,
            descripcion       = descripcion,
            fecha             = hoy
        )
        _historial.value = listOf(entrada) + _historial.value
    }

    // ── Seeds ──────────────────────────────────────────────────────────────────

    private fun seedPublicaciones() = listOf(
        Publicacion(
            id              = "p_adm_1",
            titulo          = "Firulais necesita hogar",
            descripcion     = "Perro mestizo rescatado, muy cariñoso y sociable.",
            tipoPublicacion = TipoPublicacion.ADOPCION,
            estado          = EstadoPublicacion.PENDIENTE_VERIFICACION,
            idMascota       = "m1",
            idUsuario       = "1",
            idUbicacion     = "u1",
            fechaCreacion   = "2026-04-16"
        ),
        Publicacion(
            id              = "p_adm_2",
            titulo          = "Se perdió Luna cerca del parque",
            descripcion     = "Gata persa blanca, collar azul. Desapareció el martes.",
            tipoPublicacion = TipoPublicacion.PERDIDO,
            estado          = EstadoPublicacion.PENDIENTE_VERIFICACION,
            idMascota       = "m2",
            idUsuario       = "2",
            idUbicacion     = "u1",
            fechaCreacion   = "2026-04-17"
        ),
        Publicacion(
            id              = "p_adm_3",
            titulo          = "Encontré un cachorro herido",
            descripcion     = "Labrador joven encontrado en la carretera, necesita atención.",
            tipoPublicacion = TipoPublicacion.ENCONTRADO,
            estado          = EstadoPublicacion.PENDIENTE_VERIFICACION,
            idMascota       = "m3",
            idUsuario       = "1",
            idUbicacion     = "u1",
            fechaCreacion   = "2026-04-18"
        )
    )

    private fun seedReportes() = listOf(
        Reporte(
            id               = "r1",
            motivo           = "Datos falsos",
            descripcion      = "La publicación contiene información engañosa sobre la mascota.",
            fecha            = "2026-04-15",
            estado           = EstadoReporte.PENDIENTE,
            idPublicacion    = "p_adm_1",
            idUsuarioReporta = "2"
        ),
        Reporte(
            id               = "r2",
            motivo           = "Venta ilegal",
            descripcion      = "El usuario está intentando vender una mascota ilegalmente.",
            fecha            = "2026-04-16",
            estado           = EstadoReporte.PENDIENTE,
            idPublicacion    = "p_adm_2",
            idUsuarioReporta = "1"
        ),
        Reporte(
            id               = "r3",
            motivo           = "Lenguaje inapropiado",
            descripcion      = "Comentarios ofensivos en la descripción.",
            fecha            = "2026-04-17",
            estado           = EstadoReporte.RESUELTO,
            idPublicacion    = "p_adm_3",
            idUsuarioReporta = "2"
        )
    )

    private fun seedUsuarios() = listOf(
        Usuario(
            id         = "1",
            nombre     = "Juan Galvis",
            email      = "juan@pawpaws.com",
            password   = "111111",
            telefono   = "3001234567",
            fotoPerfil = app.pawpaws.core.utils.resources.ImageResources.USER_JUAN,
            rol        = Rol.USER,
            estado     = EstadoUsuario.ACTIVO
        ),
        Usuario(
            id         = "2",
            nombre     = "Ana Vélez",
            email      = "ana@pawpaws.com",
            password   = "222222",
            telefono   = "3109876543",
            fotoPerfil = app.pawpaws.core.utils.resources.ImageResources.USER_ANA,
            rol        = Rol.USER,
            estado     = EstadoUsuario.ACTIVO
        ),
        Usuario(
            id         = "3",
            nombre     = "Isabella García",
            email      = "admin@pawpaws.com",
            password   = "333333",
            telefono   = "3205551234",
            fotoPerfil = app.pawpaws.core.utils.resources.ImageResources.USER_ADMIN,
            rol        = Rol.ADMIN,
            estado     = EstadoUsuario.ACTIVO
        )
    )

    private fun seedHistorial() = listOf(
        HistorialAccion(
            id                = "h1",
            accion            = AccionAdmin.APROBADA,
            idModerador       = "admin_3",
            idUsuarioAfectado = "pub_001",
            descripcion       = "Publicación de adopción aprobada",
            fecha             = "2026-04-14"
        ),
        HistorialAccion(
            id                = "h2",
            accion            = AccionAdmin.RECHAZADA,
            idModerador       = "admin_3",
            idUsuarioAfectado = "pub_002",
            descripcion       = "Rechazada por datos falsos",
            fecha             = "2026-04-15"
        ),
        HistorialAccion(
            id                = "h3",
            accion            = AccionAdmin.ADVERTENCIA,
            idModerador       = "admin_3",
            idUsuarioAfectado = "1",
            descripcion       = "Advertencia por contenido inapropiado",
            fecha             = "2026-04-16"
        )
    )
}