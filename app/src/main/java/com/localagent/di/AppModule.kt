package com.localagent.di

import com.localagent.data.agent.DefaultAgentRepository
import com.localagent.domain.repository.AgentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAgentRepository(impl: DefaultAgentRepository): AgentRepository
}
