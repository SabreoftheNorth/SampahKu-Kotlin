package com.example.sampahku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class EdukasiAdapter(
    private var listEdukasi: List<EdukasiResponse>,
    private var viewType: Int, // 0: List, 1: Grid, 2: Card
    private val onVideoClick: (String?) -> Unit
) : RecyclerView.Adapter<EdukasiAdapter.EdukasiViewHolder>() {

    companion object {
        const val VIEW_TYPE_LIST = 0
        const val VIEW_TYPE_GRID = 1
        const val VIEW_TYPE_CARD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EdukasiViewHolder {
        val layoutRes = when (viewType) {
            VIEW_TYPE_LIST -> R.layout.item_edukasi_list
            VIEW_TYPE_GRID -> R.layout.item_edukasi_grid
            VIEW_TYPE_CARD -> R.layout.item_edukasi_card
            else -> R.layout.item_edukasi_list
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return EdukasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EdukasiViewHolder, position: Int) {
        holder.bind(listEdukasi[position], viewType, onVideoClick)
    }

    override fun getItemCount(): Int = listEdukasi.size

    fun updateData(newList: List<EdukasiResponse>) {
        this.listEdukasi = newList
        notifyDataSetChanged()
    }

    fun updateViewType(newViewType: Int) {
        this.viewType = newViewType
        notifyDataSetChanged()
    }

    class EdukasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJudul: TextView = itemView.findViewById(R.id.tv_judul_edukasi)
        private val tvDesc: TextView? = itemView.findViewById(R.id.tv_desc_edukasi)
        private val ivThumbnail: ImageView? = itemView.findViewById(R.id.iv_thumbnail)
        private val btnTonton: MaterialButton = itemView.findViewById(R.id.btn_tonton_video)

        fun bind(edukasi: EdukasiResponse, viewType: Int, onVideoClick: (String?) -> Unit) {
            tvJudul.text = edukasi.judul
            tvDesc?.text = edukasi.deskripsi

            // THUMBNAIL LOGIC
            // Note: Since we don't have specific thumbnails for each video yet, 
            // we use a placeholder and provide comments on which filename to use.
            if (ivThumbnail != null) {
                // If you have different thumbnails for Grid vs Card, you can use viewType here.
                if (viewType == VIEW_TYPE_GRID) {
                    // TODO: Put thumbnail png for GRID view here (e.g., thumb_video_grid.png)
                    // ivThumbnail.setImageResource(R.drawable.YOUR_GRID_THUMBNAIL_NAME)
                    ivThumbnail.setImageResource(R.drawable.logo_sampahku) // Placeholder
                } else if (viewType == VIEW_TYPE_CARD) {
                    // TODO: Put thumbnail png for CARD view here (e.g., thumb_video_card.png)
                    // ivThumbnail.setImageResource(R.drawable.YOUR_CARD_THUMBNAIL_NAME)
                    ivThumbnail.setImageResource(R.drawable.logo_sampahku) // Placeholder
                }
            }

            btnTonton.setOnClickListener { onVideoClick(edukasi.videoIdYoutube) }
        }
    }
}