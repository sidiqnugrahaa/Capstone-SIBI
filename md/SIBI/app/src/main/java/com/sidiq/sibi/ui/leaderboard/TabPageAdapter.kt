package com.sidiq.sibi.ui.leaderboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.sidiq.sibi.ui.leaderboard.rank.RankFragment
import com.sidiq.sibi.ui.leaderboard.record.RecordFragment

class TabPageAdapter (fragment: Fragment) :
    FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RecordFragment()
                1 -> RankFragment()
                else -> Fragment()
            }
        }

}