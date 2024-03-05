package org.freedomtool.data.models

data class RegistrationData(
    val chainId: String,
    /// Address for the voting
    val contract_address: String,
    val name: String,
    val description: String,
    val excerpt: String,
    val external_url: String,
    val isActive: Boolean?
)


data class SendCalldataRequest(val data: SendCalldataRequestData)

data class SendCalldataRequestData(val tx_data: String)