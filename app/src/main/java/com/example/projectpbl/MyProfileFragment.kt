package com.example.projectpbl

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide

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
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference()
        val photo = binding.myprofileImage
        val uid= Firebase.auth.currentUser!!.uid
        val filename = uid + "profileimage"
        val itemsRef = database.getReference("Users").child(uid)
        val UserName = binding.myprofileUsername
        val UserStatusMessage = binding.myprofileStatusmsg
/*
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    println("imageUri $imageUri")
                    photo.setImageURI(imageUri)
                    //여기서는 올려놓기만하고 바뀐 image파일에 대해서만 비트맵 전환후 올리면될듯
                        // 기존 프로필 사진 따로 delete 안하고 새로 putFile ( 동일한 filename ) --> replace
                        fireStorage.child(filename).putFile(imageUri!!).addOnSuccessListener {
                            println("putFile") //  동일한 filename으로 putFile --> 덮어쓰기가 됨
                            fireStorage.child(filename).downloadUrl.addOnSuccessListener {
                                val photoUri : Uri = it
                                println(photoUri)
                                fireDatabase.child("Users/$uid/userProfileImageUri").setValue(photoUri.toString())
                                Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        photo.setOnClickListener { //image view 눌렀을 때 이미지 변경
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
 */
        itemsRef.addValueEventListener(object :  ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    if(child.key=="userProfileImageUri"){
                        val testtemp = child.value.toString() //filename
                        val uri = Uri.parse(testtemp)
                        println(testtemp)
                        Glide.with(requireContext())
                            .load(uri).into(photo)
                    }
                    if(child.key=="userName") {
                        val test =child.value.toString()
                        UserName.text = test
                    }
                    if(child.key=="UserStatusMessage"){
                        val temp=child.value.toString()
                        UserStatusMessage.text=temp
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadItem:onCancelled")
            }
        })

        binding.editMyprofile.setOnClickListener {
            startActivity(Intent(activity, ProfileEditActivity::class.java))
        }
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_profile, container, false)

        return binding.root
    }


    companion object {
        val fireStorage = FirebaseStorage.getInstance().reference
        val fireDatabase = FirebaseDatabase.getInstance().reference
        var imageUri : Uri? = null
        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }
}