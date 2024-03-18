package org.freedomtool.di.providers

import org.freedomtool.base.BaseConfig
import org.freedomtool.data.datasource.api.CircuitBackendApi
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import retrofit2.Retrofit

class ApiProviderImpl(
    private val retrofit: Retrofit
) : ApiProvider {
    override val circuitBackend: CircuitBackendApi by lazy {
        retrofit.create(CircuitBackendApi::class.java)
    }

    override val web3: Web3j
        get() = Web3j.build(HttpService(BaseConfig.BLOCK_CHAIN_RPC_LINK))
}