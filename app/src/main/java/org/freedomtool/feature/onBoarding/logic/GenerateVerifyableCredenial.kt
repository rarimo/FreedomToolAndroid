package org.freedomtool.feature.onBoarding.logic

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import identity.Identity
import identity.Identity_
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.freedomtool.R
import org.freedomtool.contracts.SRegistration
import org.freedomtool.data.models.Data
import org.freedomtool.data.models.IdCardSod
import org.freedomtool.data.models.IdentityData
import org.freedomtool.data.models.InputsPassport
import org.freedomtool.data.models.Payload
import org.freedomtool.data.models.SendCalldataRequest
import org.freedomtool.data.models.SendCalldataRequestData
import org.freedomtool.data.models.VotingInputs
import org.freedomtool.data.models.ZkProof
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.logic.stateProvider.StateProviderImpl
import org.freedomtool.utils.ZKPTools
import org.freedomtool.utils.ZKPUseCase
import org.freedomtool.utils.addCharAtIndex
import org.freedomtool.utils.decodeHexString
import org.freedomtool.utils.nfc.SecurityUtil
import org.freedomtool.utils.nfc.model.EDocument
import org.freedomtool.utils.toBitArray
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.tx.gas.DefaultGasProvider
import java.time.LocalDate


class GenerateVerifyableCredenial {
    @OptIn(ExperimentalStdlibApi::class)
    fun generateIdentity(
        context: Context,
        apiProvider: ApiProvider,
        eDocument: EDocument
    ): Single<Payload> {

        return Single.create { it ->
            val hexString = eDocument.sod
            if (hexString.isNullOrEmpty())
                throw IllegalStateException("No Sod File found")

            val byteArray = hexString.decodeHexString()
                .inputStream()

            val zkpTools = ZKPTools(context)

            val sodFile = SODFileOwn(byteArray)

            val dg1 = eDocument.dg1

            val current = LocalDate.now()


            val nextDate = current.plusYears(1)
            val inputs = InputsPassport(
                `in` = (dg1!!).toBitArray().toCharArray().map { it1 -> it1.digitToInt() },
                currDateDay = current.dayOfMonth,
                credValidDay = nextDate.dayOfMonth,
                credValidMonth = nextDate.month.value,
                credValidYear = nextDate.year.toString().takeLast(2).toInt(),
                currDateMonth = current.month.value,
                currDateYear = current.year.toString().takeLast(2).toInt(),
                ageLowerbound = 18
            )


            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonInputs = gson.toJson(inputs).toByteArray()


            val zkp: ZkProof = when (sodFile.digestAlgorithm) {
                "SHA-256" -> {
                    ZKPUseCase(context).generateZKP(
                        R.raw.passport_verification_sha_256_zkey,
                        R.raw.passport_verification_sha_256,
                        jsonInputs,
                        zkpTools::passportVerification256
                    )
                }

                "SHA-1" -> {
                    ZKPUseCase(context).generateZKP(
                        R.raw.passport_verification_sha_1_zkey,
                        R.raw.passport_verification_sha_1,
                        jsonInputs,
                        zkpTools::passportVerification1
                    )
                }

                else -> {
                    throw Exception("Invalid digest algorithm: ${sodFile.digestAlgorithm}")
                }
            }

            val identity = Identity.newIdentity(
                StateProviderImpl(context, apiProvider)
            )

            val signedAttributes = sodFile.eContent
            val algorithm = sodFile.digestEncryptionAlgorithm
            val certificate = sodFile.docSigningCertificate
            var pemFile = SecurityUtil.convertToPem(certificate)

            val index = pemFile.indexOf("-----END CERTIFICATE-----")
            pemFile = pemFile.addCharAtIndex('\n', index)
            val encapsulatedContent = sodFile.readASN1Data()!!.toHexString().substring(8)

            val payload = Payload(
                Data(
                    id = identity.did,
                    zkproof = zkp,
                    document_sod = IdCardSod(
                        signed_attributes = signedAttributes.toHexString(),
                        algorithm = algorithm,
                        signature = sodFile.encryptedDigest.toHexString(),
                        pem_file = pemFile,
                        encapsulated_content = encapsulatedContent
                    )
                )
            )


            val passportVerificationProof: String = gson.toJson(payload)

            Log.i("Payload", passportVerificationProof)

            val response = apiProvider
                .circuitBackend
                .createIdentity(payload)
                .blockingGet()

            val timestamp = System.currentTimeMillis() / 1000

            Log.i("Payload", gson.toJson(response))



            SecureSharedPrefs.saveIssuerDid(context, response.data.attributes.issuer_did)
            val identityToSave = IdentityData(
                identity.secretHex,
                identity.secretKeyHex,
                identity.nullifierHex,
                (timestamp).toString()
            )


            SecureSharedPrefs.savePassportVerificationProof(
                context,
                passportVerificationProof.toByteArray().toHexString()
            )

            SecureSharedPrefs.saveIdentityData(context, identityToSave.toJson())

            it.onSuccess(payload)
        }
    }

