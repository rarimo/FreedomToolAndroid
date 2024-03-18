package org.freedomtool.data.datasource.api

import android.util.Log
import io.reactivex.Observable
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
                BaseConfig.REGISTRATION_ADDRESS,
                web3j,
                credentials,
                gasProvider
            )

            val numberOfVoting = contract.poolCountByProposer(BaseConfig.PROPOSAL_ADDRESS).send()
            val resp = contract.listPoolsByProposer(
                BaseConfig.PROPOSAL_ADDRESS,
                numberOfVoting.minus(BigInteger.ONE),
                BigInteger.ONE
            ).send()

            val voteList = mutableListOf<VotingData>()
            val voteListEnded = mutableListOf<VotingData>()

            val registrationDataListSingle = Observable.fromIterable(resp)
                .flatMapSingle { registrationAddress ->
                    Single.fromCallable {
                        val registration = SRegistration.load(
                            registrationAddress as String,
                            web3j,
                            credentials,
                            gasProvider
                        )

                        val addressVerifier = registration.registerVerifier().send()
                        Log.i("Registration", addressVerifier)
                        val registrationVerifier =
                            RegistrationVerifier.load(
                                addressVerifier,
                                web3j,
                                credentials,
                                gasProvider
                            )

                        val arrayOfCountries = registrationVerifier.listIssuingAuthorityWhitelist(
                            BigInteger.ZERO,
                            BigInteger.valueOf(100L)
                        ).send()

                        val data = registration.registrationInfo().send()
                        val (url, time, registeredCount) = data

                        val registrationData =
                            apiProvider.circuitBackend.getRegistrationData(url).blockingGet()


                        VotingData(
                            header = registrationData.name,
                            excerpt = registrationData.excerpt,
                            description = registrationData.description,
                            contractAddress = registrationAddress,
                            dueDate = time.commitmentEndTime.toLong(),
                            isPassportRequired = true,
                            requirements = RequirementsForVoting(
                                arrayOfCountries as List<BigInteger>,
                                18
                            ),
                            isManifest = true,
                            isActive = registrationData.isActive == true && !isEnded(time.commitmentEndTime.toLong()),
                            votingCount = registeredCount.totalRegistrations.toLong()
                        )
                    }.compose(ObservableTransformers.defaultSchedulersSingle())
                }
                .toList()

            val registrationDataList = registrationDataListSingle.blockingGet()

            registrationDataList.forEach { votingData ->
                if (isEnded(votingData.dueDate!!)) {
                    voteListEnded.add(votingData)
                } else {
                    voteList.add(votingData)
                }
            }

            Pair(voteList, voteListEnded)
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

