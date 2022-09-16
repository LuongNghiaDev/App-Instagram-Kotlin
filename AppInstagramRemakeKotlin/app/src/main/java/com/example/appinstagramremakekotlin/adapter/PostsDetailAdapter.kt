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
import com.example.appinstagramremakekotlin.databinding.PostsLayoutBinding
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

class PostsDetailAdapter(private val context: Context, private val list: List<Post>):RecyclerView.Adapter<
        PostsDetailAdapter.Myholder>() {

    private var firebaseUser: FirebaseUser? = null
    private lateinit var binding: PostsLayoutBinding
    var isLike: Boolean = false
    private var i = 0

    inner class Myholder: RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        binding =PostsLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        setLayout()
        return Myholder()
    }

    override fun onBindViewHolder(holder: Myholder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = list[position]
        binding.description.text = post.getDescription()

        Picasso.get().load(post.getPostImage()).into(binding.postImageHome)

        publisherInfo(binding.userProfileImagePost, binding.userNamePost, binding.publisher,
        post.getPublisher())

        binding.postImageCommentBtn.setOnClickListener {
            val intent = Intent(context, CommentPostActivity::class.java)
            intent.putExtra("postId", list[position].getPostId())
            intent.putExtra("imagePost", list[position].getPostImage())
            intent.putExtra("postDecription", list[position].getDescription())
            intent.putExtra("publisherId", list[position].getPublisher())
            context.startActivity(intent)
        }

        binding.postImageLikeBtn.setOnClickListener {
            if(isLike) {
                i--
                isLike = false
                binding.likes.text = "${i} like"
                binding.postImageLikeBtn.setImageResource(R.drawable.heart_not_clicked)
            } else {
                i++
                isLike = true
                binding.likes.text = "${i} like"
                binding.postImageLikeBtn.setImageResource(R.drawable.heart_clicked)
            }
        }
    }

    private fun setLayout() {
        if(isLike) {
            binding.likes.text = "${i} like"
            binding.postImageLikeBtn.setImageResource(R.drawable.heart_clicked)
        } else {
            binding.likes.text = "${i} like"
            binding.postImageLikeBtn.setImageResource(R.drawable.heart_not_clicked)
        }
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String) {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getFullname()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return list.size
    }


}