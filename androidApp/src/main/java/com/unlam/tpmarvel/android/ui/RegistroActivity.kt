package com.unlam.tpmarvel.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.unlam.tpmarvel.android.R
import com.unlam.tpmarvel.data.ValidarContraseña
import com.unlam.tpmarvel.android.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupPasswordValidation()

        binding.btnRegistrarse.setOnClickListener {
            registerUser()
        }

        binding.btnVolver.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    private fun setupPasswordValidation() {
        binding.etPassword.addTextChangedListener {
            val validation = ValidarContraseña.validate(it.toString())
            updatePasswordRequirements(validation)
            validatePasswords()
        }

        binding.etConfirmPassword.addTextChangedListener {
            validatePasswords()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePasswordRequirements(validation: ValidarContraseña.ValidationResult) {
        binding.passwordRequirements.removeAllViews()
        validation.requirements.forEach { requirement ->
            val requirementView = TextView(this).apply {
                text = "• ${requirement.description}"
                setTextColor(if (requirement.isMet)
                    getColor(R.color.marvel_blue)
                else getColor(R.color.marvel_red))
            }
            binding.passwordRequirements.addView(requirementView)
        }
    }

    private fun validatePasswords() {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val validation = ValidarContraseña.validate(password)

        binding.btnRegistrarse.isEnabled = validation.isValid &&
                password == confirmPassword &&
                password.isNotEmpty() &&
                binding.etEmail.text.toString().isNotEmpty()
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                val validation = ValidarContraseña.validate(password)
                if (validation.isValid) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigateToMainActivity()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error al registrar usuario: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "La contraseña no cumple con los requisitos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}