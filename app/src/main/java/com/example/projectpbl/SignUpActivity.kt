package com.example.projectpbl

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpbl.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity(){
    private var auth : FirebaseAuth? = null
    private val database = Firebase.database
    //private var

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        //database.getReference(path)형식으로 사용
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.SignUpConfirmButton.setOnClickListener {
            val email=binding.SignUpUserEmail.text.toString()
            val password=binding.SignUpUserPassword.text.toString()
            val SignUpUserName=binding.SignUpUserName.text.toString()
            val ConfirmPassword=binding.SignUpUserConfirmPassword.text.toString()
            if (email.isEmpty() || password.isEmpty() || ConfirmPassword.isEmpty() || SignUpUserName.isEmpty()) {
                if (email.isEmpty())
                    Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                else if(password.isEmpty()){
                    Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else if(password.length<6){
                    Toast.makeText(this, "비밀번호는 6자리 이상이여야 합니다..", Toast.LENGTH_SHORT).show()
                }
                else if(ConfirmPassword.isEmpty())
                {
                    Toast.makeText(this, "비밀번호를 다시 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "이름이 없습니다..", Toast.LENGTH_SHORT).show()
                }
            }
            else if(password == ConfirmPassword)
                createAccount(email,password,SignUpUserName)
            else{
                Toast.makeText(this, "비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        binding.SignUpBackbtn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun createAccount(email:String,password:String, SignUpUserName:String){
        if(email.isNotEmpty()&&password.isNotEmpty()){
            auth?.createUserWithEmailAndPassword(email,password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful){
                        val uid=it.result.user?.uid
                        database.getReference("Users").child(uid!!).setValue(UserData(uid,email,password,SignUpUserName))
                        Toast.makeText(
                            this,"계정 생성에 성공하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(
                            this,"계정 생성에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}