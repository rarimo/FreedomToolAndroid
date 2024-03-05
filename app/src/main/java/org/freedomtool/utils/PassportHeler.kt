package org.freedomtool.utils

fun getIssuingAuthorityCode(issuingAuthority: String): String {
    return when (issuingAuthority) {
        "UKR" -> "4903594"
        "RUS" -> "13281866"
        "GEO" -> "15901410"
        else -> "0"
    }
}
