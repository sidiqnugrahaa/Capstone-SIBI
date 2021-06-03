package com.sidiq.sibi.ui.contribute

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sidiq.sibi.databinding.FragmentContributeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributeFragment : Fragment() {
    private lateinit var binding: FragmentContributeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContributeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val word = ""

            binding.btnStartContrib.setOnClickListener {
                val intent = Intent(activity, ContributeCameraActivity::class.java).apply {
                    putExtra(ContributeCameraActivity.EXTRA_WORD, word)
                }
                startActivity(intent)
            }
        }
    }
}