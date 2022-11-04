package com.kisusyenni.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kisusyenni.storyapp.R
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.local.entity.Session
import com.kisusyenni.storyapp.databinding.ActivityLoginBinding
import com.kisusyenni.storyapp.view.home.HomeActivity
import com.kisusyenni.storyapp.viewmodel.LoginViewModel
import com.kisusyenni.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setViewModel()
        showLoading(false)

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, R.string.login_error, Toast.LENGTH_LONG).show()
            } else {
                loginViewModel.login(email = email, password = password)
            }

        }
    }

    private fun setViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getSession().observe(this) { session ->
            this.session = session
            if(session.token != "") {
                Intent(this, HomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        loginViewModel.isLogin.observe(this) { logged ->
            Toast.makeText(this@LoginActivity, resources.getString(R.string.login_success), Toast.LENGTH_SHORT).show()
            if(logged) Intent(this, HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                finish()
            }
        }

        loginViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        loginViewModel.error.observe(this) { res ->
            if(res.error) Toast.makeText(this@LoginActivity, res.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.pbLogin.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.visibility = if (loading) View.GONE else View.VISIBLE
    }
}