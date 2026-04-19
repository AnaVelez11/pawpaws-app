package app.pawpaws.di

import app.pawpaws.data.repository.*
import app.pawpaws.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPublicacionRepository(
        publicacionRepositoryImpl: PublicacionRepositoryImpl
    ): PublicacionRepository

    @Binds
    @Singleton
    abstract fun bindMascotaRepository(
        mascotaRepositoryImpl: MascotaRepositoryImpl
    ): MascotaRepository

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        usuarioRepositoryImpl: UsuarioRepositoryImpl
    ): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindReporteRepository(
        reporteRepositoryImpl: ReporteRepositoryImpl
    ): ReporteRepository

    @Binds
    @Singleton
    abstract fun bindRevisionRepository(
        revisionRepositoryImpl: RevisionRepositoryImpl
    ): RevisionRepository

    @Binds
    @Singleton
    abstract fun bindSolicitudRepository(
        solicitudRepositoryImpl: SolicitudRepositoryImpl
    ): SolicitudRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionRepository(
        notificacionRepositoryImpl: NotificacionRepositoryImpl
    ): NotificacionRepository

    @Binds
    @Singleton
    abstract fun bindHistorialAccionRepository(
        historialAccionRepositoryImpl: HistorialAccionRepositoryImpl
    ): HistorialAccionRepository

    @Binds
    @Singleton
    abstract fun bindComentarioRepository(
        comentarioRepositoryImpl: ComentarioRepositoryImpl
    ): ComentarioRepository

    @Binds
    @Singleton
    abstract fun bindPerfilRepository(
        perfilRepositoryImpl: PerfilRepositoryImpl
    ): PerfilRepository

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        resourceProviderImpl: ResourceProviderImpl
    ): ResourceProvider
}
