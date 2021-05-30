package com.sidiq.sibi.ui.leaderboard.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sidiq.sibi.databinding.FragmentGlobalrankBinding
import com.sidiq.sibi.databinding.FragmentMyrecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankFragment  : Fragment() {
    private lateinit var binding: FragmentGlobalrankBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlobalrankBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

        }
    }
}