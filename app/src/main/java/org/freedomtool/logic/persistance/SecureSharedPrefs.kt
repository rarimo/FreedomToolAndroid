package org.freedomtool.logic.persistance

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object SecureSharedPrefs {
    private const val PREFS_FILE_NAME = "irpgeojk;flp[oioguhohupj"
    private var sharedPref: SharedPreferences? = null


    private val tags = mapOf(
        "accessToken" to "access_token",
        "DATE_OF_BIRTH" to "DATE_OF_BIRTH",
        "ISSUE_AUTHORITY" to "ISSUE_AUTHORITY",
        "FIRST_LAUNCHED" to "FIRST_LAUNCHED",
        "VOTE_RESULT" to "VOTE_RESULT",
        "LOCALE" to "LOCALE",
        "ISSUER_DID" to "ISSUER_DID",
        "IDENTITY" to "IDENTITY",
        "SAVED_VOTING" to "SAVED_VOTING",
        "FINALIZATION_VOTE" to "FINALIZATION_VOTE",
        "VC" to "VC",
        "CLAIM_ID" to "CLAIM_ID",
        "PIN_CODE" to "PIN_CODE",
        "IS_BIOMETRIC_ENABLED" to "IS_BIOMETRIC_ENABLED"
    )

    private fun getSharedPreferences(context: Context): SharedPreferences {
        if (sharedPref == null) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            sharedPref = EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME, masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPref!!
        }

        return sharedPref!!

    }

    fun addVoted(context: Context, address: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val currentSavedData =
            sharedPreferences.getStringSet(tags["SAVED_VOTING"], HashSet<String>())

        currentSavedData!!.add(address)

        editor.putStringSet(tags["SAVED_VOTING"], currentSavedData)
        editor.apply()
    }

    private fun getFinalizationVote(context: Context): Set<String> {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getStringSet(tags["FINALIZATION_VOTE"], HashSet<String>())
            .orEmpty()
    }

    fun addFinalizationVote(context: Context, address: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val currentSavedData =
            sharedPreferences.getStringSet(tags["FINALIZATION_VOTE"], HashSet<String>())

        currentSavedData!!.add(address)

        editor.putStringSet(tags["SAVED_VOTING"], currentSavedData)
        editor.apply()
    }

    fun checkFinalizes(context: Context, address: String): Boolean {
        val set = getFinalizationVote(context)
        return set.contains(address)
    }

    private fun getVoted(context: Context): Set<String> {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getStringSet(tags["SAVED_VOTING"], HashSet<String>()).orEmpty()
    }

    fun checkIsVoted(context: Context, address: String): Boolean {
        val set = getVoted(context)
        return set.contains(address)
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
            if(entry.value == tags["PIN_CODE"])
                continue
            editor.remove(entry.value)
        }
        editor.apply()
    }

    fun savePinCode(context: Context, pinCode: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["PIN_CODE"], pinCode)
        editor.apply()
    }

    fun isPinCodeExist(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        val savedPinCode = sharedPreferences.getString(tags["PIN_CODE"], null)

        return savedPinCode != null
    }

    fun checkPinCode(context: Context, pinCode: String): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        val savedPinCode = sharedPreferences.getString(tags["PIN_CODE"], "")

        return savedPinCode == pinCode
    }

    fun saveIsBiometricSaved(context: Context, isBiometricEnabled: Boolean) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tags["IS_BIOMETRIC_ENABLED"], isBiometricEnabled)
        editor.apply()
    }

    fun getIsBiometricEnabled(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(tags["IS_BIOMETRIC_ENABLED"], false)
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

    fun saveIdentityData(context: Context, json: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["IDENTITY"], json)
        editor.apply()
    }

    fun getIdentityData(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["IDENTITY"], "")
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

    fun saveIsPassportScanned(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tags["accessToken"], true)
        editor.apply()
    }

    fun getIsPassportScanned(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(tags["accessToken"], false)
    }

    fun getVC(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["VC"], null)
    }

    fun saveVC(context: Context, rawData: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["VC"], rawData)
        editor.apply()
    }

    fun saveClaimId(context: Context, claimId: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tags["CLAIM_ID"], claimId)
        editor.apply()
    }

    fun geiClaimId(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(tags["CLAIM_ID"], "")
    }


}