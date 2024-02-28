package org.freedomtool.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serial

@Parcelize
data class VotingData(
    val header: String,
    val description: String,
    val isPassportRequired: Boolean,
    val dueDate: Long?,
    val requirements: RequirementsForVoting? = null,
    val isManifest: Boolean = false,
    val options: List<OptionsData>? = null,
) : Parcelable
@Parcelize
data class RequirementsForVoting(
    val nationality: String? =null,
    val age: Int? = null,
) : Parcelable

@Parcelize
data class OptionsData(
    val name: String,
    val number: Int
) : Parcelable
