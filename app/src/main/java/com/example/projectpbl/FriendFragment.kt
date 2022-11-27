package com.example.projectpbl

import android.content.Context
import android.content.Intent
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendFragment : Fragment() {
    companion object {
    }
    private lateinit var database: DatabaseReference
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
            val friendsRef = database.child("Friends").child("uid") //나중에 본인 uid기입
            friendsRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error:DatabaseError){
                    println("실패")
                }
                override fun onDataChange(snapshot: DataSnapshot){
                    friend.clear()
                    for (data in snapshot.children){ //Friends의 myuid의 친구 uid를 참조한다.

                        val myfrienduid = data.getValue().toString() // 내 친구 uid를얻는다
                        println(myfrienduid)
                        var tempfriend= FriendData(myfrienduid,"none")
                        println(tempfriend.username)
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
        }
        override fun onBindViewHolder(holder: FriendViewHolder,position: Int){
            //데이터 가져옴

            holder.usernameitem.text=friend[position].username.toString()
            }
        override fun getItemCount(): Int{
            return friend.size
        }
    }
}
