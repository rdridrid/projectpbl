package com.example.projectpbl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectpbl.databinding.ActivityFindpasswordBinding
import com.example.projectpbl.databinding.ActivityOtherProfileBinding

class OtherProfileActivity : AppCompatActivity() {
    // 다른 사람 프로필 ( 친구 리스트에서 친구 이름 선택 시 상대 프로필로 이동 )
    // 또는 포스팅에서 상대 프로필 사진 클릭 시 이동
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getStringExtra("uid")
        println(data)
    }
}