package org.freedomtool.data.datasource.api

import io.reactivex.Single
import okhttp3.ResponseBody
import org.freedomtool.base.BaseConfig
import org.freedomtool.data.models.ClaimId
import org.freedomtool.data.models.ClaimOfferResponse
import org.freedomtool.data.models.GistData
import org.freedomtool.data.models.Payload
import org.freedomtool.data.models.RegistrationData
import org.freedomtool.data.models.SendCalldataRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CircuitBackendApi {
    @POST(BaseConfig.CREATE_IDENTITY_LINK)
    fun createIdentity(@Body body: Payload): Single<ClaimId>

    @GET(BaseConfig.GIST_DATA_LINK)
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


    @GET(BaseConfig.CLAIM_OFFER_LINK)
    fun claimOffer(
        @Path("Did") identityDid: String,
        @Path("claim_id") claimId: String
    ): Single<ClaimOfferResponse>

    @POST(BaseConfig.SEND_REGISTRATION_LINK)
    fun sendRegistration(@Body body: SendCalldataRequest): Single<ResponseBody>

    @GET
    fun getRegistrationData(@Url url: String): Single<RegistrationData>
}