package org.freedomtool.data.datasource.api

import io.reactivex.Single
import okhttp3.ResponseBody
import org.freedomtool.data.models.ClaimId
import org.freedomtool.data.models.ClaimOfferResponse
import org.freedomtool.data.models.GistData
import org.freedomtool.data.models.Payload
import org.freedomtool.data.models.StateInfoResponse
import org.freedomtool.data.models.UserDid
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CircuitBackendApi {
    @POST("https://api.stage.freedomtool.org/integrations/identity-provider-service/v1/create-identity")
    fun createIdentity(@Body body: Payload): Single<ClaimId>

    @GET("https://api.stage.freedomtool.org/integrations/identity-provider-service/v1/gist-data")
    fun gistData(@Query("user_did") user_did: String): Single<GistData>

    @GET
    fun fetchForProofGet(@Url url : String): Single<ResponseBody>

    @POST
    fun fetchForProofPost(@Url url : String, @Body body : String): Single<ResponseBody>

    @GET("https://issuer.polygon.robotornot.mainnet-beta.rarimo.com/v1/credentials/{Did}/urn:uuid:f2c8db83-7e1b-493d-a65e-9939d643e79d")
    fun claimOffer(@Path("Did") issuerDid: String): Single<ClaimOfferResponse>

    @GET("https://rpc-api.node1.mainnet-beta.rarimo.com/rarimo/rarimo-core/identity/state/{issuerIdHex}")
    fun getCoreStateHash(@Path("issuerIdHex") issuerIdHash : String ) : Single<StateInfoResponse>
}