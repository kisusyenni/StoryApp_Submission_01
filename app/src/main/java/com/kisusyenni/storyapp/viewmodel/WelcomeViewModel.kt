package com.kisusyenni.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.Session

class WelcomeViewModel (private val pref: SessionPreference): ViewModel() {
    fun getSession(): LiveData<Session> {
        return pref.getSession().asLiveData()
    }
}