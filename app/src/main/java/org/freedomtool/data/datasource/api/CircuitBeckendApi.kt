package org.freedomtool.data.datasource.api

import io.reactivex.Single
import okhttp3.ResponseBody
import org.freedomtool.data.models.ClaimId
import org.freedomtool.data.models.ClaimOfferResponse
import org.freedomtool.data.models.GistData
import org.freedomtool.data.models.Payload
import org.freedomtool.data.models.RegistrationData
import org.freedomtool.data.models.SendCalldataRequest
import org.freedomtool.data.models.StateInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CircuitBackendApi {
    @POST("https://kyc.freedomtool.org/integrations/identity-provider-service/v1/create-identity")
    fun createIdentity(@Body body: Payload): Single<ClaimId>

    @GET("https://kyc.freedomtool.org/integrations/identity-provider-service/v1/gist-data")
    fun gistData(@Query("user_did") user_did: String): Single<GistData>

    @GET
    fun fetchForProofGet(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Single<ResponseBody>

    @POST
    fun fetchForProofPost(
        @Url url: String,
        @Body body: String,
        @HeaderMap headers: Map<String, String>
    ): Single<ResponseBody>


    @GET("https://api.robotornot.rarimo.com/v1/{Did}/claims/{claim_id}/offer")
    fun claimOffer(@Path("Did") identityDid: String, @Path("claim_id") claimId: String): Single<ClaimOfferResponse>

    @POST("https://proofverification.freedomtool.org/integrations/proof-verification-relayer/v1/verify-proof")
    fun sendRegistration(@Body body: SendCalldataRequest): Single<ResponseBody>

    @GET
    fun getRegistrationData(@Url url: String): Single<RegistrationData>
}