package com.example.sampahku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class RewardAdapter(
    private var rewards: List<RewardResponse>,
    private var viewType: Int, // 0: List, 1: Grid, 2: Card
    private val onTukarClick: (RewardResponse) -> Unit
) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    companion object {
        const val VIEW_TYPE_LIST = 0
        const val VIEW_TYPE_GRID = 1
        const val VIEW_TYPE_CARD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val layoutRes = when (viewType) {
            VIEW_TYPE_LIST -> R.layout.item_reward_list
            VIEW_TYPE_GRID -> R.layout.item_reward_grid
            VIEW_TYPE_CARD -> R.layout.item_reward_card
            else -> R.layout.item_reward_list
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind(rewards[position], onTukarClick)
    }

    override fun getItemCount(): Int = rewards.size

    fun updateData(newRewards: List<RewardResponse>) {
        this.rewards = newRewards
        notifyDataSetChanged()
    }

    fun updateViewType(newViewType: Int) {
        this.viewType = newViewType
        notifyDataSetChanged()
    }

    class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_reward_name)
        private val tvPoints: TextView = itemView.findViewById(R.id.tv_reward_points)
        private val tvDesc: TextView? = itemView.findViewById(R.id.tv_reward_desc)
        private val ivLogo: ImageView = itemView.findViewById(R.id.iv_reward_logo)
        private val btnTukar: MaterialButton = itemView.findViewById(R.id.btn_tukar)

        fun bind(reward: RewardResponse, onTukarClick: (RewardResponse) -> Unit) {
            tvName.text = reward.namaReward
            tvPoints.text = "${reward.poinDibutuhkan} Poin"
            tvDesc?.text = reward.deskripsi

            val context = itemView.context
            val resId = context.resources.getIdentifier(
                reward.logoResourceName,
                "drawable",
                context.packageName
            )
            if (resId != 0) {
                ivLogo.setImageResource(resId)
            } else {
                ivLogo.setImageResource(R.drawable.logo_sampahku) // Fallback
            }

            btnTukar.setOnClickListener { onTukarClick(reward) }
        }
    }
}