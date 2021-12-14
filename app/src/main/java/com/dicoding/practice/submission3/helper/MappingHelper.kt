package com.dicoding.practice.submission3.helper

import android.database.Cursor
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.AVATAR
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.COMPANY
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FAVORITE
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FOLLOWERS
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.FOLLOWING
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.LOCATION
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.NAME
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.REPOSITORY
import com.dicoding.practice.submission3.database.DatabaseContract.UserFavoriteColumns.Companion.USERNAME
import com.dicoding.practice.submission3.model.Favorite

object MappingHelper {
    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<Favorite> {
        val favList = ArrayList<Favorite>()

        cursor?.apply {
            while (moveToNext()) {
                val username =
                    getString(getColumnIndexOrThrow(USERNAME))
                val name =
                    getString(getColumnIndexOrThrow(NAME))
                val avatar =
                    getString(getColumnIndexOrThrow(AVATAR))
                val company =
                    getString(getColumnIndexOrThrow(COMPANY))
                val location =
                    getString(getColumnIndexOrThrow(LOCATION))
                val repository =
                    getString(getColumnIndexOrThrow(REPOSITORY))
                val followers =
                    getString(getColumnIndexOrThrow(FOLLOWERS))
                val following =
                    getString(getColumnIndexOrThrow(FOLLOWING))
                val isFav =
                    getString(getColumnIndexOrThrow(FAVORITE))

                favList.add(
                    Favorite(
                        username,
                        name,
                        avatar,
                        company,
                        location,
                        repository,
                        followers,
                        following,
                        isFav
                    )
                )
            }
        }
        return favList
    }

}