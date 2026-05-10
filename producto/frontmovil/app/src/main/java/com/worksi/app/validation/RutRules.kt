package com.worksi.app.validation

object RutRules {
    fun normalize(raw: String?): String {
        if (raw == null) return ""
        return raw.trim().replace(".", "")
    }

    fun isValidChileRut(raw: String?): Boolean {
        val n = normalize(raw)
        if (n.isEmpty()) return false
        val dash = n.lastIndexOf('-')
        if (dash < 1 || dash != n.length - 2) return false
        val body = n.substring(0, dash)
        val dv = n.substring(dash + 1)
        if (!body.matches(Regex("[0-9]{7,8}")) || !dv.matches(Regex("[0-9kK]"))) return false
        var factor = 2
        var sum = 0
        for (i in body.length - 1 downTo 0) {
            sum += Character.digit(body[i], 10) * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        val rest = 11 - (sum % 11)
        val expected = when (rest) {
            11 -> '0'
            10 -> 'K'
            else -> ('0'.code + rest).toChar()
        }
        val got = dv[0].uppercaseChar()
        return expected == got
    }
}
