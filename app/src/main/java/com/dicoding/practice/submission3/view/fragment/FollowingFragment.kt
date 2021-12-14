package com.dicoding.practice.submission3.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.practice.submission3.databinding.FragmentFollowingBinding
import com.dicoding.practice.submission3.model.Following
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.adapter.FollowingAdapter
import com.dicoding.practice.submission3.viewmodel.FollowingViewModel

class FollowingFragment : Fragment() {

    private var listData: ArrayList<Following> = ArrayList()
    private lateinit var adapter: FollowingAdapter
    private lateinit var followingViewModel: FollowingViewModel
    private lateinit var binding: FragmentFollowingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FollowingAdapter(listData)
        followingViewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        ).get(FollowingViewModel::class.java)

        val dataUser = requireActivity().intent.getParcelableExtra<User>(EXTRA_USER)!!
        config()

        followingViewModel.getDataGit(
            requireActivity().applicationContext,
            dataUser.username.toString()
        )
        showLoading(true)

        followingViewModel.getListFollowing().observe(requireActivity(), { listFollowing ->
            if (listFollowing != null) {
                adapter.setData(listFollowing)
                showLoading(false)
            }
        })
    }

    private fun config() {
        binding.recyclerViewFollowing.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewFollowing.setHasFixedSize(true)
        binding.recyclerViewFollowing.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        binding.progressbarFollowing.visibility = if (state) View.VISIBLE else View.INVISIBLE

    }


    companion object {
        val TAG = FollowingFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
    }

}