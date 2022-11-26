package com.example.projectpbl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class OtherProfileActivity : AppCompatActivity() { // 다른 사람 프로필 ( 친구 리스트에서 친구 이름 선택 시 상대 프로필로 이동 )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile)
    }
}