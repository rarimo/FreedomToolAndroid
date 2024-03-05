package org.freedomtool.utils.nfc.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.security.PublicKey
@Parcelize
data class EDocument (
    var docType: DocType? = null,
    var personDetails: PersonDetails? = null,
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var isPassiveAuth: Boolean = false,
    var isActiveAuth: Boolean = false,
    var isChipAuth: Boolean = false,
    var sod: String? = null,
    var dg1: String? = null,
    var dg2Hash: String? = null
) : Parcelable
