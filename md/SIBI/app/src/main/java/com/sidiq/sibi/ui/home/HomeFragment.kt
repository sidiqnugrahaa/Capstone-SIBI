package com.sidiq.sibi.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sidiq.sibi.R
import com.sidiq.sibi.SibiApp
import com.sidiq.sibi.databinding.FragmentHomeBinding
import com.sidiq.sibi.ui.game.GameActivity
import com.sidiq.sibi.ui.learning.LearningActivity
import com.sidiq.sibi.ui.practice.PracticeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfile()

        if (activity != null) {
            binding.learnAlphabet.setOnClickListener(View.OnClickListener {
                startActivity(Intent(context, LearningActivity::class.java))
            })
            binding.practice.setOnClickListener(View.OnClickListener {
                startActivity(Intent(context, PracticeActivity::class.java))
            })
            binding.game.setOnClickListener(View.OnClickListener {
                startActivity(Intent(context, GameActivity::class.java))
            })
        }
    }

    private fun initProfile(){
        with(binding){
            val profile = (activity?.application as SibiApp).authUser
            helloUser.text = resources.getString(
                R.string.hello_user, profile?.name?.split(" ")?.get(0)
            )
            Glide.with(requireView())
                .load(profile?.imageUrl)
                .into(profileImage)

        }
    }
}