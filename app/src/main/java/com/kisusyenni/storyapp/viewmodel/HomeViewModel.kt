package com.kisusyenni.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonParser
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.ErrorMessage
import com.kisusyenni.storyapp.data.source.local.entity.Session
import com.kisusyenni.storyapp.data.source.remote.network.ApiConfig
import com.kisusyenni.storyapp.data.source.remote.response.ListStoryItem
import com.kisusyenni.storyapp.data.source.remote.response.StoriesResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: SessionPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val _isLogout = MutableLiveData<Boolean>()
    val isLogout: LiveData<Boolean> = _isLogout

    private val _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    fun getSession(): LiveData<Session> {
        return pref.getSession().asLiveData()
    }

    fun getStories(token: String){
        _isLoading.value = true
        if (token != "") {
            val client = ApiConfig.getApiService().getStories("Bearer $token")
            client.enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listStories.value = response.body()?.listStory
                        _error.value = ErrorMessage(error=false, message = "")
                    } else {

                        val jsonObj = JsonParser().parse(
                            response.errorBody()!!.charStream().readText()
                        ).asJsonObject
                        val error = jsonObj.get("error").asBoolean
                        val message = jsonObj.get("message").asString

                        val result = ErrorMessage(
                            error = error,
                            message = message
                        )
                        _error.value = result
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        } else {
            _isLoading.value = false
            _error.value = ErrorMessage(true, "")
        }
    }

    fun removeSession() {
        _isLogout.value = true
        viewModelScope.launch {
            pref.removeSession()
        }
    }

    init {
        _isLoading.value = false
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}