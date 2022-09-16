package com.example.appinstagramremakekotlin

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MyApplication: Application() {

    /*fun getUID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }*/

    override fun onCreate() {
        super.onCreate()
    }

    companion object {

        fun formatTimestamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        public fun removeFromBookmark(context: Context, bookId: String) {
            val firebaseAuth = FirebaseAuth.getInstance()

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Bookmarks").child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Remove Bookmark", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed remove bookmark", Toast.LENGTH_LONG).show()
                }
        }

    }
}