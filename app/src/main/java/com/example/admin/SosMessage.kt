package com.example.admin

data class SosMessage(
    var pkey : String = "",
    var name : String = "",
    var content : String = "",
    var timeStamp : String = "",
    var isRead : Boolean = false
)
