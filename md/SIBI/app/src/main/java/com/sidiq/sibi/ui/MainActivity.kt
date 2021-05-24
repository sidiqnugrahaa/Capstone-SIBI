package com.sidiq.sibi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sidiq.sibi.R
import com.sidiq.sibi.databinding.ActivityMainMenuBinding
import com.sidiq.sibi.ui.home.HomeFragment
import com.sidiq.sibi.ui.leaderboard.LeaderboardFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomMenu()
    }

    private fun bottomMenu() {
        binding.navView.setOnItemSelectedListener {
            var fragment: Fragment? = null
            when (it) {
                R.id.nav_home -> fragment = HomeFragment()
                R.id.nav_leaderboard -> fragment = LeaderboardFragment()
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.nav_host_fragment,
                        fragment
                    ).commit()
            }
        }
    }
}