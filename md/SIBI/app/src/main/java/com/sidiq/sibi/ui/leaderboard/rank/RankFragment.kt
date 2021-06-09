package com.sidiq.sibi.ui.leaderboard.rank

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sidiq.sibi.R
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.databinding.FragmentGlobalrankBinding
import com.sidiq.sibi.ui.leaderboard.LeaderBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankFragment  : Fragment() {
    private lateinit var binding: FragmentGlobalrankBinding
    private val leaderBoardViewModel: LeaderBoardViewModel by activityViewModels()
    private val rankAdapter = RankAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlobalrankBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            leaderBoardViewModel.globalRank.observe(viewLifecycleOwner){
                when(it){
                    is Resource.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                        binding.viewEmpty.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.loading.visibility = View.GONE
                        binding.viewEmpty.visibility = View.GONE

                        rankAdapter.users = it.data
                        binding.rvGlobalRank.apply {
                            adapter = rankAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                    }

                    is Resource.Empty -> {
                        binding.loading.visibility = View.GONE
                        binding.viewEmpty.apply {
                            visibility = View.VISIBLE
                            text = resources.getText(R.string.data_kosong)
                        }
                    }

                    is Resource.Failure -> {
                        binding.loading.visibility = View.GONE
                        binding.viewEmpty.apply {
                            visibility = View.VISIBLE
                            text = resources.getText(R.string.error_mengambil_data)
                        }
                        Log.d("STATUS", it.throwable.message!!)
                    }
                }
            }
        }
    }

}