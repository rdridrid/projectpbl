package com.example.projectpbl

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
import androidx.core.view.drawToBitmap
import com.example.projectpbl.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

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
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    photo.setImageURI(imageUri)
                    //여기서는 올려놓기만하고 바뀐 image파일에 대해서만 비트맵 전환후 올리면될듯
                } else {
                }
            }

        edit.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        } //Image바꾸기


        usersRef.addValueEventListener(object : ValueEventListener {
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
                    if(child.key=="userProfileImageUri"){
                        val testtemp = child.value.toString()
                        val profileimageRef = storageRef.child(testtemp)
                        profileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                            photo.setImageBitmap(bmp)
                        }?.addOnFailureListener{
                            println("실패")
                        }

                    }
                    //download Uri
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadItem:onCancelled")
            }
        })
        binding.editMyprofileCheck.setOnClickListener {
            val tempprofileusername = UserName.text.toString()
            val tempprofileuserstatusmessage = UserStatusMessage.text.toString()
            val profileimgRef = storageRef.child(filename)
            photo.isDrawingCacheEnabled = true
            val bitmap = photo.drawToBitmap()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = profileimgRef.putBytes(data)
            uploadTask.addOnFailureListener{
                Toast.makeText(this@ProfileEditActivity, "프로필 사진 업데이트에 실패했습니다..", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                database.child("Users").child(uid!!).child("userProfileImageUri")
                    .setValue(filename)
            }
            if (tempprofileusername.isEmpty()) {
                Toast.makeText(this@ProfileEditActivity, "이름은 반드시 들어가야합니다!", Toast.LENGTH_SHORT).show()
            } else { //프로필 변경 성공
                database.child("Users").child(uid!!).child("userName")
                    .setValue(tempprofileusername)
                database.child("Users").child(uid!!).child("UserStatusMessage")
                    .setValue(tempprofileuserstatusmessage)
                database.child("Users").child(uid!!).child("userProfileImageUri")
                    .setValue(filename)
            }
            finish()
        }
    }
}