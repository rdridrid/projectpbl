package com.example.projectpbl

import android.content.Context
import android.content.Intent
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FriendFragment : Fragment() {
    companion object {
        private lateinit var newdata : Intent
    }
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var otherprofileimageUri: String
    private lateinit var tempfriend : FriendData
    private var friend : ArrayList<FriendData> = arrayListOf()
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
        database= Firebase.database.reference
        storage=FirebaseStorage.getInstance()
        val view = inflater.inflate(R.layout.fragment_friend,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.lstUser)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter=RecyclerFriendAdapter()
        return view
    }
    inner class RecyclerFriendAdapter : RecyclerView.Adapter<RecyclerFriendAdapter.FriendViewHolder>(){
        init{
            val myUid = Firebase.auth.currentUser!!.uid //toString을 넣을 수도있음
            val database = Firebase.database.getReference()
            val friendsRef = database.child("Friends").child(myUid) //나중에 본인 uid기입

            friendsRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error:DatabaseError){
                    println("실패")
                }
                override fun onDataChange(snapshot: DataSnapshot){
                    friend.clear()
                    for (data in snapshot.children){ //Friends의 myuid의 친구 uid를 참조한다
                        val myfrienduid = data.key.toString() // 내 친구 uid를얻는다
                        val myfriendtempname=data.child("userName").value.toString()
                        val myfriendtempemail=data.child("email").value.toString()
                        var myfriendtempprofileimageUri=data.child("profileimage").value.toString()
                        if(myfriendtempprofileimageUri=="null"){
                            tempfriend= FriendData(myfriendtempname,myfriendtempemail,"default.png",myfrienduid)
                        }
                        //println(data.child("userName").value) 나중에 참고용
                        else {
                            tempfriend = FriendData(myfriendtempname, myfriendtempemail, myfriendtempprofileimageUri,myfrienduid)
                        }
                        friend.add(tempfriend!!)
                    }
                    notifyDataSetChanged()
                }
            })



        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder{
            return FriendViewHolder(LayoutInflater.from(context).inflate(R.layout.friendlist_item, parent, false))
        }
        inner class FriendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val usernameitem: TextView = itemView.findViewById(R.id.username_item)
            val userprofileimage: ImageView = itemView.findViewById(R.id.profileimage_item)
            val useremailitem: TextView = itemView.findViewById(R.id.email_item)
        }
        override fun onBindViewHolder(holder: FriendViewHolder,position: Int){
            //데이터 가져옴

            holder.usernameitem.text=friend[position].username
            holder.useremailitem.text=friend[position].useremail


            val tempimageRef=storage.getReference().child(friend[position].imgUri)
            tempimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                holder.userprofileimage.setImageBitmap(bmp)
                }
            holder.userprofileimage.setOnClickListener {
                //println(post[position].name)
                //val uid=post[position].uid.toString() owneruid
                //startActivity()
                newdata = Intent(activity, OtherProfileActivity::class.java)
                newdata.putExtra("uid", friend[position].uid) //여기 uid는 친구 uid
                startActivity(newdata)
                }
            }

        override fun getItemCount(): Int{
            return friend.size
        }
    }
}
