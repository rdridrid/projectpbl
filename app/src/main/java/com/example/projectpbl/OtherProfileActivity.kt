package com.example.projectpbl

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectpbl.databinding.ActivityFindpasswordBinding
import com.example.projectpbl.databinding.ActivityOtherProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class OtherProfileActivity : AppCompatActivity() {
    // 다른 사람 프로필 ( 친구 리스트에서 친구 이름 선택 시 상대 프로필로 이동 )
    // 또는 포스팅에서 상대 프로필 사진 클릭 시 이동
    lateinit var otherusername : String
    lateinit var otheruseremail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val otheruserprofile = intent.getStringExtra("uid")
        val auth= Firebase.auth
        val database=Firebase.database
        val storage = FirebaseStorage.getInstance()
        val storageRef=storage.getReference()
        val uid=intent.getStringExtra("uid")//
        val myuid=Firebase.auth.currentUser!!.uid
        val photo=binding.otherprofileImage //!!는 nullable이 어차피 허용안된다는 뜻인가?
        val itemsRef = database.getReference("Users").child(uid!!)
        val myfriendlistRef=database.getReference("Friends").child(myuid)
        val otherUsername=binding.otherprofileUsername
        val otherUserstatusMessage=binding.otherprofileStatusmsg
        itemsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(child in snapshot.children){
                    if(child.key=="userName"){
                        otherusername=child.value.toString()
                        otherUsername.text=otherusername
                    }
                    if(child.key=="userProfileImageUri"){
                        val testtemp=child.value.toString()
                        val profileimageRef=storageRef.child(testtemp)
                        profileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                            val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                            photo.setImageBitmap(bmp)
                        }?.addOnFailureListener { 
                            println("실패")
                        }
                    }
                    if(child.key=="email"){
                        otheruseremail=child.value.toString()
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("실패")
            }
        })
        val addfriendbtn = binding.otherprofileAdd
        addfriendbtn.setOnClickListener {//push아이디 자동생성
            myfriendlistRef.child(uid).setValue(uid)
            myfriendlistRef.child(uid).child("userName").setValue(otherusername)
            myfriendlistRef.child(uid).child("email").setValue(otheruseremail)
            myfriendlistRef.child(uid).child("profileimage").setValue(uid+"profileimage")
        }

    }
}