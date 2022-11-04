package com.kisusyenni.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonParser
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.ErrorMessage
import com.kisusyenni.storyapp.data.source.local.entity.Session
import com.kisusyenni.storyapp.data.source.remote.network.ApiConfig
import com.kisusyenni.storyapp.data.source.remote.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: SessionPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    fun getSession(): LiveData<Session> {
        return pref.getSession().asLiveData()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email = email, password = password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = Session(
                        name = response.body()?.loginResult?.name ?: "",
                        token = response.body()?.loginResult?.token ?: "",
                        userId = response.body()?.loginResult?.userId ?: "",
                    )
                    _isLogin.value = true
                    saveSession(result)
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
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun saveSession(session: Session) {
        viewModelScope.launch {
            pref.saveSession(session)
        }
    }

    init {
        _isLoading.value = false
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}