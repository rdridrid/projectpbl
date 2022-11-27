package com.example.projectpbl

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemHolderInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Value

class HomeFragment : Fragment() {
    companion object{
        private lateinit var newdata : Intent
    }
    private lateinit var database:DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var tempimageuri: String
    private lateinit var temppostowneruseruid:String
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
        storage=FirebaseStorage.getInstance()

        val view = inflater.inflate(R.layout.fragment_home,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.lstPost)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        recyclerView.adapter=RecyclerPostAdapter()
        return view
    }
    inner class RecyclerPostAdapter: RecyclerView.Adapter<RecyclerPostAdapter.PostViewHolder>(){
        init{val database = Firebase.database.getReference()
            val PostsRef = database.child("Posts")
            val UserRef = database.child("Users")
            var temppostownerusername: String =""

            PostsRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    println("실패")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    post.clear()
                    for (data in snapshot.children){
                        val tempposttitle=data.child("Posttitle").getValue().toString() //Posts/posturi == post의 id와 동일함
                        temppostowneruseruid=data.child("uid").getValue().toString()//포스트 주인
                        val temptime=data.child("time").getValue().toString() //시간
                        val tempcontent=data.child("Postcontent").getValue().toString()  //본문
                        val tempowneremail=data.child("useremail").getValue().toString() //이메일주소
                        val tempownerusername=data.child("username").getValue().toString() //만든사람 이름
                        tempimageuri=data.child("postfileImageUri").getValue().toString() //포스트 사진 주소
                        val tempownerprofileimage=data.child("profileimage").getValue().toString()
                        val temppost = PostModel(tempposttitle, tempcontent, tempownerusername, temptime,tempowneremail,tempimageuri,temppostowneruseruid,tempownerprofileimage)
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
            val postimage : ImageView = itemView.findViewById(R.id.postlist_image)
            val postcontent : TextView = itemView.findViewById(R.id.posttext_postitem)
            val username : TextView = itemView.findViewById(R.id.username_postitem)
            val useremail : TextView = itemView.findViewById(R.id.email_postitem)
            val userprofileimage : ImageView=itemView.findViewById(R.id.profileimage_postitem)

        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.postname.text=post[position].title.toString()
            holder.postcontent.text=post[position].content.toString()
            val tempimageRef=storage.getReference().child(post[position].imageUri)
            tempimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp= BitmapFactory.decodeByteArray(it,0,it.size)
                holder.postimage.setImageBitmap(bmp)
            }
            holder.username.text=post[position].name.toString()
            holder.useremail.text=post[position].email.toString()
            holder.userprofileimage.setOnClickListener {
                //println(post[position].name)
                //val uid=post[position].uid.toString() owneruid
                //startActivity()
                newdata = Intent(activity, OtherProfileActivity::class.java)
                newdata.putExtra("uid", post[position].useruid)
                startActivity(newdata)
            }
            val tempprofileimageRef=storage.getReference().child(post[position].profileimageUri)
            tempprofileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                holder. userprofileimage.setImageBitmap(bmp)
            }

        }
        override fun getItemCount(): Int {
            return post.size
        }
    }
}