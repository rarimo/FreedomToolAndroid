package org.freedomtool.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

data class RegistrationData(
    val chainId: String,
    /// Address for the voting
    val contract_address: String,
    val name: String,
    val description: String,
    val excerpt: String,
    val external_url: String,
    val isActive: Boolean?,
    val metadata: Metadata?
)
@Parcelize
@Serializable
data class Metadata(
    val option: String,
    val question: String
) : Parcelable

data class SendCalldataRequest(val data: SendCalldataRequestData)

data class SendCalldataRequestData(val tx_data: String)