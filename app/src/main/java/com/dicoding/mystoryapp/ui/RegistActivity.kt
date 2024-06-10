package com.dicoding.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivityRegistBinding
import com.dicoding.mystoryapp.data.LoginUserData
import com.dicoding.mystoryapp.data.RegisterUserData
import com.dicoding.mystoryapp.viewmodel.LoginVM
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.UserVMFactory

class RegistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistBinding

    private val loginVM: LoginVM by lazy {
        ViewModelProvider(this)[LoginVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.signupBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handleUserActions()

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]
        authSplashVM.getLoginStatus().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this@RegistActivity, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginVM.registerStatus.observe(this) { registerStatus ->
            handleRegisterResponse(
                loginVM.isErrorRegist,
                registerStatus
            )
        }

        loginVM.isRegistering.observe(this) {
            showLoading(it)
        }

        loginVM.loginStatus.observe(this) { loginStatus ->
            handleLoginResponse(
                loginVM.isErrorLogin,
                loginStatus,
                authSplashVM
            )
        }

        loginVM.isLoggingIn.observe(this) {
            showLoading(it)
        }
    }

    private fun handleLoginResponse(
        isError: Boolean,
        message: String,
        authSplashVM: AuthSplashVM
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val authUser = loginVM.authenticatedUser.value
            authSplashVM.storeLoginStatus(true)
            authSplashVM.storeToken(authUser?.loginResult!!.token)
            authSplashVM.storeName(authUser.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRegisterResponse(
        isError: Boolean,
        message: String,
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val authenticatedUser = LoginUserData(
                binding.EmailRegistration.text.toString(),
                binding.PassRegistration.text.toString()
            )
            loginVM.getResponseLogin(authenticatedUser)
        } else {
            if (message == "1") {
                binding.EmailRegistration.setErrorMessage(resources.getString(R.string.emailIsInUse), binding.EmailRegistration.text.toString())
                Toast.makeText(this, resources.getString(R.string.emailIsInUse), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUserActions() {
        binding.seeRegistPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.PassRegistration.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.confirmPassRegistration.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.PassRegistration.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.confirmPassRegistration.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }

            binding.PassRegistration.text?.let { binding.PassRegistration.setSelection(it.length) }
            binding.confirmPassRegistration.text?.let { binding.confirmPassRegistration.setSelection(it.length) }
        }

        binding.registrationBtn.setOnClickListener {
            binding.apply {
                NameRegistration.clearFocus()
                EmailRegistration.clearFocus()
                PassRegistration.clearFocus()
                confirmPassRegistration.clearFocus()
            }

            if (binding.NameRegistration.nameMatched && binding.EmailRegistration.emailMatched && binding.PassRegistration.passMatched && binding.confirmPassRegistration.confirmPassMatched) {
                val registerUserData = RegisterUserData(
                    name = binding.NameRegistration.text.toString().trim(),
                    email = binding.EmailRegistration.text.toString().trim(),
                    password = binding.PassRegistration.text.toString().trim()
                )

                loginVM.getResponseRegister(registerUserData)
            } else {
                if (!binding.NameRegistration.nameMatched) binding.NameRegistration.error =
                    resources.getString(R.string.nullName)
                if (!binding.EmailRegistration.emailMatched) binding.EmailRegistration.error =
                    resources.getString(R.string.nullEmail)
                if (!binding.PassRegistration.passMatched) binding.PassRegistration.error =
                    resources.getString(R.string.nullPass)
                if (!binding.confirmPassRegistration.confirmPassMatched) binding.confirmPassRegistration.error =
                    resources.getString(R.string.nullConfirmPass)

                Toast.makeText(this, R.string.failedLogin, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}