package org.freedomtool.data.models

data class ClaimOfferResponse(
    val body: ClaimOfferResponseBody,
    val from: String,
    val id: String,
    val threadID: String,
    val to: String,
    val typ: String,
    val type: String
)

data class ClaimOfferResponseBody(
    val Credentials: List<Credential>,
    val url: String
)

data class Credential(
    val description: String,
    val id: String
)
