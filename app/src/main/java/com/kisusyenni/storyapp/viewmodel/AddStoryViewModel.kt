package com.kisusyenni.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.gson.JsonParser
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.ErrorMessage
import com.kisusyenni.storyapp.data.source.local.entity.Session
import com.kisusyenni.storyapp.data.source.remote.network.ApiConfig
import com.kisusyenni.storyapp.data.source.remote.response.FileUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: SessionPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    fun getSession(): LiveData<Session> {
        return pref.getSession().asLiveData()
    }

    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody,) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory(
            token = "Bearer $token", file = image, description = description)
        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val error = response.body()?.error ?: false
                    val message = response.body()?.message ?: ""
                    _error.value = ErrorMessage(
                        error = error,
                        message = message
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
            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    init {
        _isLoading.value = false
    }

    companion object{
        private const val TAG = "AddStoryViewModel"
    }
}