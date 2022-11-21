package com.example.projectpbl

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity(){
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.SignUpConfirmButton.setOnClickListener {
            val email=binding.SignUpUserEmail.text.toString()
            val password=binding.SignUpUserPassword.text.toString()
            createAccount(email,password)
        }
    }
    private fun createAccount(email:String,password:String){
        if(email.isNotEmpty()&&password.isNotEmpty()){
            auth?.createUserWithEmailAndPassword(email,password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(
                            this,"계정 생성 완료",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }else{
                        Toast.makeText(
                            this,"계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}