package com.sidiq.sibi.ui.leaderboard.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.databinding.FragmentLeaderboardBinding
import com.sidiq.sibi.databinding.FragmentMyrecordBinding
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.leaderboard.LeaderBoardViewModel
import com.sidiq.sibi.ui.leaderboard.rank.RankAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment  : Fragment() {
    private lateinit var binding: FragmentMyrecordBinding
    private val authViewModel: FirebaseAuthViewModel by activityViewModels()
    private val leaderBoardViewModel: LeaderBoardViewModel by activityViewModels()
    private val recordAdapter = RecordAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyrecordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            val user = authViewModel.checkUserLoggedIn()?.toDomain()

            user?.userId?.let { userId ->
                leaderBoardViewModel.history(userId).observe(viewLifecycleOwner){
                    when(it){
                        is Resource.Loading -> {
                            //TODO: Add Spinner / Loading
                            binding.status.text = "Loading..."
                            Log.d("STATUS", "Loading")
                        }

                        is Resource.Success -> {
                            binding.status.text = "Loaded"
                            recordAdapter.histories = it.data
                            binding.rvRecord.apply {
                                adapter = recordAdapter
                                layoutManager = LinearLayoutManager(requireContext())
                            }
                        }

                        is Resource.Empty -> {
                            binding.status.text = "Empty"
                            Log.d("STATUS", "EMPTY")
                        }

                        is Resource.Failure -> {
                            binding.status.text = "Error"
                            Log.d("STATUS", it.throwable.message!!)
                        }
                    }
                }
            }
        }

    }
}