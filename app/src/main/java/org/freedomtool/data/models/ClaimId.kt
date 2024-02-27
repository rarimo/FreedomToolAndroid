package org.freedomtool.data.models
data class DataClaim(
    val id: String,
    val type: String,
    val attributes: Attributes
)

data class Attributes(
    val claim_id: String,
    val issuer_did: String
)

data class ClaimId(
    val data: DataClaim,
    val included: List<Any> // You can replace Any with the actual type if included objects have a specific structure
)
