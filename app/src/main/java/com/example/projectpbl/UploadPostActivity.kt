package com.example.projectpbl

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectpbl.databinding.ActivityMainBinding
import com.example.projectpbl.databinding.ActivityUploadPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime

class UploadPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUploadPostBinding.inflate(layoutInflater)
        val postimage = binding.uploadImage
        val postimageUri: Uri?= null
        val postcontenttext=binding.uploadText //글은 반드시 써야한다.
        val auth= Firebase.auth
        val database = Firebase.database.getReference()
        val storage = FirebaseStorage.getInstance()
        val storageRef= storage.getReference()
        val myuid=auth.currentUser!!.uid
        val postid = myuid+LocalDateTime.now()  //현재 업로드 시간
        val postRef=database.child("Posts") //Post하위 참조
        setContentView(binding.root)
        val getContent=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
                if(result.resultCode ==AppCompatActivity.RESULT_OK){
                    val postimageUri = result.data?.data
                    postimage.setImageURI(postimageUri)
                    //여기서도 포스트 이미지는 올려놓기만한다.

                }

            }
        binding.btnUploadimage.setOnClickListener {//이미지 업로드
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type= MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        binding.btnCancle.setOnClickListener { // Home 화면으로 돌아가기
            onBackPressed()
        }

        binding.btnUpload.setOnClickListener { // 업로드 버튼
            val tempcontent = postcontenttext.text.toString()

        }


    }
}