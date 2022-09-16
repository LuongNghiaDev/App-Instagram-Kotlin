package com.example.appinstagramremakekotlin.models

class Message(
    var senderId:String,
    var receiverId:String,
    var message:String,
    var date:String = System.currentTimeMillis().toString(),
    var type:String
)