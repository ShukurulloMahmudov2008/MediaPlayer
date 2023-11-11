package com.example.mediaplayer

import java.io.Serializable

data class Music(val id:Long, val title:String, val imagePath:String, val musicPath:String, val author:String):Serializable