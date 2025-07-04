package org.freedomtool.data.datasource.api

import android.util.Log
import io.reactivex.Single
import org.freedomtool.base.BaseConfig
import org.freedomtool.contracts.RegistrationVerifier
import org.freedomtool.contracts.SRegistration
import org.freedomtool.data.models.RequirementsForVoting
import org.freedomtool.data.models.VotingData
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.utils.ObservableTransformers
import org.freedomtool.utils.isEnded
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

object VotingProvider {
    fun getVotes(apiProvider: ApiProvider): Single<Pair<List<VotingData>, List<VotingData>>> {
        return Single.fromCallable {

            val web3j = apiProvider.web3
            val ecKeyPair = Keys.createEcKeyPair()

            val credentials = Credentials.create(ecKeyPair)
            val gasProvider = DefaultGasProvider()

            val contract = org.freedomtool.contracts.RegistrationVoting.load(
                BaseConfig.REGISTRATION_ADDRESS, web3j, credentials, gasProvider
            )

            val numberOfVoting = contract.poolCountByProposerAndType(
                BaseConfig.PROPOSAL_ADDRESS, BaseConfig.REGISTRATION_TYPE
            ).send()
            val resp = contract.listPoolsByProposerAndType(
                BaseConfig.PROPOSAL_ADDRESS,
                BaseConfig.REGISTRATION_TYPE,
                numberOfVoting.minus(BigInteger.valueOf(2L)),
                BigInteger.valueOf(2L)
            ).send()

            val voteList = mutableListOf<VotingData>()
            val voteListEnded = mutableListOf<VotingData>()

            val registrationDataListSingle = resp.map { registrationAddress ->
                Single.fromCallable {
                    val registration = SRegistration.load(
                        registrationAddress as String, web3j, credentials, gasProvider
                    )

                    val addressVerifier = registration.registerVerifier().send()
                    Log.i("Registration", addressVerifier)
                    val registrationVerifier = RegistrationVerifier.load(
                        addressVerifier, web3j, credentials, gasProvider
                    )

                    Log.i("RegistrationVerifier", addressVerifier)
                    Log.i("SRegistration", registrationAddress as String)

                    val arrayOfCountries = registrationVerifier.listIssuingAuthorityWhitelist(
                        BigInteger.ZERO, BigInteger.valueOf(100L)
                    ).send()

                    val data = registration.registrationInfo().send()
                    val (url, time, registeredCount) = data

                    Log.i("URL", url)
                    val registrationData =
                        apiProvider.circuitBackend.getRegistrationData(url).blockingGet()

                    VotingData(
                        header = registrationData.name,
                        excerpt = registrationData.excerpt,
                        description = registrationData.description,
                        contractAddress = resp[1] as String,
                        dueDate = time.commitmentEndTime.toLong(),
                        isPassportRequired = true,
                        requirements = RequirementsForVoting(
                            arrayOfCountries as List<BigInteger>, 18
                        ),
                        isManifest = true,
                        isActive = registrationData.isActive == true && !isEnded(time.commitmentEndTime.toLong()),
                        votingCount = registeredCount.totalRegistrations.toLong(),
                        isReferendum = true,
                        contractNo = resp[1] as String,
                        contractYes = resp[0] as String,
                        metadata = registrationData.metadata
                    )
                }.compose(ObservableTransformers.defaultSchedulersSingle()).blockingGet()
            }.toList()


            registrationDataListSingle.forEach { votingData ->
                if (isEnded(votingData.dueDate!!)) {

                    voteListEnded.add(votingData)
                } else {
                    voteList.add(votingData)
                }
            }

            if (voteList.isEmpty()) {
                return@fromCallable Pair(listOf(), voteListEnded.reversed())
            }

            val prodContract = voteList.reversed().first()
            prodContract.metadataNo = voteList[1].metadata
            prodContract.metadataYes = voteList[0].metadata

            val totalCount = (voteList[0].votingCount + voteList[1].votingCount)

            Log.i("Total Count", totalCount.toString())
            Log.i("voteList[0]", voteList[0].votingCount.toString())
            Log.i("voteList[1]", voteList[1].votingCount.toString())
            prodContract.votingCount = totalCount

            Log.i("YES", prodContract.metadataYes.toString())
            Log.i("No", prodContract.metadataNo.toString())

            Pair(listOf(prodContract), voteListEnded.reversed())
        }
    }


}

//            val vote1 = VotingData(
//                header = context.resources.getString(R.string.pool_1_header),
//                description = context.resources.getString(R.string.pool_1),
//                dueDate = 1710453600L,
//                isPassportRequired = true,
//                requirements = RequirementsForVoting("RUS", 21),
//                options = listOf(
//                    OptionsData(context.getString(R.string.name1), 0),
//                    OptionsData(context.getString(R.string.name2), 1),
//                    OptionsData(context.getString(R.string.name3), 2)
//                )
//            )
