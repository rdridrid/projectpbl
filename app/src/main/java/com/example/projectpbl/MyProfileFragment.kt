package com.example.projectpbl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.projectpbl.databinding.FragmentMyProfileBinding
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) { //프래그먼트 호출시 실행
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView( //화면구성시 호출

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyProfileBinding.inflate(inflater,container, false)
        val auth=Firebase.auth
        val database = Firebase.database
        val databaseRef = Firebase.database.getReference()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference()
        val photo = binding.myprofileImage
        val uid= Firebase.auth.currentUser!!.uid
        val filename = uid + "profileimage"
        val itemsRef = database.getReference("Users").child(uid)
        val UserName = binding.myprofileUsername
        val UserStatusMessage = binding.myprofileStatusmsg
        var imageUritemp: String
        binding.editMyprofile.visibility=View.GONE
        //binding.editMyprofile.isEnabled=false//혹시몰라서 비활성화까지 했음
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    photo.setImageURI(imageUri)
                    binding.editMyprofile.visibility=View.VISIBLE
                }
            }

        photo.setOnClickListener { //image view 눌렀을 때 이미지 변경
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        itemsRef.addValueEventListener(object :  ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    imageUritemp="default.png"
                    println(child.key)
                    if(child.key=="userProfileImageUri"){
                        imageUritemp = child.value.toString() //filename
                    }
                    val profileimageRef=storageRef.child(imageUritemp)
                    profileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                        val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                        photo.setImageBitmap(bmp)
                    }?.addOnFailureListener {
                        println("실패")
                    }
                    if(child.key=="userName") {
                        val test =child.value.toString()
                        UserName.setText(test)
                    }
                    if(child.key=="UserStatusMessage"){
                        val temp=child.value.toString()
                        UserStatusMessage.setText(temp)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadItem:onCancelled")
            }
        })

        binding.editMyprofile.setOnClickListener {
                val tempprofileusername = UserName.text.toString()
                val tempprofileuserstatusmessage = UserStatusMessage.text.toString()
                if (tempprofileusername.isEmpty()) {
                    //Toast.makeText(this@MyProfileFragment, "이름은 반드시 들어가야합니다!", Toast.LENGTH_SHORT).show()
                } else { //프로필 변경 성공
                    databaseRef.child("Users").child(uid!!).child("userName")
                        .setValue(tempprofileusername)
                    databaseRef.child("Users").child(uid!!).child("UserStatusMessage")
                        .setValue(tempprofileuserstatusmessage)
                    val profileimageRef = storageRef.child(filename)
                    photo.isDrawingCacheEnabled=true
                    val bitmap=photo.drawToBitmap()
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
                    val data=baos.toByteArray()
                    val uploadTask=profileimageRef.putBytes(data)
                    uploadTask.addOnFailureListener{
                        //Toast.makeText(this@MyProfileFragment, "프로필 이미지 변경 실패.-예기치못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener{ taskSnapshot->
                        databaseRef.child("Users").child(uid!!).child("userProfileImageUri").setValue(filename)
                        //Toast.makeText(this@MyProfileFragment, "프로필 이미지 변경 실패.-예기치못한 오류가 발생했습니다.", Toast.LE
                    }
                }
        }
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_profile, container, false)

        return binding.root
    }


    companion object {
        val fireStorage = FirebaseStorage.getInstance().reference
        var imageUri : Uri? = null
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }
}