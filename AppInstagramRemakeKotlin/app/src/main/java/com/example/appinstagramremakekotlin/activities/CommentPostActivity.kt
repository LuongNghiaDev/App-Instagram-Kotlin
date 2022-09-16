package com.example.appinstagramremakekotlin.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.adapter.AddCommentAdapter
import com.example.appinstagramremakekotlin.databinding.ActivityCommentPostBinding
import com.example.appinstagramremakekotlin.databinding.DialogCommentAddBinding
import com.example.appinstagramremakekotlin.models.Comment
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentPostBinding
    private var postId = ""
    private var imagePost = ""
    private var postDesc = ""
    private var publisherID = ""
    private var comment = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var commentArrayList: ArrayList<Comment>
    private lateinit var adapterComment: AddCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        intent = intent
        postId = intent.getStringExtra("postId")!!
        imagePost = intent.getStringExtra("imagePost")!!
        postDesc = intent.getStringExtra("postDecription")!!
        publisherID = intent.getStringExtra("publisherId")!!

        publisherPost(binding.imageProfileComment, binding.usernameComment, publisherID)
        Picasso.get().load(imagePost).placeholder(R.drawable.profile).into(binding.postImageComment)
        binding.descComment.text = postDesc

        binding.btnAddComments.setOnClickListener {
            addCommentDialog()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        loadCommentDetail()
    }

    private fun addCommentDialog() {
        val commentDialog = DialogCommentAddBinding.inflate(LayoutInflater.from(this))

        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentDialog.root)

        val alertDialog = builder.create()
        alertDialog.show()

        commentDialog.btnBack.setOnClickListener { alertDialog.dismiss() }
        commentDialog.btnComment.setOnClickListener {
            comment = commentDialog.commentEt.text.toString()

            if(comment.isEmpty()) {
                Toast.makeText(this, "Enter comment...", Toast.LENGTH_LONG).show()
            } else {
                alertDialog.dismiss()
                addComment()
            }
        }

    }

    private fun addComment() {
        progressDialog.setMessage("Add Comment...")
        progressDialog.show()

        val timestamp = "${System.currentTimeMillis()}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["postId"] = "$postId"
        hashMap["timestamp"] = "$timestamp"
        hashMap["comment"] = "$comment"
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.child(postId).child("Comments").child(timestamp)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Add Comment Successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed Add Comment", Toast.LENGTH_LONG).show()
            }
    }

    private fun publisherPost(profileImage: CircleImageView, userName: TextView, publisherId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding.imageProfileComment)
                    binding.usernameComment.text = user!!.getUsername()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadCommentDetail() {
        commentArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.child(postId).child("Comments")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    commentArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(Comment::class.java)

                        commentArrayList.add(model!!)
                    }

                    adapterComment = AddCommentAdapter(this@CommentPostActivity, commentArrayList)
                    binding.commentRv.adapter = adapterComment
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}