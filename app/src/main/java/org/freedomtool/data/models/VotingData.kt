package org.freedomtool.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serial
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class VotingData(
    val header: String,
    val description: String,
    val excerpt: String,
    val isPassportRequired: Boolean,
    val dueDate: Long?,
    val contractAddress: String?,
    val requirements: RequirementsForVoting? = null,
    val isManifest: Boolean = false,
    val options: List<OptionsData>? = null,
    val votingCount: Long = 0,
    val isActive: Boolean = false
) : Parcelable
@Parcelize
@Serializable
data class RequirementsForVoting(
    val nationality: String? =null,
    val age: Int? = null,
) : Parcelable

@Parcelize
@Serializable
data class OptionsData(
    val name: String,
    val number: Int
) : Parcelable

