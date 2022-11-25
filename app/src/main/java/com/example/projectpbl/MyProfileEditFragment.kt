package com.example.projectpbl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectpbl.databinding.FragmentMyProfileBinding
import com.example.projectpbl.databinding.FragmentMyProfileEditBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }



        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val binding = FragmentMyProfileEditBinding.inflate(inflater, container, false)
            val auth = Firebase.auth
            val database = Firebase.database
            val uid = Firebase.auth.currentUser!!.uid
            val itemsRef = database.getReference("Users").child(uid)
            val UserName = binding.editMyprofileUsername
            val UserStatusMessage = binding.editMyprofileStatusMessage
            val photo = binding.editMyprofileImage
            val edit = binding.editMyprofileImageText

            val getContent =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if(result.resultCode == AppCompatActivity.RESULT_OK) {

                        imageUri = result.data?.data //이미지 경로 원본
                        photo.setImageURI(imageUri)

                        /*
                        //기존 사진을 삭제 후 새로운 사진을 등록
                        fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                            fireStorage.child("userImages/$uid/photo").putFile(imageUri!!).addOnSuccessListener {
                                fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener {
                                    val photoUri : Uri = it
                                    println("$photoUri")
                                    fireDatabase.child("users/$uid/profileImageUrl").setValue(photoUri.toString())
                                    Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } */
                        Log.d("이미지", "성공")
                    }
                    else{
                        Log.d("이미지", "실패")
                    }
                }

            edit.setOnClickListener {
                val intentImage = Intent(Intent.ACTION_PICK)
                intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
                getContent.launch(intentImage)
            }


            itemsRef.addValueEventListener(object : ValueEventListener {
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
                    database.getReference("Users").child(uid!!).child("userName")
                        .setValue(tempprofileusername)
                    database.getReference("Users").child(uid!!).child("UserStatusMessage")
                        .setValue(tempprofileuserstatusmessage)
                }
                (activity as HomeActivity) changeFragmentWithBackStack (MyProfileFragment.newInstance())
            }
            return binding.root
        }



        companion object {
            private var imageUri : Uri? = null
            private val fireStorage = FirebaseStorage.getInstance().reference
            private val fireDatabase = FirebaseDatabase.getInstance().reference
            private val user = Firebase.auth.currentUser
            private val uid = user?.uid.toString()

            fun newInstance() : MyProfileEditFragment {
                return MyProfileEditFragment()
            }
        }
}
