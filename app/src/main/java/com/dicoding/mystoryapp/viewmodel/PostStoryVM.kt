package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.data.ResponsePostStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostStoryVM : ViewModel() {
    private val _msg = MutableLiveData<String>()
    val message: LiveData<String> = _msg
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun upload(photo: MultipartBody.Part, desc: RequestBody, token: String) {
        _isLoading.value = true
        val srvc = ApiConfig.getApiService().postNewStory(
            photo, desc,
            "Bearer $token"
        )
        srvc.enqueue(object : Callback<ResponsePostStory> {
            override fun onResponse(
                call: Call<ResponsePostStory>,
                response: Response<ResponsePostStory>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _msg.value = responseBody.message
                    }
                } else {
                    _msg.value = response.message()

                }
            }

            override fun onFailure(call: Call<ResponsePostStory>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message
            }
        })
    }
}