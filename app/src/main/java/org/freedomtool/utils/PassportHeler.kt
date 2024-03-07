package org.freedomtool.utils

fun getIssuingAuthorityCode(issuingAuthority: String): String {
    return when (issuingAuthority) {
        "UKR" -> "4903594"
        "RUS" -> "13281866"
        "GEO" -> "15901410"
        else -> "0"
    }
}

fun getIssuingAuthorityString(code: Long): String {
    return when (code) {
        4903594L -> "UKR"
        13281866L -> "RUS"
        15901410L -> "GEO"
        else -> ""
    }
}
