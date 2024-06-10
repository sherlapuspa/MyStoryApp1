package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.UserPreferencesManager
import kotlinx.coroutines.launch

class AuthSplashVM(private val userPref: UserPreferencesManager) : ViewModel() {

    fun getLoginStatus(): LiveData<Boolean> {
        return userPref.getLoginStatus().asLiveData()
    }

    fun storeLoginStatus(loginSession: Boolean) {
        viewModelScope.launch {
            userPref.storeLoginStatus(loginSession)
        }
    }

    fun getToken(): LiveData<String> {
        return userPref.getToken().asLiveData()
    }

    fun storeToken(token: String) {
        viewModelScope.launch {
            userPref.storeToken(token)
        }
    }

    fun getName(): LiveData<String> {
        return userPref.getName().asLiveData()
    }

    fun storeName(token: String) {
        viewModelScope.launch {
            userPref.storeName(token)
        }
    }

    fun resetLoginData() {
        viewModelScope.launch {
            userPref.resetLoginData()
        }
    }

}