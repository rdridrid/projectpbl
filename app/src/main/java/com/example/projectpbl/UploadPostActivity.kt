package com.example.projectpbl

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import com.example.projectpbl.databinding.ActivityMainBinding
import com.example.projectpbl.databinding.ActivityUploadPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UploadPostActivity : AppCompatActivity() {
    lateinit var ownerprofileimage : String
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
        val tempdate=date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
        val postid = date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))+myuid //현재 업로드 시간 포맷팅
        val UserRef=database.child("Users").child(myuid)
        val postRef=database.child("Posts") //Post하위 참조
        var myname : String=""
        var myemail: String=""
        var ownerprofileimage: String = ""
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
        UserRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(child in snapshot.children){
                    if(child.key=="userName") {
                        myname = child.value.toString()
                    }
                    if(child.key=="email"){
                        myemail =child.value.toString()
                    }
                    ownerprofileimage="default.png"
                    if(child.key=="profileimage"){ //디폴트로 둿다가 만약에 프로필이 있으면 바꾸는것
                        ownerprofileimage=child.value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UploadPostActivity, "포스트 업로드 실패.-예기치못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()


            }
        })

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
                if(tempposttitle.isEmpty()){
                    println("타이틀 없음")
                }else if(tempcontent.isEmpty()){
                    println("내용 없음")
                }
                else{
                    database.child("Posts").child(postid!!).child("postfileImageUri").setValue(postid) //postid가 파일 이름도 됨
                    database.child("Posts").child(postid!!).child("Postcontent").setValue(tempcontent)
                    database.child("Posts").child(postid!!).child("Posttitle").setValue(tempposttitle)
                    database.child("Posts").child(postid!!).child("uid").setValue(myuid)
                    database.child("Posts").child(postid!!).child("time").setValue(tempdate)
                    database.child("Posts").child(postid!!).child("username").setValue(myname)
                    database.child("Posts").child(postid!!).child("useremail").setValue(myemail)
                    database.child("Posts").child(postid!!).child("profileimage").setValue(ownerprofileimage)
                    onBackPressed()
                    Toast.makeText(this@UploadPostActivity, "포스트 성공.", Toast.LENGTH_SHORT).show()
                }

            }


        }



    }
}