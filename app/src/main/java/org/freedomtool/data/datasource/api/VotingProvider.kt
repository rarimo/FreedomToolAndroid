package org.freedomtool.data.datasource.api

import io.reactivex.Single
import org.freedomtool.contracts.Registration
import org.freedomtool.data.models.RequirementsForVoting
import org.freedomtool.data.models.VotingData
import org.freedomtool.di.providers.ApiProvider
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
            val contractAddress = "0x1d84cFd4839fE92dAe8E1F8F777010c08a60013C"
            val proposalAddress = "0xF5Ca28acbBC7DFFfFFf714e1F306A803037Bdad2"
            val credentials = Credentials.create(ecKeyPair)
            val gasProvider = DefaultGasProvider()

            val contract = org.freedomtool.contracts.RegistrationVoting.load(
                contractAddress,
                web3j,
                credentials,
                gasProvider
            )

            val numberOfVoting = contract.poolCountByProposer(proposalAddress).send()
            val resp = contract.listPoolsByProposer(
                proposalAddress,
                numberOfVoting.min(BigInteger.ZERO),
                BigInteger.TEN
            ).send()

            val voteList = mutableListOf<VotingData>()
            val voteListEnded = mutableListOf<VotingData>()


            resp.map { registrationAddress ->
                val registration = Registration.load(
                    registrationAddress as String,
                    web3j,
                    credentials,
                    gasProvider
                )
                val data = registration.registrationInfo().send()
                val (url, time, registeredCount) = data


                val registrationData =
                    apiProvider.circuitBackend.getRegistrationData(url).blockingGet()


                val votingData = VotingData(
                    header = registrationData.name,
                    excerpt = registrationData.excerpt,
                    description = registrationData.description,
                    contractAddress = registrationAddress,
                    dueDate = time.commitmentEndTime.toLong(),
                    isPassportRequired = true,
                    requirements = RequirementsForVoting("UKR", 18),
                    isManifest = true,
                    votingCount = registeredCount.totalRegistrations.toLong(),
                    isActive = registrationData.isActive
                )

                if (isEnded(time.commitmentEndTime.toLong())) {
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
//                requirements = RequirementsForVoting("UKR", 18),
//                options = listOf(
//                    OptionsData(context.getString(R.string.name1), 0),
//                    OptionsData(context.getString(R.string.name2), 1),
//                    OptionsData(context.getString(R.string.name3), 2)
//                )
//            )

