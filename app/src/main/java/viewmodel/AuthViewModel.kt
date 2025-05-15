package com.kavyakanaja.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val userEmail: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("kavya_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        // Auto login if already registered before
        val savedEmail = prefs.getString("email", null)
        val savedName  = prefs.getString("name", null)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (isLoggedIn && savedEmail != null) {
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    userEmail  = savedEmail,
                    userName   = savedName ?: ""
                )
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        // Basic validation
        if (name.isBlank()) {
            _state.update { it.copy(error = "Please enter your name") }
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _state.update { it.copy(error = "Please enter a valid email") }
            return
        }
        if (password.length < 6) {
            _state.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(800) // small delay so it feels real
            // Save to SharedPreferences
            prefs.edit()
                .putString("name", name.trim())
                .putString("email", email.trim())
                .putString("password", password)
                .putBoolean("is_logged_in", true)
                .apply()
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    isLoading  = false,
                    userName   = name.trim(),
                    userEmail  = email.trim()
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Please enter email and password") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(600)

            val savedEmail    = prefs.getString("email", null)
            val savedPassword = prefs.getString("password", null)
            val savedName     = prefs.getString("name", "") ?: ""

            // Check if any account exists at all
            if (savedEmail == null || savedPassword == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No account found. Please tap 'Create Account' to register first."
                    )
                }
                return@launch
            }

            // Compare — ignore case for email, exact match for password
            val emailMatch    = email.trim().lowercase() == savedEmail.trim().lowercase()
            val passwordMatch = password == savedPassword

            if (!emailMatch) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Email not found. Please check your email or create a new account."
                    )
                }
                return@launch
            }

            if (!passwordMatch) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Wrong password. Please try again."
                    )
                }
                return@launch
            }

            // Success
            prefs.edit().putBoolean("is_logged_in", true).apply()
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    isLoading  = false,
                    userName   = savedName,
                    userEmail  = savedEmail
                )
            }
        }
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _state.update { AuthState() }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}