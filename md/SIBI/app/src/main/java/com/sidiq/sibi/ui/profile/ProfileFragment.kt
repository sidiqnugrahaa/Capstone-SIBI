package com.sidiq.sibi.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.sidiq.sibi.R
import com.sidiq.sibi.SibiApp
import com.sidiq.sibi.databinding.FragmentContributeBinding
import com.sidiq.sibi.databinding.FragmentProfileBinding
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.start.StartActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val authViewModel : FirebaseAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            initProfile()
            binding.btnSignOut.setOnClickListener { logout() }

        }
    }

    private fun initProfile(){
        with(binding){
            val profile = (activity?.application as SibiApp).authUser
            tvName.text = profile?.name
            tvEmail.text = profile?.userId
            Glide.with(requireView())
                .load(profile?.imageUrl)
                .into(profileImage)

        }
    }

    private fun logout() {
        authViewModel.logout()
        startLoginActivity()
    }

    private fun startLoginActivity() {
        val intent = Intent(requireActivity(), StartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}