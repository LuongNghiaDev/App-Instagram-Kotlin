package com.example.appinstagramremakekotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.activities.AddVideoActivity
import com.example.appinstagramremakekotlin.adapter.PostsAdapter
import com.example.appinstagramremakekotlin.adapter.VideoAdapter
import com.example.appinstagramremakekotlin.databinding.FragmentHomeBinding
import com.example.appinstagramremakekotlin.models.Post
import com.example.appinstagramremakekotlin.models.User
import com.example.appinstagramremakekotlin.models.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var postAdapter: PostsAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    private lateinit var profileId: String

    private lateinit var videoArrayList: ArrayList<Video>
    private lateinit var adapterVideo: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        var recyclerView: RecyclerView? = null
        recyclerView = binding.recyclerViewHome
        val linerLayoutManager = LinearLayoutManager(context)
        linerLayoutManager.reverseLayout = true
        linerLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linerLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostsAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        checkFollowings()

        binding.addVideo.setOnClickListener {
            startActivity(Intent(requireContext(), AddVideoActivity::class.java))
        }

        binding.addVideoToolbar.setOnClickListener {
            startActivity(Intent(requireContext(), AddVideoActivity::class.java))
        }

        loadVideo()

        return binding.root
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {

                    (followingList as ArrayList<String>).clear()

                    for (snap in snapshot.children) {
                        snap.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePost()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrievePost() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for(snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)

                    for(id in (followingList as ArrayList<String>)) {
                        if(post!!.getPublisher() == id) {
                            postList!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadVideo() {
        videoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Videos")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                videoArrayList.clear()

                for(ds in snapshot.children) {
                    val modelVideo = ds.getValue(Video::class.java)
                    videoArrayList.add(modelVideo!!)
                }

                adapterVideo = VideoAdapter(requireContext(), videoArrayList)
                binding.videoRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.videoRecyclerView.adapter = adapterVideo
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}