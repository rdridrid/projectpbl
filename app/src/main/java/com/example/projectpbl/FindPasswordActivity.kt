package com.example.projectpbl

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivityFindpasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FindPasswordActivity : AppCompatActivity(){  //비밀번호 재설정
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        val binding = ActivityFindpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.FindPasswordConfirmButton.setOnClickListener {
            val email = binding.FindPasswordUseEmail.text.toString()
            setUpdatePasswordBtn(email)
        }

        binding.backLogin.setOnClickListener {
            onBackPressed();
        }
    }

    private fun setUpdatePasswordBtn(email : String) { // 비밀번호 재설정 버튼 이벤트
        // overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        // Dialog 띄우기
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("비밀번호 재설정")
        builder.setMessage("비밀번호 재설정 이메일을 보내시겠습니까?")

        builder.setPositiveButton("예"
        ) { dialogInterface: DialogInterface, i: Int ->
            sendEmailForPasswordUpdate(email)
        }
        builder.setNegativeButton("아니오"
        ) { dialogInterface: DialogInterface, i: Int ->

        }
        builder.show()
    }

    private fun sendEmailForPasswordUpdate(email : String) {
        auth?.sendPasswordResetEmail(email)?.addOnCompleteListener {
            if(it.isSuccessful){
                    //Snackbar.make(window.decorView.rootView,"이메일을 보냈습니다.", Snackbar.LENGTH_LONG).show()
                Toast.makeText(this,"이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
            }else{
                    //Snackbar.make(window.decorView.rootView,"이메일 발송이 실패했습니다.", Snackbar.LENGTH_LONG).show()
                Toast.makeText(this, "이메일 발송이 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    private fun resetPassword(email:String){
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener(this){
                if(it.isSuccessful){
                    println("비밀번호 재설정 메일 송신 성공")
                }
                else {
                    println("등록되지 않은 이메일입니다.")
                }
            }
    } */
}