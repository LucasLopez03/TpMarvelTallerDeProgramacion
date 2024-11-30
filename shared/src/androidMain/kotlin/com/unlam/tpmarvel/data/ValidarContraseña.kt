package com.unlam.tpmarvel.data

import java.util.regex.Pattern

class ValidarContraseña {
    companion object {
        private const val MIN_LENGTH = 8
        private val PATTERN_SPECIAL = Pattern.compile("[!@#\$%^&*(),.?\":{}|<>]")
        private val PATTERN_UPPERCASE = Pattern.compile("[A-Z]")
        private val PATTERN_LOWERCASE = Pattern.compile("[a-z]")
        private val PATTERN_DIGIT = Pattern.compile("\\d")

        fun validate(password: String): ValidationResult {
            val requirements = mutableListOf<Requirement>()

            requirements.add(
                Requirement(
                "Mínimo $MIN_LENGTH caracteres",
                password.length >= MIN_LENGTH
            )
            )
            requirements.add(
                Requirement(
                "Al menos una mayúscula",
                PATTERN_UPPERCASE.matcher(password).find()
            )
            )
            requirements.add(
                Requirement(
                "Al menos una minúscula",
                PATTERN_LOWERCASE.matcher(password).find()
            )
            )
            requirements.add(
                Requirement(
                "Al menos un número",
                PATTERN_DIGIT.matcher(password).find()
            )
            )
            requirements.add(
                Requirement(
                "Al menos un carácter especial",
                PATTERN_SPECIAL.matcher(password).find()
            )
            )

            return ValidationResult(
                isValid = requirements.all { it.isMet },
                requirements = requirements
            )
        }
    }

    data class Requirement(
        val description: String,
        val isMet: Boolean
    )

    data class ValidationResult(
        val isValid: Boolean,
        val requirements: List<Requirement>
    )
}