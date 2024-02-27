package org.freedomtool.data.models

data class StateInfo(
    val index: String,
    val hash: String,
    val createdAtTimestamp: String,
    val createdAtBlock: String,
    val lastUpdateOperationIndex: String
)

data class StateInfoResponse(
    val state: StateInfo
)
