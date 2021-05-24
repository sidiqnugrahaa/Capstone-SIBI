package com.sidiq.sibi.ui.leaderboard.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sidiq.sibi.databinding.FragmentLeaderboardBinding
import com.sidiq.sibi.databinding.FragmentMyrecordBinding

class RecordFragment  : Fragment() {
    private lateinit var binding: FragmentMyrecordBinding

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

        }
    }
}