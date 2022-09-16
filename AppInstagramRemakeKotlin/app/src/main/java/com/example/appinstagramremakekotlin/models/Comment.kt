package com.example.appinstagramremakekotlin.models

class Comment {

    var id = ""
    var postId = ""
    var timestamp = ""
    var comment = ""
    var uid = ""

    constructor()

    constructor(id: String, postId: String, timestamp: String, comment: String, uid: String) {
        this.id = id
        this.postId = postId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
    }

}