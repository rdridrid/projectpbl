package com.example.projectpbl

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.IResultReceiver.Default
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpbl.databinding.ActivityFindpasswordBinding
import com.example.projectpbl.databinding.ActivityOtherProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Value

class OtherProfileActivity : AppCompatActivity() {
    // 다른 사람 프로필 ( 친구 리스트에서 친구 이름 선택 시 상대 프로필로 이동 )
    // 또는 포스팅에서 상대 프로필 사진 클릭 시 이동
    lateinit var otherusername : String
    lateinit var otheruseremail: String
    private lateinit var defaultdatabase: DatabaseReference
    private lateinit var defaultstorage: FirebaseStorage
    private lateinit var defaulttempimageuri: String
    private lateinit var defaulttemppostowneruseruid:String
    private var post: ArrayList<PostModel> = arrayListOf()
    lateinit var passuid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val otheruserprofile = intent.getStringExtra("uid")
        val auth= Firebase.auth
        val database=Firebase.database
        val storage = FirebaseStorage.getInstance()
        val storageRef=storage.getReference()
        val uid=intent.getStringExtra("uid")//
        val passuid=uid
        val myuid=Firebase.auth.currentUser!!.uid
        val photo=binding.otherprofileImage //!!는 nullable이 어차피 허용안된다는 뜻인가?
        val itemsRef = database.getReference("Users").child(uid!!) //uid는 친구 uid
        val myfriendlistRef=database.getReference("Friends").child(myuid)
        val otherUsername=binding.otherprofileUsername
        val otherUserstatusMessage=binding.otherprofileStatusmsg
        binding.alreadyfriendMessage.visibility=View.GONE
        itemsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(child in snapshot.children){
                    if(child.key=="userName"){
                        otherusername=child.value.toString()
                        otherUsername.text=otherusername
                    }
                    if(child.key=="userProfileImageUri"){
                        val testtemp=child.value.toString()
                        val profileimageRef=storageRef.child(testtemp)
                        profileimageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                            val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                            photo.setImageBitmap(bmp)
                        }?.addOnFailureListener { 
                            println("실패")
                        }
                    }
                    if(child.key=="email"){
                        otheruseremail=child.value.toString()
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("실패")
            }
        })
        myfriendlistRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    //data.key가 친구 uid
                    if(uid==data.key){//하나라도 만족한다->친구관계이다. (물론 여기서 친구는 일방적인 팔로우를 의미)
                        binding.otherprofileAdd.visibility=View.GONE
                        binding.otherprofileAdd.isEnabled=false//혹시몰라서 비활성화까지 했음
                        binding.alreadyfriendMessage.visibility=View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        binding.alreadyfriendMessage.setOnClickListener {
            val dialog=DeleteFriendDialog()
            dialog.isCancelable=true
            val transaction = supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("uid",passuid)
            dialog.arguments=bundle
            dialog.show(supportFragmentManager,"")
            binding.otherprofileAdd.visibility=View.VISIBLE
            binding.otherprofileAdd.isEnabled=true//혹시몰라서 비활성화까지 했음
            binding.alreadyfriendMessage.visibility=View.GONE

        }


        val addfriendbtn = binding.otherprofileAdd
        addfriendbtn.setOnClickListener {//push아이디 자동생성
            myfriendlistRef.child(uid).setValue(uid) //child(uid)친구의 uid
            myfriendlistRef.child(uid).child("userName").setValue(otherusername)
            myfriendlistRef.child(uid).child("email").setValue(otheruseremail)
            myfriendlistRef.child(uid).child("profileimage").setValue(uid+"profileimage")
            Toast.makeText(this@OtherProfileActivity, "친구추가 성공.", Toast.LENGTH_SHORT).show()
        }
        /*inner class DefaultRecyclerPostAdapter:DefaultRecyclerPostAdapter<DefaultRecyclerPostAdapter.DefaultPostViewHolder>(){
            init{
                val defaultdatabase=Firebase.database.getReference()
                val defaultPostRef=defaultdatabase.child("Posts")
                val defaultUserRef =defaultdatabase.child("Users")
                val defaulttempppostownerusername:String=""
                defaultPostRef.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        println("실패")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        post.clear()
                        for (data in snapshot.children){
                            val tempposttitle=data.child("Posttitle").getValue().toString() //Posts/posturi == post의 id와 동일함
                            defaulttemppostowneruseruid=data.child("uid").getValue().toString()//포스트 주인 uid
                            val temptime=data.child("time").getValue().toString() //시간
                            val tempcontent=data.child("Postcontent").getValue().toString()  //본문
                            val tempowneremail=data.child("useremail").getValue().toString() //이메일주소
                            val tempownerusername=data.child("username").getValue().toString() //만든사람 이름
                            defaulttempimageuri=data.child("postfileImageUri").getValue().toString() //포스트 사진 주소
                            val tempownerprofileimage=data.child("profileimage").getValue().toString()
                            val temppost = PostModel(tempposttitle, tempcontent, tempownerusername, temptime,tempowneremail,defaulttempimageuri,defaulttemppostowneruseruid,tempownerprofileimage)
                            if(uid==defaulttemppostowneruseruid) {
                                post.add(temppost!!)
                            }
                        }
                        notifyDataSetChanged()
                    }
                })
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):DefaultPostViewHolder{
                return Defau
            }
            inner class DefaultPostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

            }
        }*/
    }
}