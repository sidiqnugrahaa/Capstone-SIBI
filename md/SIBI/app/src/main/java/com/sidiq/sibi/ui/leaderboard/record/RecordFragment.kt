package com.sidiq.sibi.ui.leaderboard.record

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
import com.sidiq.sibi.databinding.FragmentMyrecordBinding
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.leaderboard.LeaderBoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

    @ExperimentalCoroutinesApi
    override fun onResume() {
        super.onResume()
        if (activity != null) {
            val user = authViewModel.checkUserLogin()

            user?.userId?.let { userId ->
                leaderBoardViewModel.history(userId).observe(viewLifecycleOwner){
                    when(it){
                        is Resource.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                            binding.viewEmpty.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.loading.visibility = View.GONE
                            binding.viewEmpty.visibility = View.GONE

                            recordAdapter.histories = it.data
                            binding.rvRecord.apply {
                                adapter = recordAdapter
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
}