package com.sidiq.sibi.ui.leaderboard.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sidiq.sibi.databinding.ItemGlobalRankBinding
import com.sidiq.sibi.databinding.ItemMyRecordBinding
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.History

class RecordAdapter(
    var histories: List<History>? = null,
) : RecyclerView.Adapter<RecordAdapter.ItemViewHolder>() {

    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemMyRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        histories?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return histories!!.size
    }

    inner class ItemViewHolder(
        private val binding: ItemMyRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History){
            with(binding){
                timestamp.text = history.type
                score.text = "${history.score}"
            }
        }
    }

}