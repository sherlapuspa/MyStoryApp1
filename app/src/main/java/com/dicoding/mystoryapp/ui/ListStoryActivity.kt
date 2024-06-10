package com.dicoding.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.databinding.ActivityListStoryBinding
import com.dicoding.mystoryapp.data.ListStoryItem
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.UserVMFactory


class ListStoryActivity : AppCompatActivity() {
    private val userPref = UserPreferencesManager.getInstance(dataStore)
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var token: String
    private val listStoryVM: ListStoryVM by lazy {
        ViewModelProvider(this)[ListStoryVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(R.layout.custom_toolbar)
        }
        handleUserActions()

        val layout = LinearLayoutManager(this)
        binding.rvStoryList.layoutManager = layout
        val storyDecoration = DividerItemDecoration(this, layout.orientation)
        binding.rvStoryList.addItemDecoration(storyDecoration)

        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]

        authSplashVM.getToken().observe(this) {
            token = it
            listStoryVM.getUserStories(token)
        }

        listStoryVM.message.observe(this) {
            displayStoryData(listStoryVM.userStories)
            showToast(it)
        }

        listStoryVM.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showToast(msg: String) {
        if (listStoryVM.isError) {
            Toast.makeText(
                this,
                "${getString(R.string.loading_error)} $msg",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun displayNullData(isNoData: Boolean) {
        binding.nullDataView.visibility = if (isNoData) View.VISIBLE else View.GONE
        binding.tvNullDataView.visibility = if (isNoData) View.VISIBLE else View.GONE
    }

    private fun displayStoryData(storyList: List<ListStoryItem>) {
        displayNullData(storyList.isEmpty())

        val adapter = ListStoryAdapt(storyList)
        binding.rvStoryList.adapter = adapter

        adapter.setOnItemClickCallback(object : ListStoryAdapt.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                displaySelectedStory(data)
            }
        })
    }

    private fun displaySelectedStory(data: ListStoryItem) {
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.STORY_DATA, data)
        startActivity(intent)
    }

    private fun handleUserActions() {
        binding.floatingBtn.setOnClickListener {
            startActivity(Intent(this, PostStoryActivity::class.java))
        }

        binding.pullRefresh.setOnRefreshListener {
            listStoryVM.getUserStories(token)
            binding.pullRefresh.isRefreshing = false
        }
    }

    private fun logout() {
        val authVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]
        authVM.resetLoginData()
        Toast.makeText(this, R.string.logoutSucceed, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.refreshApp -> {
//                listStoryVM.getUserStories(token)
//                true
//            }
            R.id.switchLang -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                showAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logoutWarn))
            .setPositiveButton(getString(R.string.logoutNo)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.logoutYes)) { _, _ ->
                logout()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}