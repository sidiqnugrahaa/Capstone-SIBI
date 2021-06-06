package com.sidiq.sibi.ui.leaderboard.rank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sidiq.sibi.databinding.ItemGlobalRankBinding
import com.sidiq.sibi.domain.model.AuthUser

class RankAdapter(
    var users: List<AuthUser>? = null,
) : RecyclerView.Adapter<RankAdapter.ItemViewHolder>() {

    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemGlobalRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        users?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return users!!.size
    }

    inner class ItemViewHolder(
        private val binding: ItemGlobalRankBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: AuthUser){
            with(binding){
                Glide.with(root.context)
                    .load(user.imageUrl)
                    .into(avatarUser)

                nameUser.text = user.name
                scoreUser.text = "${user.score}"
            }
        }
    }

}