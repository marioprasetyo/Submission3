package com.dicoding.practice.submission3.view

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.practice.submission3.R
import com.dicoding.practice.submission3.SettingPreferences
import com.dicoding.practice.submission3.ViewModelFactory
import com.dicoding.practice.submission3.databinding.ActivityUserDetailBinding
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.adapter.ViewPagerDetailAdapter
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.AVATAR
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.COMPANY
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.CONTENT_URI
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FAVORITE
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FOLLOWERS
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FOLLOWING
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.LOCATION
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.NAME
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.REPOSITORY
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.USERNAME
import com.dicoding.practice.submission3.helper.FavoriteHelper
import com.dicoding.practice.submission3.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var nameUser: String
    private lateinit var content: String
    private lateinit var binding: ActivityUserDetailBinding
    private var isFavorite = false
    private lateinit var favHelper: FavoriteHelper
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = getString(R.string.detail_user)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        favHelper = FavoriteHelper.getInstance(applicationContext)
        favHelper.open()

        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        val cursor: Cursor = favHelper.queryByUsername(user.username.toString())
        if (cursor.moveToNext()) {
            isFavorite = true
            setStatusFavorite(true)
        }

        setData()
        binding.btnFavorite.setOnClickListener(this)
        viewPagerConfig()

        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            MainViewModel::class.java
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.search)
            item.isVisible = false
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
        when (item.itemId) {
            R.id.app_bar_share -> {
                val shareUser = "Github User:\n$nameUser\n$content"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareUser)
                shareIntent.type = "text/html"
                startActivity(Intent.createChooser(shareIntent, "Share using"))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun viewPagerConfig() {
        val viewPagerDetail = ViewPagerDetailAdapter(this)
        binding.viewPager.adapter = viewPagerDetail
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }


    private fun setData() {
        showLoading(true)

        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)
            val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
            val image = user.avatar
            nameUser = user.name.toString()
            content = "${user.username.toString()}\n" +
                    "${user.company.toString()}\n" +
                    "${user.location.toString()}\n" +
                    "${user.repository.toString()}\n" +
                    "${user.followers.toString()}\n" +
                    user.following.toString()

            Glide.with(this).load(image).into(binding.ivAvatarReceived)
            binding.tvNameReceived.text = nameUser
            binding.tvObjectReceived.text = content
        }, 1000L)

    }

    private fun showLoading(state: Boolean) {
        binding.loadingProgressUserDetail.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    override fun onClick(view: View) {
        val data = intent.getParcelableExtra<User>(EXTRA_USER)!!
        if (view.id == R.id.btn_favorite) {
            if (isFavorite) {
                favHelper.deleteById(data.username.toString())
                Toast.makeText(this, getString(R.string.delete_favorite), Toast.LENGTH_SHORT).show()
                setStatusFavorite(false)
                isFavorite = false
            } else {
                val values = ContentValues()
                values.put(USERNAME, data.username)
                values.put(NAME, data.name)
                values.put(AVATAR, data.avatar)
                values.put(COMPANY, data.company)
                values.put(LOCATION, data.location)
                values.put(REPOSITORY, data.repository)
                values.put(FOLLOWERS, data.followers)
                values.put(FOLLOWING, data.following)
                values.put(FAVORITE, "isFav")

                isFavorite = true
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
                setStatusFavorite(true)
            }
        }
    }

    private fun setStatusFavorite(status: Boolean) {
        if (status) {
            binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_50)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_50)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        favHelper.close()
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

}