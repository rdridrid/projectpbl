package com.example.projectpbl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectpbl.databinding.ActivityMainBinding
import com.example.projectpbl.databinding.ActivityUploadPostBinding

class UploadPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUploadPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancle.setOnClickListener { // Home 화면으로 돌아가기
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.btnUpload.setOnClickListener { // 업로드 버튼

        }


    }
}