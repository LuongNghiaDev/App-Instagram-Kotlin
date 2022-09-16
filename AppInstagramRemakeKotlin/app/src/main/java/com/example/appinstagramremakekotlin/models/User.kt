package com.example.appinstagramremakekotlin.models

class User {

    private var username:String = ""
    private var fullname:String = ""
    private var bio:String = ""
    private var image:String = ""
    private var uid:String = ""

    constructor(username: String, fullname: String, bio: String, image: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
    }

    constructor()

    fun setUsername(username: String) {
        this.username = username
    }

    fun getUsername():String {
        return username
    }

    fun setFullname(fullname: String) {
        this.fullname = fullname
    }

    fun getFullname():String {
        return fullname
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getBio():String {
        return bio
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getImage():String {
        return image
    }

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getUid():String {
        return uid
    }
}