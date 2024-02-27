package org.freedomtool.logic.stateProvider

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import identity.StateProvider
import okhttp3.ResponseBody
import org.freedomtool.R
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.utils.ZKPTools
import org.freedomtool.utils.ZKPUseCase
import java.util.Locale


class StateProviderImpl(val context: Context, val apiProvider: ApiProvider) : StateProvider {
    override fun fetch(url: String?, method: String?, body: String?): ByteArray {

        val response: ResponseBody = if (method!!.toLowerCase(Locale.getDefault()) == "post") {
            apiProvider.circuitBackend
                .fetchForProofPost(url!!, body!!).blockingGet()
        } else if (method.toLowerCase(Locale.getDefault()) == "get") {
            apiProvider.circuitBackend
                .fetchForProofGet(url!!).blockingGet()
        } else {
            throw IllegalStateException("No method for fetch")
        }

        return response.string().toByteArray().clone()
    }

    override fun getGISTProof(userId: String?): ByteArray {

        val response = apiProvider.circuitBackend
            .gistData(userId!!).blockingGet()

        return Gson().toJson(response.data.attributes.gist_proof).toByteArray()
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
}