package com.example.models


data class Payload (

        val pid : String,
        val name : String,
        val details : String,
        val price : Int,
        val image : String,
        val created_on : String,
        val category_id : String
)