package com.example.appinstagramremakekotlin.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appinstagramremakekotlin.R
import com.example.appinstagramremakekotlin.databinding.ActivityAddVideoBinding
import com.example.appinstagramremakekotlin.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class AddVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVideoBinding
    private val VIDEO_PICK_GALLERY = 100
    private val VIDEO_PICK_CAMERA = 101
    private val CAMERA_REQUEST_CODE = 102
    private var userImage: String = ""
    private lateinit var profileId: String

    private lateinit var cameraPermission:Array<String>
    private var videoUri: Uri? = null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val pref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        userInfo()

        binding.btnDone.setOnClickListener {
            uploadVideo()
        }

        binding.pickVideo.setOnClickListener {
            videoPickDialog()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Video From")
            .setItems(options) { dialog, i ->
                if(i==0) {
                    if(!checkCameraPermisson()) {
                        requestCameraPermisson()
                    } else {
                        videoPickCamera()
                    }
                } else {
                    videoPickGallery()
                }
            }
            .show()
    }

    private fun uploadVideo() {
        val timestamp=""+System.currentTimeMillis()

        val filePathAndName = "Videos/video_$timestamp"
        val title = binding.titleOfAddVideo.text.toString()

        progressDialog.setMessage("Uploading Video...")
        progressDialog.show()

        val ref = FirebaseStorage.getInstance().getReference(filePathAndName)
        ref.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val downloadUri = uriTask.result

                if(uriTask.isSuccessful) {
                    val hashMap = HashMap<String, Any>()
                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["videoUri"] = "$downloadUri"
                    hashMap["image"] = "$userImage"

                    val dbRef = FirebaseDatabase.getInstance().getReference("Videos")
                    dbRef.child(timestamp)
                        .setValue(hashMap)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Video Uploaded", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Failed Video Uploaded", Toast.LENGTH_LONG).show()
                        }

                }
            }
            .addOnFailureListener {

            }
    }

    private fun checkCameraPermisson():Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(
            intent,
            VIDEO_PICK_CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE ->
                if(grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccept && storageAccept) {
                        videoPickCamera()
                    } else {
                        Toast.makeText(this, "Permission", Toast.LENGTH_LONG).show()
                    }
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestCameraPermisson() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            CAMERA_REQUEST_CODE
        )
    }

    private fun videoPickGallery() {
        val intent = Intent()
        intent.type = "video/*"

        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose Video"),
            VIDEO_PICK_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK) {
            if(requestCode == VIDEO_PICK_CAMERA) {
                videoUri = data!!.data
                setVideoToVideoView()
            } else if(requestCode == VIDEO_PICK_GALLERY) {
                videoUri = data!!.data
                setVideoToVideoView()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.requestFocus()
        binding.videoView.setOnPreparedListener {
            binding.videoView.pause()
        }

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    userImage = user!!.getImage()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}