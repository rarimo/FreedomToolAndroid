package org.freedomtool.data.models

import com.google.gson.Gson

data class IdentityData(
    val secretHex: String,
    val secretKeyHex: String,
    val nullifierHex: String,
    val vCsJSON: String
){

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromJson(json: String): IdentityData {
            val gson = Gson()
            return gson.fromJson(json, IdentityData::class.java)
        }
    }


}
