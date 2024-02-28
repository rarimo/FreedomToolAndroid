package org.freedomtool.logic.persistance

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object SecureSharedPrefs {
    private const val PREFS_FILE_NAME = "secure_file"


    private val tags = mapOf(
        "accessToken" to "access_token",
        "DG7" to "DG7",
        "DG2" to "DG2",
        "DG1" to "DG1",
        "SOD" to "SOD",
        "DATE_OF_BIRTH" to "DATE_OF_BIRTH",
        "ISSUE_AUTHORITY" to "ISSUE_AUTHORITY",
        "CERTIFICATE_PEM" to "CERTIFICATE_PEM",
        "FIRST_LAUNCHED" to "FIRST_LAUNCHED",
        "PASSPORT_VERIFICATION_PROOF" to "PASSPORT_VERIFICATION_PROOF",
        "VOTE_RESULT" to "VOTE_RESULT",
        "LOCALE" to "LOCALE",
        "PRIVATE_KEY" to "PRIVATE_KEY",
        "ISSUER_DID" to "ISSUER_DID",
        "IDENTITY" to "IDENTITY",
    )

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getIssuerDid(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["ISSUER_DID"], "")
    }

    fun saveIssuerDid(context: Context, did: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["ISSUER_DID"], did)
        editor.apply()
    }

    fun clearAllData(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        for (entry in tags.entries.iterator()) {
            if (entry.value == tags["FIRST_LAUNCHED"])
                continue
            if (entry.value == tags["LOCALE"])
                continue
            editor.remove(entry.value)
        }
        editor.apply()
    }

    fun saveLocale(context: Context, language: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["LOCALE"], language)
        editor.apply()
    }


    fun getSavedLocale(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["LOCALE"], "")
    }

    fun saveDateOfBirth(context: Context, date: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["DATE_OF_BIRTH"], date)
        editor.apply()
    }

    fun saveFirstLaunch(context: Context, firstLaunched: Boolean) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tags["FIRST_LAUNCHED"], firstLaunched)
        editor.apply()
    }

    fun savePassportVerificationProof(context: Context, proof: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["PASSPORT_VERIFICATION_PROOF"], proof)
        editor.apply()
    }

    fun saveIdentityData(context: Context, json: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["IDENTITY"], json)
        editor.apply()
    }

    fun getIdentityData(context: Context) : String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["IDENTITY"], "")
    }

    fun getPassportVerificationProof(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["PASSPORT_VERIFICATION_PROOF"], "")
    }

    fun isFirstLaunch(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(tags["FIRST_LAUNCHED"], true)
    }

    fun saveIssuerAuthority(context: Context, data: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["ISSUE_AUTHORITY"], data)
        editor.apply()
    }

    fun getIssuerAuthority(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["ISSUE_AUTHORITY"], "")
    }

    fun getDateOfBirth(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["DATE_OF_BIRTH"], "")
    }

    fun readSOD(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["SOD"], "")
    }

    fun saveVoteResult(context: Context, string: Int) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(tags["VOTE_RESULT"], string)
        editor.apply()
    }

    fun getVoteResult(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt(tags["VOTE_RESULT"], -1)
    }


    fun saveSOD(context: Context, sod: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["SOD"], sod)
        editor.apply()
    }

    fun saveDG1(context: Context, string: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["DG1"], string)
        editor.apply()
    }

    fun readDG1(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["DG1"], "")
    }

    fun savePassportData(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tags["accessToken"], true)
        editor.apply()
    }

    fun getPassportData(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(tags["accessToken"], false)
    }



}