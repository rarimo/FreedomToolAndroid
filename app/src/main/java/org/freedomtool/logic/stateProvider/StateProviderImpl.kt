package org.freedomtool.logic.stateProvider

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import identity.StateProvider
import okhttp3.ResponseBody
import org.freedomtool.R
import org.freedomtool.contracts.SRegistration
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.utils.ZKPTools
import org.freedomtool.utils.ZKPUseCase
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger


class StateProviderImpl(val context: Context, val apiProvider: ApiProvider) : StateProvider {
//    override fun fetch(url: String?, method: String?, body: String?): ByteArray {
//
//        val response: ResponseBody = if (method!!.toLowerCase(Locale.getDefault()) == "post") {
//            apiProvider.circuitBackend
//                .fetchForProofPost(url!!, body!!).blockingGet()
//        } else if (method.toLowerCase(Locale.getDefault()) == "get") {
//            apiProvider.circuitBackend
//                .fetchForProofGet(url!!).blockingGet()
//        } else {
//            throw IllegalStateException("No method for fetch")
//        }
//
//        return response.string().toByteArray().clone()
//    }

    override fun fetch(
        url: String?,
        method: String?,
        body: ByteArray?,
        headerKey: String?,
        headerValue: String?
    ): ByteArray {

        Log.i("Fetching", "url: $url\nmethod: $method\nbody: ${body?.decodeToString()}")

        val headers: Map<String, String> =
            if (headerValue.isNullOrEmpty() || headerKey.isNullOrEmpty()) {
                mapOf("foo" to "bar")
            } else {
                mapOf(headerKey to headerValue)
            }

        val response: ResponseBody = if (method!! == "POST") {
            apiProvider.circuitBackend
                .fetchForProofPost(url!!, body!!.decodeToString(), headers).blockingGet()
        } else if (method == "GET") {
            apiProvider.circuitBackend
                .fetchForProofGet(url!!, headers).blockingGet()
        } else {
            throw IllegalStateException("No method for fetch")
        }

        val resp = response.string()
        Log.e("Fetching", resp)

        return resp.toByteArray().clone()

    }

    override fun getGISTProof(userId: String?): ByteArray {

        val response = apiProvider.circuitBackend
            .gistData(userId!!).blockingGet()

        return Gson().toJson(response.data.attributes.gist_proof).toByteArray()
    }

    override fun isUserRegistered(contract: String?, documentNullifier: ByteArray?): Boolean {
        Log.i("HERE", "1")
        val documentNullifierBigUInt = BigInteger(documentNullifier)
        val web3j = apiProvider.web3



        Log.i("CONTRACT", contract.toString())


        val ecKeyPair = Keys.createEcKeyPair()

        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()

        val contractRegister = SRegistration.load(
            contract,
            web3j,
            credentials,
            gasProvider
        )
        Log.i("RES", documentNullifierBigUInt.toString())

        val result = contractRegister.isUserRegistered(documentNullifierBigUInt).send()

        Log.e("BOOL", result.toString())

        return result
    }

    override fun localPrinter(msg: String?) {
        Log.e("LocalPrinterGo", msg!!)
    }

    override fun proveAuthV2(inputs: ByteArray?): ByteArray {
        val zkpTools = ZKPTools(context)
        localPrinter(inputs!!.decodeToString())
        val proof = ZKPUseCase(context).generateZKP(
            R.raw.auth_v2_zkey,
            R.raw.auth_v2,
            inputs,
            zkpTools::witnesscalcAuthV2
        )

        return Gson().toJson(proof).toByteArray()
    }

    override fun proveCredentialAtomicQueryMTPV2OnChainVoting(inputs: ByteArray?): ByteArray {
        val zkpTools = ZKPTools(context)
        val proof = ZKPUseCase(context)
            .generateZKP(
                R.raw.new_zkkey,
                R.raw.new_dat,
                inputs!!,
                zkpTools::credentialAtomicQueryMTPV2OnChainVoting
            )

        return Gson().toJson(proof).toByteArray()
    }
}