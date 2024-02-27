package org.freedomtool.data.datasource.api

import android.content.Context
import org.freedomtool.R
import org.freedomtool.data.models.RequirementsForVoting
import org.freedomtool.data.models.VotingData

object VotingProvider {
    fun getVotes(context: Context): List<VotingData> {

        val vote1 = VotingData(
            header = context.resources.getString(R.string.pool_1_header),
            description = context.resources.getString(R.string.pool_1),
            dueDate = 1710453600L,
            isPassportRequired = true,
            requirements = RequirementsForVoting("RUS", 18)
        )

        val vote2 = VotingData(
            header = context.resources.getString(R.string.pool_2_header),
            description = context.resources.getString(R.string.pool_2),
            dueDate = 1709441600L,
            isPassportRequired = true,
            isManifest = true,
            requirements = RequirementsForVoting(age = 21)
        )
        return  listOf(vote1, vote2)

    }
}