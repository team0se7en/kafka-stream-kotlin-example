package com.example.models

data class Schema (

        val type : String,
        val fields : List<Fields>,
        val optional : Boolean,
        val name : String
)
