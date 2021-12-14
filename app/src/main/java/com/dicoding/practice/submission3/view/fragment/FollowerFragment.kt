package com.dicoding.practice.submission3.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.practice.submission3.databinding.FragmentFollowerBinding
import com.dicoding.practice.submission3.model.Follower
import com.dicoding.practice.submission3.model.User
import com.dicoding.practice.submission3.adapter.FollowerAdapter
import com.dicoding.practice.submission3.viewmodel.FollowerViewModel

class FollowerFragment : Fragment() {


    private val listData: ArrayList<Follower> = ArrayList()
    private lateinit var adapter: FollowerAdapter
    private lateinit var followerViewModel: FollowerViewModel

    private lateinit var binding: FragmentFollowerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FollowerAdapter(listData)
        followerViewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        ).get(FollowerViewModel::class.java)

        val dataUser = requireActivity().intent.getParcelableExtra<User>(EXTRA_USER)!!
        config()

        followerViewModel.getDataGit(
            requireActivity().applicationContext,
            dataUser.username.toString()
        )
        showLoading(true)

        followerViewModel.getListFollower().observe(requireActivity(), { listFollower ->
            if (listFollower != null) {
                adapter.setData(listFollower)
                showLoading(false)
            }
        })
    }

    private fun config() {
        binding.recyclerViewFollowers.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewFollowers.setHasFixedSize(true)
        binding.recyclerViewFollowers.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        binding.progressbarFollowers.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        val TAG = FollowerFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
    }
}
