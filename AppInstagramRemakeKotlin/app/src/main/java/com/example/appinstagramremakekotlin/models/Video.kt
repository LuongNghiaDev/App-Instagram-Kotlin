package com.example.appinstagramremakekotlin.models

class Video {

    var id:String? = ""
    var title: String? = ""
    var timestamp: String? = ""
    var videoUri: String? = ""
    var image: String? = ""

    constructor()

    constructor(
        id: String?,
        title: String?,
        timestamp: String?,
        videoUri: String?,
        image: String?
    ) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.videoUri = videoUri
        this.image = image
    }


}