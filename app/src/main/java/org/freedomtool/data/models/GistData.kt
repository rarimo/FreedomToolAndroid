package org.freedomtool.data.models

data class GistData(
    val data: GistDataIn,
    val included: List<Any>
)

data class GistDataIn(
    val id: String,
    val type: String,
    val attributes: DataAttributes
)

data class DataAttributes(
    val gist_proof: GistProof,
    val gist_root: String
)

data class GistProof(
    val aux_existence: Boolean,
    val aux_index: String,
    val aux_value: String,
    val existence: Boolean,
    val index: String,
    val root: String,
    val siblings: List<String>,
    val value: String
)
