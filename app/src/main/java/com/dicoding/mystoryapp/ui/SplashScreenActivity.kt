package com.dicoding.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivitySplashScreenBinding
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.UserVMFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]

        authVM.getLoginStatus().observe(this) { isLoggedIn ->

            val animatedText1 =
                ObjectAnimator.ofFloat(binding.tv1, View.ALPHA, 1f, 0f).setDuration(4000)
            val animatedText2 =
                ObjectAnimator.ofFloat(binding.tv2, View.ALPHA, 1f, 0f).setDuration(4000)

            AnimatorSet().apply {
                playTogether(animatedText2, animatedText1)
                start()
            }

            val intent = if (isLoggedIn) {
                Intent(this, ListStoryActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            binding.logoapp.animate()
                .setDuration(4000)
                .alpha(1f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }
        }
    }
}