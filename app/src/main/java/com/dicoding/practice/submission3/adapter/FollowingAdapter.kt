package com.dicoding.practice.submission3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.practice.submission3.R
import com.dicoding.practice.submission3.databinding.ItemUserBinding
import com.dicoding.practice.submission3.model.Following
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.view.UserDetailActivity

class FollowingAdapter (private val listFollowing: ArrayList<Following>) :
    RecyclerView.Adapter<FollowingAdapter.ListDataHolder>() {

    fun setData(item: ArrayList<Following>) {
        listFollowing.clear()
        listFollowing.addAll(item)
        notifyDataSetChanged()
    }

    inner class ListDataHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataFollowing: Following) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(dataFollowing.avatar)
                    .circleCrop()
                    .into(ivItemAvatar)

                tvItemName.text = dataFollowing.name
                tvItemUsername.text = dataFollowing.username
                tvItemCompany.text = dataFollowing.company
                countRepository.text =
                    itemView.context.getString(R.string.repository, dataFollowing.repository)
                countFollowers.text =
                    itemView.context.getString(R.string.follower, dataFollowing.followers)
                countFollowing.text =
                    itemView.context.getString(R.string.follower, dataFollowing.following)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListDataHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListDataHolder(binding)

    }

    override fun getItemCount(): Int {
        return listFollowing.size
    }

    override fun onBindViewHolder(holder: ListDataHolder, position: Int) {
        holder.bind(listFollowing[position])

        val data = listFollowing[position]

        holder.itemView.setOnClickListener {
            val dataUserIntent = User(
                data.username,
                data.name,
                data.avatar,
                data.company,
                data.location,
                data.repository,
                data.followers,
                data.following
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