package com.sidiq.sibi.ui.learning

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.sidiq.sibi.databinding.ActivityLearnBinding
import com.sidiq.sibi.domain.model.Alphabet
import com.sidiq.sibi.domain.model.LearningData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLearnBinding
    private var list: ArrayList<Alphabet> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list.addAll(LearningData.listData)
        showRecyclerList()
    }

    private fun showRecyclerList() {
        binding.rvIcon.setHasFixedSize(true)
        binding.rvIcon.layoutManager = GridLayoutManager(this@LearningActivity, 4)
        val adapter = LearningAdapter(list)
        binding.rvIcon.adapter = adapter
        adapter.setOnItemClickCallback(object : LearningAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Alphabet) {
                Intent(this@LearningActivity, LearningDetailActivity::class.java).also {
                    it.putExtra(LearningDetailActivity.EXTRA_IMG, data.icon)
                    it.putExtra(LearningDetailActivity.EXTRA_URL, data.video_url)
                    startActivity(it)
                }
            }
        })
    }
}