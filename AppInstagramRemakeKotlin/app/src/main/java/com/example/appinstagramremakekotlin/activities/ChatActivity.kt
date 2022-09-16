package com.example.appinstagramremakekotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appinstagramremakekotlin.adapter.ChatAdapter
import com.example.appinstagramremakekotlin.databinding.ActivityChatBinding
import com.example.appinstagramremakekotlin.models.Chat
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var userId = ""
    private var chatId: String? = null
    private var myId:String? = null
    //private lateinit var appUtil: AppUtil
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //appUtil = AppUtil()
        //myId = appUtil.getUID()!!

        val intent = intent
        userId = intent.getStringExtra("userId")!!

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }

      /*  if(chatId == null) {
            checkChat(idUser)
        }

        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if(message.isEmpty()) {
                Toast.makeText(this, "Enter Message", Toast.LENGTH_LONG).show()
            } else {
                sendMessage(message)
            }
        }*/

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                binding.tvUserName.text = user!!.getUsername()

            }
        })

        binding.btnSendMessage.setOnClickListener {
            var message:String = binding.etMessage.text.toString()

            if(TextUtils.isEmpty(message)) {
                Toast.makeText(this,"Message can't empty", Toast.LENGTH_LONG).show()
                binding.etMessage.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                binding.etMessage.setText("")

            }
        }

        readMessage(firebaseUser!!.uid, userId)


    }

   /* private fun checkChat(idUser: String) {
        val ref = FirebaseDatabase.getInstance().getReference("ChatList").child(myId!!)
        val query = ref.orderByChild("member").equalTo(idUser)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(ds in snapshot.children) {
                        val member = ds.child("member").value.toString()
                        if(idUser == member) {
                            chatId = ds.key
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun createChat(message: String) {

        var ref = FirebaseDatabase.getInstance().getReference("ChatList").child(myId!!)
        chatId = ref.push().key

        val chatListMode = ChatList(chatId!!, message, idUser, System.currentTimeMillis().toString())

        ref.child(chatId!!).setValue(chatListMode)
        ref = FirebaseDatabase.getInstance().getReference("ChatList").child(idUser)

        val chatList = ChatList(chatId!!, message, myId!!, System.currentTimeMillis().toString())
        ref.child(chatId!!).setValue(chatList)

        ref = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
        val messageModel = Message(myId!!, idUser, message, type = "text")
        ref.push().setValue(messageModel)

    }

    private fun sendMessage(message: String) {
        if(chatId == null) {
            createChat(message)
        } else {
            var ref = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
            val messageModel = Message(myId!!, idUser, message, type = "text")
            ref.push().setValue(messageModel)

            val map:MutableMap<String, Any> = HashMap()
            map["lastMessage"] = message
            map["date"] = System.currentTimeMillis().toString()

            ref = FirebaseDatabase.getInstance().getReference("ChatList").child(myId!!)
                .child(chatId!!)
            ref.updateChildren(map)

            ref = FirebaseDatabase.getInstance().getReference("ChatList").child(idUser)
                .child(chatId!!)
            ref.updateChildren(map)
        }
    }*/

    private fun sendMessage(senderId: String, receiverId:String, message:String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        var hashMap:HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)

    }

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
    }

}