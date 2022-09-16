package com.example.appinstagramremakekotlin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstagramremakekotlin.MyApplication
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.activities.CommentPostActivity
import com.example.appinstagramremakekotlin.activities.PostsDetailsActivity
import com.example.appinstagramremakekotlin.databinding.PostsLayoutBinding
import com.example.appinstagramremakekotlin.databinding.RowPostsFrofileBinding
import com.example.appinstagramremakekotlin.models.Post
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsUploadAdapter(private val context: Context, private val list: List<Post>):RecyclerView.Adapter<
        PostsUploadAdapter.Myholder>() {

    private lateinit var binding: RowPostsFrofileBinding

    inner class Myholder: RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        binding =RowPostsFrofileBinding.inflate(LayoutInflater.from(context), parent, false)
        return Myholder()
    }

    override fun onBindViewHolder(holder: Myholder, position: Int) {

        val post = list[position]

        Picasso.get().load(post.getPostImage()).into(binding.imagePostUpload)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PostsDetailsActivity::class.java)
            intent.putExtra("postId", list[position].getPostId())
            intent.putExtra("imagePost", list[position].getPostImage())
            intent.putExtra("postDecription", list[position].getDescription())
            intent.putExtra("publisherId", list[position].getPublisher())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}