package com.example.appinstagramremakekotlin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.adapter.PostsAdapter
import com.example.appinstagramremakekotlin.adapter.PostsDetailAdapter
import com.example.appinstagramremakekotlin.adapter.PostsUploadAdapter
import com.example.appinstagramremakekotlin.databinding.ActivityPostsDetailsBinding
import com.example.appinstagramremakekotlin.models.Post
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var postAdapter: PostsAdapter? = null
    private var postList: MutableList<Post>? = null
    private var postId = ""
    private var imagePost = ""
    private var postDesc = ""
    private var publisherID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val intent = intent
        postId = intent.getStringExtra("postId")!!
        imagePost = intent.getStringExtra("imagePost")!!
        postDesc = intent.getStringExtra("postDecription")!!
        publisherID = intent.getStringExtra("publisherId")!!

        publisherDetails(binding.userProfileImagePost, binding.userNamePost, publisherID)
        binding.description.text = postDesc
        Picasso.get().load(imagePost).placeholder(R.drawable.profile).into(binding.postImageHome)

        binding.postImageCommentBtn.setOnClickListener {
            val intent = Intent(this, CommentPostActivity::class.java)
            intent.putExtra("postId", postId)
            intent.putExtra("imagePost", imagePost)
            intent.putExtra("postDecription", postDesc)
            intent.putExtra("publisherId", publisherID)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        var recyclerView: RecyclerView? = null
        recyclerView = binding.recyclerViewDetailsPost
        val linerLayoutManager = LinearLayoutManager(this)
        linerLayoutManager.reverseLayout = true
        linerLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linerLayoutManager

        postList = ArrayList()
        postAdapter = this?.let { PostsAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        retrievePost()

    }

    private fun retrievePost() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        val idUser = firebaseAuth.currentUser!!.uid
        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for(snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)

                    if(post!!.getPublisher() == idUser) {
                        if(post!!.getPostId() != postId) {
                            postList!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun publisherDetails(profileImage: CircleImageView, userName: TextView, publisherId: String) {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user!!.getUsername()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}

