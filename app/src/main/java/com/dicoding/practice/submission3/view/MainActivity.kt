package com.dicoding.practice.submission3.view

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.practice.submission3.R
import com.dicoding.practice.submission3.SettingPreferences
import com.dicoding.practice.submission3.ViewModelFactory
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.viewmodel.MainViewModel
import com.dicoding.practice.submission3.adapter.UserAdapter
import com.dicoding.practice.submission3.databinding.ActivityMainBinding

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private var listDataUser: ArrayList<User> = ArrayList()
    private lateinit var listAdapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAdapter = UserAdapter(listDataUser)
        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            MainViewModel::class.java
        )

        viewConfig()
        runGetDataGit()
        configMainViewModel(listAdapter)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.find_username)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listDataUser.clear()
                    viewConfig()
                    showLoading(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        listDataUser.clear()
                        viewConfig()
                        mainViewModel.getDataGitSearch(query, applicationContext)
                        configMainViewModel(listAdapter)
                    }, 1000L)

                } else {
                    return true
                }
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        val item = menu.findItem(R.id.app_bar_share)
        if (item != null) {
            item.isVisible = false
        }

        val switchTheme =
            menu.findItem(R.id.change_theme)?.actionView?.findViewById<SwitchCompat>(R.id.switch_theme)

        mainViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    if (switchTheme != null) {
                        switchTheme.isChecked = true
                    }
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (switchTheme != null) {
                        switchTheme.isChecked = false
                    }
                }
            })

        switchTheme?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)

        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_language) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }

        if (item.itemId == R.id.favorite_page) {
            val mIntent = Intent(this, FavoriteActivity::class.java)
            startActivity(mIntent)
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun viewConfig() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.setHasFixedSize(true)

        listAdapter.notifyDataSetChanged()
        binding.rvUsers.adapter = listAdapter

    }


    private fun runGetDataGit() {
        mainViewModel.getDataGit(applicationContext)
        showLoading(true)
    }

    private fun configMainViewModel(adapter: UserAdapter) {
        mainViewModel.getListUsers().observe(this, { listUsers ->
            if (listUsers != null) {
                adapter.setData(listUsers)
                showLoading(false)
            }
        })
    }


    private fun showLoading(state: Boolean) {
        binding.loadingProgress.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName

    }
}