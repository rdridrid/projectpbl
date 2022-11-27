package com.example.projectpbl

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import com.example.projectpbl.databinding.ActivityMainBinding
import com.example.projectpbl.databinding.ActivityUploadPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UploadPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUploadPostBinding.inflate(layoutInflater)
        val postimage = binding.uploadImage
        val postimageUri: Uri?= null
        val postcontenttext=binding.uploadText //글은 반드시 써야한다.
        val posttitle=binding.uploadPosttitle
        val auth= Firebase.auth
        val database = Firebase.database.getReference()
        val storage = FirebaseStorage.getInstance()
        val storageRef= storage.getReference()
        val myuid=auth.currentUser!!.uid
        val date=LocalDateTime.now()
        val postid = myuid+date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))  //현재 업로드 시간 포맷팅
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

        binding.btnUploadimageClear.setOnClickListener {
            val imageview = binding.uploadImage
            imageview.setImageDrawable(resources.getDrawable(R.drawable.ic_addphoto))
        }

        binding.btnUpload.setOnClickListener { // 업로드 버튼
            val tempcontent = postcontenttext.text.toString()
            val tempposttitle=posttitle.text.toString()
            val postfileimagRef = storageRef.child(postid)
            postimage.isDrawingCacheEnabled=true
            val bitmap = postimage.drawToBitmap()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
            val data=baos.toByteArray()
            val uploadTask = postfileimagRef.putBytes(data) //데이터 저장해놓고
            uploadTask.addOnFailureListener{
                println("포스트업로드 실패")
            }.addOnSuccessListener { taskSnapshot->
                database.child("Posts").child(postid!!).child("postfileImageUri").setValue(postid) //postid가 파일 이름도 됨
                println("포스트 업로드 성공")
            }
            if(tempposttitle.isEmpty()){
                println("타이틀 없음")
            }else if(tempcontent.isEmpty()){
                println("내용 없음")
            }
            else{
                database.child("Posts").child(postid!!).child("Postcontent").setValue(tempcontent)
                database.child("Posts").child(postid!!).child("Posttitle").setValue(tempposttitle)
                database.child("Posts").child(postid!!).child("uid").setValue(myuid)
                onBackPressed()
            }

        }



    }
}