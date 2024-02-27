package org.freedomtool.data.models
data class InputsPassport(
    val `in`: List<Int>,
    val currDateYear: Int,
    val currDateMonth: Int,
    val currDateDay: Int,
    val credValidYear: Int,
    val credValidMonth: Int,
    val credValidDay: Int,
    val ageLowerbound: Int
)
