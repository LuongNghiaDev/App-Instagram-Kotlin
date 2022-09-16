package com.example.appinstagramremakekotlin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.activities.ChatActivity
import com.example.appinstagramremakekotlin.databinding.ItemUserBinding
import com.example.appinstagramremakekotlin.databinding.UserItemLayoutBinding
import com.example.appinstagramremakekotlin.fragments.ProfileFragment
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserChatAdapter(private val context:Context, private val userList: ArrayList<User>)
    :RecyclerView.Adapter<UserChatAdapter.MyHolder>(){

    private lateinit var binding: ItemUserBinding

    inner class MyHolder: RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatAdapter.MyHolder {
        binding = ItemUserBinding.inflate(LayoutInflater.from(context)
            , parent, false)
        return MyHolder()
    }

    override fun onBindViewHolder(holder: UserChatAdapter.MyHolder, position: Int) {
        val user = userList[position]

        binding.usernameSearch.text = user.getUsername()
        binding.fullnameSearch.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(binding.imageProfileSearch)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", user.getUid())
            context.startActivity(intent)

            val pref = context.getSharedPreferences("PREFSCHAT", Context.MODE_PRIVATE).edit()
            pref.putString("imageUser", user.getImage())
            pref.apply()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}