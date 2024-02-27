package org.freedomtool.di.providers

import org.freedomtool.data.datasource.api.CircuitBackendApi
import org.web3j.protocol.Web3j

interface ApiProvider {
    val circuitBackend: CircuitBackendApi
    val web3: Web3j
}