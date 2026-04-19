package app.pawpaws.di

import app.pawpaws.data.repository.MensajeRepositoryImpl
import app.pawpaws.domain.repository.MensajeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MensajeModule {

    @Binds
    @Singleton
    abstract fun bindMensajeRepository(
        impl: MensajeRepositoryImpl
    ): MensajeRepository
}