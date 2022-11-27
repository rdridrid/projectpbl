package com.example.projectpbl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    companion object{

    }
    private lateinit var database:DatabaseReference
    private var post: ArrayList<PostModel> = arrayListOf()
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
        recyclerView.adapter=RecyclerPostAdapter()
        return view
    }
    inner class RecyclerPostAdapter: RecyclerView.Adapter<RecyclerPostAdapter.PostViewHolder>(){
        init{val database = Firebase.database.getReference()
            val PostsRef = database.child("Posts")
            PostsRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    println("실패")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    post.clear()
                    for (data in snapshot.children){
                        val temppostid=data.child("postfileImageUri").getValue().toString() //post uri
                        val tempposttitle=data.child("Posttitle").getValue().toString() //Posts/posturi == post의 id와 동일함
                        val temppost = PostModel(tempposttitle, "none", "ab", "none")
                        post.add(temppost!!)
                    }
                    notifyDataSetChanged()
                }
            })
        }//val myUid = Firebase.auth.currentUser!!.uid
        override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): PostViewHolder{
            return PostViewHolder(LayoutInflater.from(context).inflate(R.layout.postlist_item,parent,false))

        }
        inner class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
            val postname : TextView = itemView.findViewById(R.id.posttitle_postitem)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

            holder.postname.text=post[position].title.toString()
        }

        override fun getItemCount(): Int {
            return post.size
        }
    }

}