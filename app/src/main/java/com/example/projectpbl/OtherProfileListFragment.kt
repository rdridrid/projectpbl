package com.example.projectpbl

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class OtherProfileListFragment : Fragment() {
    lateinit var uid : String
    fun getuid(uid : String){
        this.uid=uid
    }
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var tempimageuri: String
    private lateinit var temppostowneruseruid:String //==uid
    private var post:ArrayList<PostModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database= Firebase.database.reference
        storage=FirebaseStorage.getInstance()

        val view=inflater.inflate(R.layout.fragment_other_profile_list,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.otherlstPost)
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.adapter=RecyclerPostAdapter()
        return view
    }
    inner class RecyclerPostAdapter: RecyclerView.Adapter<RecyclerPostAdapter.otherPostViewHolder>(){
        init{
            val database=Firebase.database.getReference()
            val PostsRef=database.child("Posts")//넘어온거 확인
            PostsRef.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    post.clear()
                    for(data in snapshot.children){
                        val refekey=data.key.toString()
                        PostsRef.child(refekey).addValueEventListener(object:ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(child in snapshot.children){
                                    var tempposttitle=""
                                    var temppostowneruseruid=""
                                    var temptime=""
                                    var tempimageuri=""
                                    var tempcontent=""
                                    if(child.key=="Posttitle"){
                                        tempposttitle=child.child("Posttitle").key.toString()
                                        //println(tempposttitle)
                                    }
                                    /*
                                    else if(child.key=="uid"){
                                        temppostowneruseruid=child.getValue().toString()
                                    }
                                    else if(child.key=="postfileImageUri"){
                                        tempimageuri=child.getValue().toString()
                                    }
                                    else if(child.key=="Postcontent"){
                                        tempcontent=child.getValue().toString()  //본문
                                    }
                                    if(uid==temppostowneruseruid){
                                        val temppost = PostModel(tempposttitle, tempcontent, "" ,"","",tempimageuri,temppostowneruseruid,"")
                                        post.add(temppost!!)
                                    }*/
                                }
                                notifyDataSetChanged()
                            }
                        })
                    }
                    notifyDataSetChanged()
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): otherPostViewHolder {
            return otherPostViewHolder(LayoutInflater.from(context).inflate(R.layout.default_postlist_item,parent,false))
        }
        inner class otherPostViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val postname : TextView = itemView.findViewById(R.id.default_posttitle_postitem)
            val postimage : ImageView = itemView.findViewById(R.id.default_postlist_image)
            val postcontent : TextView = itemView.findViewById(R.id.default_posttext_postitem)
        }

        override fun onBindViewHolder(holder: otherPostViewHolder, position: Int) {
            holder.postname.text=post[position].title.toString()
            holder.postcontent.text=post[position].content.toString()
            val tempimageRef=storage.getReference().child(post[position].imageUri)
            tempimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                holder.postimage.setImageBitmap(bmp)
            }
        }

        override fun getItemCount(): Int {
            return post.size
        }
    }


    companion object {
    }
}