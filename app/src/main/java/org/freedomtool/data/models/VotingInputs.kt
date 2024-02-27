package org.freedomtool.data.models

data class VotingInputs(
    val root: String,
    val vote: String,
    val votingAddress: String,
    val secret: String,
    val nullifier: String,
    val siblings: List<String>
)