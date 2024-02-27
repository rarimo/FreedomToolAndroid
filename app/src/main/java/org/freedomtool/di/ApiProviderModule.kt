package org.freedomtool.di

import dagger.Module
import dagger.Provides
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.di.providers.ApiProviderImpl

import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiProviderModule {
    @Provides
    @Singleton
    fun getApiProvider(retrofit: Retrofit): ApiProvider {
        return ApiProviderImpl(retrofit)
    }
}