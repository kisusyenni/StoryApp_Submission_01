package com.kisusyenni.storyapp.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kisusyenni.storyapp.R
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.data.source.remote.response.ListStoryItem
import com.kisusyenni.storyapp.databinding.ActivityHomeBinding
import com.kisusyenni.storyapp.view.addstory.AddStoryActivity
import com.kisusyenni.storyapp.view.welcome.WelcomeActivity
import com.kisusyenni.storyapp.viewmodel.HomeViewModel
import com.kisusyenni.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        setIntentAddButton()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Remove user's session data when logout
                homeViewModel.removeSession()
                Toast.makeText(this@HomeActivity, resources.getString(R.string.logout_success), Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setViewModel() {
        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[HomeViewModel::class.java]

        homeViewModel.getSession().observe(this) { session ->
            homeViewModel.getStories(session.token)
        }

        homeViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        homeViewModel.listStories.observe(this) { stories ->
            if(stories.isNotEmpty()) setStoriesData(stories)
        }

        homeViewModel.isLogout.observe(this) { logged ->
            if(logged) Intent(this@HomeActivity, WelcomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        homeViewModel.error.observe(this) { res ->
            if (res.error) {
                binding.tvHomeError.text = if(res.message != "") res.message else resources.getString(R.string.load_data_error)

                binding.tvHomeError.visibility = View.VISIBLE
            } else {
                Toast.makeText(this@HomeActivity, resources.getString(R.string.load_data_succeed), Toast.LENGTH_LONG).show()
                binding.tvHomeError.visibility = View.GONE
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.pbHome.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun setStoriesData(stories: List<ListStoryItem>) {
        val homeAdapter = HomeAdapter()
        homeAdapter.submitList(stories)
        binding.rvStoryList.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun setIntentAddButton () {
        binding.intentAddBtn.setOnClickListener {
            startActivity( Intent(this@HomeActivity, AddStoryActivity::class.java))
        }
    }
}