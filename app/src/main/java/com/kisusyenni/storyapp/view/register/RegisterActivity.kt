package com.kisusyenni.storyapp.view.register

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
import com.kisusyenni.storyapp.databinding.ActivityRegisterBinding
import com.kisusyenni.storyapp.view.login.LoginActivity
import com.kisusyenni.storyapp.viewmodel.RegisterViewModel
import com.kisusyenni.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setViewModel()
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@RegisterActivity, R.string.register_error, Toast.LENGTH_LONG)
                    .show()
            } else {
                registerViewModel.register(name = name, email = email, password = password)
            }

        }
    }

    private fun setViewModel() {
        registerViewModel = ViewModelProvider(this@RegisterActivity, ViewModelFactory(
            SessionPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        registerViewModel.error.observe(this) { res ->
            if (res.error) {
                Toast.makeText(this@RegisterActivity, res.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    resources.getString(R.string.register_success),
                    Toast.LENGTH_LONG
                ).show()
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.pbRegister.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }
}