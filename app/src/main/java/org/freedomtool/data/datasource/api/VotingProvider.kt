package org.freedomtool.data.datasource.api

import android.content.Context
import org.freedomtool.R
import org.freedomtool.data.models.OptionsData
import org.freedomtool.data.models.RequirementsForVoting
import org.freedomtool.data.models.VotingData

object VotingProvider {
    fun getVotes(context: Context): List<VotingData> {

        val vote1 = VotingData(
            header = context.resources.getString(R.string.pool_1_header),
            description = context.resources.getString(R.string.pool_1),
            dueDate = 1710453600L,
            isPassportRequired = true,
            requirements = RequirementsForVoting("RUS", 18),
            options = listOf(
                OptionsData(context.getString(R.string.name1), 0),
                OptionsData(context.getString(R.string.name2), 1),
                OptionsData(context.getString(R.string.name3), 2)
            )
        )

        return listOf(vote1)

    }
}