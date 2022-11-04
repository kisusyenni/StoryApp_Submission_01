package com.kisusyenni.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonParser
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.ErrorMessage
import com.kisusyenni.storyapp.data.source.remote.network.ApiConfig
import com.kisusyenni.storyapp.data.source.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: SessionPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name = name, email = email, password = password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _error.value = ErrorMessage(
                        error = response.body()?.error ?: false,
                        message = response.body()?.message ?: ""
                    )
                } else {
                    val jsonObj = JsonParser().parse(response.errorBody()!!.charStream().readText()).asJsonObject
                    val error=jsonObj.get("error").asBoolean
                    val message=jsonObj.get("message").asString

                    val result = ErrorMessage(
                        error = error,
                        message = message
                    )
                    _error.value = result
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }


    init {
        _isLoading.value = false
    }

    companion object{
        private const val TAG = "RegisterViewModel"
    }
}