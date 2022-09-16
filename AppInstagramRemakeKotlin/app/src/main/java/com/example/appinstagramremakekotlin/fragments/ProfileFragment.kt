package com.example.appinstagramremakekotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.activities.AccountSettingActivity
import com.example.appinstagramremakekotlin.activities.AddPostsActivity
import com.example.appinstagramremakekotlin.adapter.PostsAdapter
import com.example.appinstagramremakekotlin.adapter.PostsUploadAdapter
import com.example.appinstagramremakekotlin.databinding.FragmentProfileBinding
import com.example.appinstagramremakekotlin.models.Post
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var binding: FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var postAdapter: PostsUploadAdapter? = null
    private var postList: MutableList<Post>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        if(profileId == firebaseUser.uid) {
            binding.editAccountSettingBtn.text = "Edit Profile"
        } else if(profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }

        binding.editAccountSettingBtn.setOnClickListener {
            val getButtonText = binding.editAccountSettingBtn.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                getButtonText == "Follow" -> {

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }
                getButtonText == "Following" -> {

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
            }
        }

        getFollowers()
        getFollowings()
        userInfo()

        binding.postsUserProfile.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

        postList = ArrayList()
        postAdapter = context?.let { PostsUploadAdapter(it, postList as ArrayList<Post>) }
        binding.postsUserProfile.adapter = postAdapter

        retrievePost()

        return binding.root
    }

    private fun retrievePost() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        val idUser = firebaseAuth.currentUser!!.uid
        var total = 0

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for(snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)

                    if(post!!.getPublisher() == idUser) {
                        postList!!.add(post)
                        total++
                    }
                    postAdapter!!.notifyDataSetChanged()
                }
                binding?.totalPosts?.text = total.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if(followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()) {
                        binding?.editAccountSettingBtn?.text = "Following"
                    } else {
                        binding?.editAccountSettingBtn?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    binding?.totalFollowers?.text = snapshot.childrenCount.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    binding?.totalFollowing?.text = snapshot.childrenCount.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding?.imageProfilePro)
                    binding?.profileFragmentUsername?.text = user!!.getUsername()
                    binding?.fullnameProfileFrag?.text = user!!.getFullname()
                    binding?.bioProfileFrag?.text = user!!.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}