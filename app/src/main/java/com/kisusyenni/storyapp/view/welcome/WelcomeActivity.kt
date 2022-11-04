package com.kisusyenni.storyapp.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.Session
import com.kisusyenni.storyapp.databinding.ActivityWelcomeBinding
import com.kisusyenni.storyapp.view.home.HomeActivity
import com.kisusyenni.storyapp.view.login.LoginActivity
import com.kisusyenni.storyapp.view.register.RegisterActivity
import com.kisusyenni.storyapp.viewmodel.ViewModelFactory
import com.kisusyenni.storyapp.viewmodel.WelcomeViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var welcomeViewModel: WelcomeViewModel
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        setBtnAction()
        startAnimation()
    }

    private fun startAnimation() {

        val image = ObjectAnimator.ofFloat(binding.welcomeImageView, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.welcomeLoginBtn, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.welcomeRegisterBtn, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.welcomeTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.welcomeDesc, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, title, desc, login, register)
            start()
        }
    }

    private fun setViewModel() {
        welcomeViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[WelcomeViewModel::class.java]

        welcomeViewModel.getSession().observe(this) { session ->
            this.session = session
            if(session.token != "") {
                Intent(this, HomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private fun setBtnAction ()  {
        binding.welcomeLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.welcomeRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}