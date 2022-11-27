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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.projectpbl.databinding.FragmentMyProfileBinding
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
        val profileimage = binding.myprofileImage
        val uid= Firebase.auth.currentUser!!.uid
        val itemsRef = database.getReference("Users").child(uid)
        val UserName = binding.myprofileUsername
        val UserStatusMessage = binding.myprofileStatusmsg

        itemsRef.addValueEventListener(object :  ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    if(child.key=="userProfileImageUri"){ //uri는 존재하고
                        val testtemp = child.value.toString()
                        println(testtemp)
                        //file uri가 나옴
                        val profileimageRef = storageRef.child(testtemp)
                        profileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                            profileimage.setImageBitmap(bmp)
                        }?.addOnFailureListener{
                            println("실패")
                        }
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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_profile, container, false)
        binding.editMyprofile.setOnClickListener {
            startActivity(Intent(activity, ProfileEditActivity::class.java))
        }
        return binding.root
    }


    companion object {

        @JvmStatic
        fun newInstance() = MyProfileFragment()
    }
}