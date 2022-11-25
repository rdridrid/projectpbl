package com.example.projectpbl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.projectpbl.databinding.FragmentMyProfileBinding
import com.example.projectpbl.databinding.FragmentMyProfileEditBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyProfileEditBinding.inflate(inflater,container, false)
        val auth= Firebase.auth
        val database = Firebase.database
        val uid= Firebase.auth.currentUser!!.uid
        val itemsRef = database.getReference("Users").child(uid)
        val UserName = binding.editMyprofileUsername
        val UserStatusMessage = binding.editMyprofileStatusMessage
        itemsRef.addValueEventListener(object :  ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
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
        binding.editMyprofileCheck.setOnClickListener {
            val tempprofileusername=UserName.text.toString()
            val tempprofileuserstatusmessage=UserStatusMessage.text.toString()
            if(tempprofileusername.isEmpty()) {
               println("이름이 없음")
            }
            else{ //프로필 변경 성공
                database.getReference("Users").child(uid!!).child("userName").setValue(tempprofileusername)
                database.getReference("Users").child(uid!!).child("UserStatusMessage").setValue(tempprofileuserstatusmessage)
            }
            (activity as HomeActivity)changeFragmentWithBackStack(MyProfileFragment.newInstance())
        }
        return binding.root
    }

    companion object{
        @JvmStatic
        fun newInstance() = MyProfileEditFragment()
    }
}