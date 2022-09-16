package com.example.appinstagramremakekotlin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appinstagramremakekotlin.activities.VideoActivity
import com.example.appinstagramremakekotlin.databinding.RowUserBinding
import com.example.appinstagramremakekotlin.models.Video

class VideoAdapter(
    var context: Context, var videoArrayList: ArrayList<Video>
):RecyclerView.Adapter<VideoAdapter.Myholder>() {

    private lateinit var binding: RowUserBinding

    inner class Myholder:RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        binding = RowUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return Myholder()
    }

    override fun onBindViewHolder(holder: Myholder, position: Int) {
        val model = videoArrayList!![position]

        Glide.with(context).load(model.image).into(binding.imageVideoDetails)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("videoUriMain", model.videoUri)
            intent.putExtra("titleVideo", model.title)
            intent.putExtra("timeVideo", model.timestamp)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoArrayList.size
    }

}