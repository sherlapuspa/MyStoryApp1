package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.data.ResponseStory
import com.dicoding.mystoryapp.data.ListStoryItem
import retrofit2.Call
import retrofit2.Response

class ListStoryVM : ViewModel() {

    var userStories: List<ListStoryItem> = listOf()

    private val _msg = MutableLiveData<String>()
    val message: LiveData<String> = _msg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isError: Boolean = false

    fun getUserStories(token: String) {
        _isLoading.value = true
        val api = ApiConfig.getApiService().getAllStories("Bearer $token")
        api.enqueue(object : retrofit2.Callback<ResponseStory> {
            override fun onResponse(call: Call<ResponseStory>, response: Response<ResponseStory>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    isError = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        userStories = responseBody.listStory
                    }
                    _msg.value = responseBody?.message.toString()

                } else {
                    isError = true
                    _msg.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                _isLoading.value = false
                isError = true
                _msg.value = t.message.toString()
            }
        })
    }
}