    //TODO implement this method
    @OptIn(ExperimentalStdlibApi::class)
    fun vote(context: Context, apiProvider: ApiProvider, vote: String): Completable {
        val identity = createIdentity(context, apiProvider)
        val contractAddress = "0xFc86C6F2483bef470C38e4816E371f6bc996FcF3"
        val ecKeyPair = Keys.createEcKeyPair()

        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()

        val zkpTools = ZKPTools(context)

        return Completable.create {
            //val root = contract.root.send()
            val commitmentIndex = identity.commitmentIndex

            Log.i("commitmentIndex", commitmentIndex.toHexString())

            val siblings = mutableListOf<String>()


            val votingInputs = VotingInputs(
                root = "0x" + "",
                vote = vote,
                votingAddress = contractAddress,
                secret = identity.secretIntStr,
                nullifier = identity.nullifierIntStr,
                siblings = siblings
            )

            val gson = GsonBuilder().setPrettyPrinting().create()
            val inputs = gson.toJson(votingInputs)

            Log.i("INPUTS VOTE", inputs)

            val zkp = ZKPUseCase(context).generateZKP(
                R.raw.vote_smt_zkey,
                R.raw.vote_smt,
                inputs.toByteArray(),
                zkpTools::voteSMT
            )

            Log.i("ZKP", gson.toJson(zkp))
        }
    }

    private fun createIdentity(context: Context, apiProvider: ApiProvider): Identity_ {
        val identityRaw = SecureSharedPrefs.getIdentityData(context)!!
        val identityData = IdentityData.fromJson(identityRaw)
        val identity = Identity.newIdentityWithData(
            identityData.secretKeyHex,
            identityData.secretHex,
            identityData.nullifierHex,
            StateProviderImpl(context, apiProvider)
        )
        return identity
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun register(
        context: Context,
        apiProvider: ApiProvider,
        votingAddress: String
    ): Observable<Int> {

        val identity = createIdentity(context, apiProvider)
        val gson = Gson()

        return Observable.create {

            val ecKeyPair = Keys.createEcKeyPair()
            val credentials = Credentials.create(ecKeyPair)
            val gasProvider = DefaultGasProvider()

            val contract = SRegistration.load(
                votingAddress,
                apiProvider.web3,
                credentials,
                gasProvider
            )

            val zkpTools = ZKPTools(context)
            val issuerDid = SecureSharedPrefs.getIssuerDid(context)!!
            val schemaJson = zkpTools.openRawResourceAsByteArray(R.raw.registration)

            val identityRaw = SecureSharedPrefs.getIdentityData(context)!!
            val identityData = IdentityData.fromJson(identityRaw)

            it.onNext(0)
            if (SecureSharedPrefs.getVC(context) == null) {
                while (!isFinalized(identity, identityData, issuerDid)) {
                    Thread.sleep(10 * 1000)
                }

                val claimOfferResponse =
                    apiProvider.circuitBackend.claimOffer(identity.did).blockingGet()

                val rawClaimOfferResponse = gson.toJson(claimOfferResponse)

                SecureSharedPrefs.saveVC(context, rawClaimOfferResponse)

                identity.initVerifiableCredentials(rawClaimOfferResponse.toByteArray())

            } else {
                val vC = SecureSharedPrefs.getVC(context)!!.toByteArray()
                identity.initVerifiableCredentials(vC)
            }

            it.onNext(1)

            val issuerAuthority = SecureSharedPrefs.getIssuerAuthority(context)

            if (!SecureSharedPrefs.checkFinalizes(context, votingAddress)) {

                Log.i("HERE", "HELP ME")
                val callData: ByteArray = identity.register(
                    "https://rpc-api.node1.mainnet-beta.rarimo.com",
                    issuerDid,
                    votingAddress,
                    schemaJson,
                    "4903594"//getIssuingAuthorityCode(issuerAuthority!!)
                )

                Log.i("HERE", "HELP ME pls")

                val calldataRequest =
                    SendCalldataRequest(SendCalldataRequestData("0x" + callData.toHexString()))


                val resp =
                    apiProvider.circuitBackend.sendRegistration(calldataRequest).blockingGet()

                SecureSharedPrefs.addFinalizationVote(context, votingAddress)
                it.onNext(2)
            }
            it.onNext(2)

            while (contract.commitments(identity.commitment).send()) {
                Thread.sleep(10 * 1000)
            }

            it.onNext(3)

            it.onComplete()

        }
    }

    private fun isFinalized(
        identity: Identity_,
        identityData: IdentityData,
        issuerDid: String
    ): Boolean {
        try {
            val res = identity.isFinalized(
                "https://rpc-api.node1.mainnet-beta.rarimo.com",
                issuerDid,
                identityData.timeStamp.toLong(),
            )
            Log.i("adawdawf", identityData.timeStamp)
            Log.i("ISFINITE", res.toString())
            return res
        } catch (e: Exception) {
            throw e
        }

    }
}
