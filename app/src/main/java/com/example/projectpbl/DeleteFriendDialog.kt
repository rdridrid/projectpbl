package com.example.projectpbl

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.projectpbl.databinding.DialogDeleteFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DeleteFriendDialog : DialogFragment() {
    lateinit var uid: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle=arguments
        val tempuid = bundle!!.getString("uid")
        if (tempuid != null) {
            uid=tempuid
        }
        val binding = DialogDeleteFriendBinding.inflate(inflater, container, false)
        val database= Firebase.database
        val storage = FirebaseStorage.getInstance()
        val storageRef=storage.getReference()
        val myuid=Firebase.auth.currentUser!!.uid
        val myfriendlistRef=database.getReference("Friends").child(myuid)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.deletefriendyesbutton.setOnClickListener {
            //setvisible도 변경할것
            dismiss()
            myfriendlistRef.child(uid).removeValue()
        }
        binding.deletefriendcancelbutton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

}