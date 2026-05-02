package com.worksi.app.validation

object PasswordPolicy {
    private val pattern =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}\$")

    fun matches(password: String): Boolean = password.isNotEmpty() && pattern.matches(password)
}
