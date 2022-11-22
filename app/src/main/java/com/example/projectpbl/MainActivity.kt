package com.example.projectpbl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
class MainActivity : AppCompatActivity(){
    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.LoginButton.setOnClickListener {
            val userEmail = binding.SignInUserEmail.text.toString()
            val password = binding.SignInUserPassword.text.toString()
            doLogin(userEmail,password)
        }
        binding.SignUpButton.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.FindPassword.setOnClickListener {
            startActivity(Intent(this,FindPasswordActivity::class.java))
        }
    }
    private fun doLogin(userEmail: String, password:String){
        Firebase.auth.signInWithEmailAndPassword(userEmail,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
                }else{
                    Log.w("MainActivity","signInWithEmail",it.exception)
                    Toast.makeText(this,"Authentication failed", Toast.LENGTH_SHORT)
                }
            }

    }

}