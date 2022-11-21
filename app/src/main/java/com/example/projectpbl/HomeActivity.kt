package com.example.projectpbl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivityHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.success.text= Firebase.auth.currentUser?.uid?:"No Userr"
    }
}