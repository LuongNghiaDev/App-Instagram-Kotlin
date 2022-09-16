package com.example.appinstagramremakekotlin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.models.Chat
import com.example.appinstagramremakekotlin.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context:Context, private val chatList: ArrayList<Chat>)
    :RecyclerView.Adapter<ChatAdapter.MyHolder>(){

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null
    private var imageUser = ""

    class MyHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtMessage:TextView = view.findViewById(R.id.tvMessage)
        val userImage:CircleImageView = view.findViewById(R.id.userImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.MyHolder {
        if(viewType == MESSAGE_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return MyHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return MyHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatAdapter.MyHolder, position: Int) {
        val chat = chatList[position]
        holder.txtMessage.text = chat.message

        val pref = context?.getSharedPreferences("PREFSCHAT", Context.MODE_PRIVATE)
        if(pref != null) {
            imageUser = pref.getString("imageUser", "none")!!
        }
        Picasso.get().load(imageUser).placeholder(R.drawable.profile).into(holder.userImage)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(chatList[position].senderId == firebaseUser!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }

}