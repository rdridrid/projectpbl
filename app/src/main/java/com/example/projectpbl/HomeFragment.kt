package com.example.projectpbl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var database:DatabaseReference
    private var friend: ArrayList<PostModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        database= Firebase.database.reference
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.lstPost)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        //recyclerView.adapter=RecyclerPostAdapter()
        return view
    }
    /*inner class RecyclerPostAdapter: RecyclerView.Adapter<RecyclerPostAdapter.PostViewHolder>(){
        //val myUid = Firebase.auth.currentUser!!.uid
        val database = Firebase.database.getReference()
        val PostsRef = database.child("Posts")

    }*/
}