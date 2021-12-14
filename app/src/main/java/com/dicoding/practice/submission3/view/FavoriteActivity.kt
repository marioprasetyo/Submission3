package com.dicoding.practice.submission3.view

import android.content.Intent
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.practice.submission3.R
import com.dicoding.practice.submission3.SettingPreferences
import com.dicoding.practice.submission3.ViewModelFactory
import com.dicoding.practice.submission3.adapter.FavouriteAdapter
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.CONTENT_URI
import com.dicoding.practice.submission3.databinding.ActivityFavoriteBinding
import com.dicoding.practice.submission3.helper.MappingHelper
import com.dicoding.practice.submission3.helper.FavoriteHelper
import com.dicoding.practice.submission3.model.Favorite
import com.dicoding.practice.submission3.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var adapter: FavouriteAdapter
    private lateinit var dbHelper: FavoriteHelper
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = FavoriteHelper.getInstance(applicationContext)
        dbHelper.open()

        binding.rvUsersFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvUsersFavorite.setHasFixedSize(true)
        adapter = FavouriteAdapter()
        binding.rvUsersFavorite.adapter = adapter

        val handleThread = HandlerThread("DataObserver")
        handleThread.start()
        val handler = Handler(handleThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                loadListFavourite()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadListFavourite()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Favorite>(EXTRA_STATE)
            if (list != null) {
                adapter.listFavourite = list
            }
        }

        if (supportActionBar != null) {
            supportActionBar?.title = getString(R.string.title_appbar_favorite)
        }

        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            MainViewModel::class.java
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavourite)
    }

    private fun loadListFavourite() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.loadingProgressFavorite.visibility = View.VISIBLE
            val deferredFav = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favData = deferredFav.await()
            binding.loadingProgressFavorite.visibility = View.INVISIBLE
            if (favData.size > 0) {
                adapter.listFavourite = favData
            } else {
                adapter.listFavourite = ArrayList()
                showSnack()
            }
        }
    }

    private fun showSnack() {
        Snackbar.make(
            binding.rvUsersFavorite,
            getString(R.string.empty_favorite),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        loadListFavourite()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        if (menu != null) {
            val search = menu.findItem(R.id.search)
            search.isVisible = false
        }

        val share = menu?.findItem(R.id.app_bar_share)
        if (share != null) {
            share.isVisible = false
        }

        val favorite = menu?.findItem(R.id.favorite_page)
        if (favorite != null) {
            favorite.title = "Home Page"
        }

        val switchTheme =
            menu?.findItem(R.id.change_theme)?.actionView?.findViewById<SwitchCompat>(R.id.switch_theme)

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
            val mIntent = Intent(this, MainActivity::class.java)
            startActivity(mIntent)
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}