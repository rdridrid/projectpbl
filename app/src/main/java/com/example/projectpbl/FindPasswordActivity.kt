package com.example.projectpbl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivityFindpasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FindPasswordActivity : AppCompatActivity(){  //비밀번호 재설정
    private var auth: FirebaseAuth? =null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        val binding = ActivityFindpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.FindPasswordConfirmButton.setOnClickListener {
            val email=binding.FindPasswordUseEmail.text.toString()
        }
    }
    private fun resetPassword(email:String){
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener(this){
                if(it.isSuccessful){
                    println("비밀번호 재설정 메일 송신 성공")
                }
            }
    }
}