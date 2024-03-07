package org.freedomtool.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import org.freedomtool.utils.getIssuingAuthorityString
import java.math.BigInteger

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
    val isActive: Boolean = true,
) : Parcelable

@Parcelize
@Serializable
data class RequirementsForVoting(
    val nationality: List<BigInteger>,
    val age: Int? = null,
) : Parcelable {
    fun getNationality(): String? {
        if (nationality.isEmpty())
            return null

        val nationalityStrings = nationality.map { getIssuingAuthorityString(it.toLong()) }
        return nationalityStrings.joinToString(", ")
    }

    fun isInList(userNationality: String): Boolean {
        val nationalityStrings = nationality.map { getIssuingAuthorityString(it.toLong()) }
        return userNationality in nationalityStrings
    }
}

@Parcelize
@Serializable
data class OptionsData(
    val name: String,
    val number: Int
) : Parcelable

