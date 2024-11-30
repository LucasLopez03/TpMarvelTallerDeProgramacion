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
import com.unlam.tpmarvel.android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var loginAttempts = 0
    private var lastLoginAttempt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            navigateToMainActivity()
        }

        setupPasswordValidation()
        setupLoginButton()
        binding.btnRegistro.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun setupPasswordValidation() {
        binding.etPassword.addTextChangedListener {
            val validation = ValidarContraseña.validate(it.toString())
            updatePasswordRequirements(validation)
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

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (canAttemptLogin()) {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigateToMainActivity()
                            } else {
                                loginAttempts++
                                lastLoginAttempt = System.currentTimeMillis()
                                Toast.makeText(this, "Usuario o contraseña incorrecto.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            } else {
                showRateLimitMessage()
            }
        }
    }

    private fun canAttemptLogin(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastLoginAttempt < 60000 && loginAttempts >= 3) {
            return false
        }
        if (currentTime - lastLoginAttempt >= 60000) {
            loginAttempts = 0
        }
        return true
    }

    private fun showRateLimitMessage() {
        val remainingTime = 60 - ((System.currentTimeMillis() - lastLoginAttempt) / 1000)
        Toast.makeText(
            this,
            "Demasiados intentos. Espera $remainingTime segundos.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}