package org.freedomtool.data.models

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IdCardSod(
    val signed_attributes: String,
    val algorithm: String,
    val signature: String,
    val pem_file: String,
    val encapsulated_content: String
) : Parcelable

@Parcelize
data class Proof(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
    var curve: String = "bn128"
) : Parcelable {
    companion object {
        fun fromJson(jsonString: String): Proof {
            val json = Gson().fromJson(jsonString, Proof::class.java)
            json.curve = getDefaultCurve()
            return json
        }

        private fun getDefaultCurve(): String {
            return "bn128"
        }
    }

}

@Parcelize
data class ZkProof(
    val proof: Proof,
    val pub_signals: List<String>
) : Parcelable
@Parcelize
data class Data(
    val id: String,
    val document_sod: IdCardSod,
    val zkproof: ZkProof
) : Parcelable

@Parcelize
data class Payload(
    val data: Data
) : Parcelable {

}
