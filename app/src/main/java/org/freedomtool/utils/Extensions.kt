package org.freedomtool.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun String.decodeHexString(): ByteArray {
    check(length % 2 == 0) {
        "Must have an even length"
    }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun String.toBitArray(): String {
    val stringBuilder = StringBuilder()

    for (char in this) {
        val binaryRepresentation = char.toInt().toString(2)
        // Ensure each character is represented by 8 bits by padding zeros if necessary
        val paddedBinary = binaryRepresentation.padStart(8, '0')
        stringBuilder.append(paddedBinary)
    }

    return stringBuilder.toString()
}

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

fun ViewGroup.inflate(layoutRes: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attach) ?: throw IllegalArgumentException(
        "ViewHolder not found, view = null"
    )

fun <T> unSafeLazy(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)