package com.ekoviso.app.util

import java.text.Normalizer

fun normalize(text: String): String {
    return Normalizer.normalize(text.lowercase().trim(), Normalizer.Form.NFKD)
        .replace(Regex("[^a-z0-9\\s]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun levenshtein(s1: String, s2: String): Int {
    val a = if (s1.length < s2.length) s1 else s2
    val b = if (s1.length < s2.length) s2 else s1
    var prev = IntArray(a.length + 1) { it }
    for (i in 1..b.length) {
        val curr = IntArray(a.length + 1)
        curr[0] = i
        for (j in 1..a.length) {
            curr[j] = minOf(
                prev[j] + 1,
                curr[j - 1] + 1,
                prev[j - 1] + if (b[i - 1] == a[j - 1]) 0 else 1
            )
        }
        prev = curr
    }
    return prev.last()
}

fun similarity(a: String, b: String): Double {
    val maxLen = maxOf(a.length, b.length)
    if (maxLen == 0) return 1.0
    return 1.0 - (levenshtein(a, b).toDouble() / maxLen)
}
