package com.example.appinstagramremakekotlin.activities

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.MediaController
import com.example.appinstagramremakekotlin.databinding.ActivityVideoBinding
import java.util.*

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val video = intent.getStringExtra("videoUriMain")
        val title = intent.getStringExtra("titleVideo")
        val time = intent.getStringExtra("timeVideo")

        val calender = Calendar.getInstance()
        calender.timeInMillis = time!!.toLong()
        val formatDateTime = DateFormat.format("dd/MM/yyy K:mm a", calender).toString()
        binding.timeTv.text = formatDateTime
        binding.titleTv.text = title

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoViewMain)
        val videoUri = Uri.parse(video)

        binding.videoViewMain.setMediaController(mediaController)
        binding.videoViewMain.setVideoURI(videoUri)
        binding.videoViewMain.requestFocus()

        binding.videoViewMain.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            binding.progressBar.visibility = View.GONE
        }

        binding.videoViewMain.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
            when(what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    return@OnInfoListener true
                }
            }
            false
        })

        binding.videoViewMain.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.start()
        }
    }
}