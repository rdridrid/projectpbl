package com.example.projectpbl

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide

import com.example.projectpbl.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class ProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth
        val database = Firebase.database.getReference()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference()
        val uid = Firebase.auth.currentUser!!.uid
        val UserName = binding.editMyprofileUsername
        var imageUri: Uri? = null
        val usersRef = database.child("Users").child(uid)
        val UserStatusMessage = binding.editMyprofileStatusMessage
        val photo = binding.editMyprofileImage
        val edit = binding.editMyprofileImageText
        val filename = uid+"profileimage"
        val fireStorage = FirebaseStorage.getInstance().reference
        val fireDatabase = FirebaseDatabase.getInstance().reference
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    println("imageUri $imageUri")
                    photo.setImageURI(imageUri)
                    //여기서는 올려놓기만하고 바뀐 image파일에 대해서만 비트맵 전환후 올리면될듯
                    fireStorage.child(filename).putFile(imageUri!!).addOnSuccessListener {
                        fireStorage.child(filename).downloadUrl.addOnSuccessListener {
                            val photoUri : Uri = it
                            println(photoUri)
                            fireDatabase.child("Users/$uid/userProfileImageUri").setValue(photoUri.toString())
                            Toast.makeText(this, "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        edit.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        } //Image바꾸기


        usersRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("CheckResult")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    if (child.key == "userName") {
                        val test = child.value.toString()
                        UserName.setText(test)
                    }
                    if (child.key == "UserStatusMessage") {
                        val temp = child.value.toString()
                        UserStatusMessage.setText(temp)
                    }
                    if(child.key=="userProfileImageUri") {
                        val testtemp = child.value.toString()
                        val uri = Uri.parse(testtemp)
                        println(uri)
                        Glide.with(applicationContext).load(uri).into(photo)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadItem:onCancelled")
            }
        })
        binding.editMyprofileCheck.setOnClickListener {
            val tempprofileusername = UserName.text.toString()
            val tempprofileuserstatusmessage = UserStatusMessage.text.toString()
            if (tempprofileusername.isEmpty()) {
                println("이름이 없음")
            } else { //프로필 변경 성공
                database.child("Users").child(uid!!).child("userName")
                    .setValue(tempprofileusername)
                database.child("Users").child(uid!!).child("UserStatusMessage")
                    .setValue(tempprofileuserstatusmessage)
            }
            onBackPressed()
        }

    }
}