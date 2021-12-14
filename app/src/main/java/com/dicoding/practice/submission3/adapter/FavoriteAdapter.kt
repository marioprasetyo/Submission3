package com.dicoding.practice.submission3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.practice.submission3.R
import com.dicoding.practice.submission3.databinding.ItemUserBinding
import com.dicoding.practice.submission3.model.Favorite
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.view.UserDetailActivity

class FavouriteAdapter :
    RecyclerView.Adapter<FavouriteAdapter.ListViewHolder>() {

    var listFavourite = ArrayList<Favorite>()
        set(listFavourite) {
            if (listFavourite.size > 0) {
                this.listFavourite.clear()
            }
            this.listFavourite.addAll(listFavourite)
            notifyDataSetChanged()
        }

    inner class ListViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(favorite.avatar)
                    .circleCrop()
                    .into(ivItemAvatar)

                tvItemName.text = favorite.name
                tvItemUsername.text = favorite.username
                tvItemCompany.text = favorite.company
                countRepository.text =
                    itemView.context.getString(R.string.repository, favorite.repository)
                countFollowers.text =
                    itemView.context.getString(R.string.follower, favorite.followers)
                countFollowing.text =
                    itemView.context.getString(R.string.follower, favorite.following)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFavourite.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listFavourite[position])

        val data = listFavourite[position]
        holder.itemView.setOnClickListener {

            val dataUserIntent = User(
                data.username,
                data.name,
                data.avatar,
                data.company,
                data.location,
                data.repository,
                data.followers,
                data.following,
            )
            val mIntent = Intent(it.context, UserDetailActivity::class.java)
            mIntent.putExtra(UserDetailActivity.EXTRA_USER, dataUserIntent)
            it.context.startActivity(mIntent)
        }

        val btnShare: Button = holder.itemView.findViewById(R.id.btn_share)
        btnShare.setOnClickListener {
            val shareUser = "Github User:\n" +
                    "Name: ${data.name}\n" +
                    "Username: ${data.username}\n" +
                    "Company: ${data.company}\n" +
                    "Location: ${data.location}\n" +
                    "Repositories: ${data.repository}\n" +
                    "Followers: ${data.followers}\n" +
                    "Following: ${data.following}"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareUser)
            shareIntent.type = "text/html"
            it.context.startActivity(Intent.createChooser(shareIntent, "Share using"))
        }

    }
}