package com.example.projectpbl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.projectpbl.databinding.ActivityHomeBinding
import com.google.firebase.ktx.Firebase


private const val TAG_HOME = "home_fragment"
private const val TAG_FRIEND = "friend_fragment"
private const val TAG_MY_PROFILE = "my_profile_fragment"
private const val TAG_POST = "post_fragment"
private const val TAG_EDIT_MY_PROFILE="my_profile_edit_fragment"
class HomeActivity : AppCompatActivity(){
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME,HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.homeFragment -> setFragment(TAG_HOME,HomeFragment())
                R.id.friendFragment -> setFragment(TAG_FRIEND,FriendFragment())
                R.id.profileFragment -> setFragment(TAG_MY_PROFILE,MyProfileFragment())
                R.id.postFragment -> startActivity(Intent(this, UploadPostActivity::class.java))
                R.id.postFragment -> setFragment(TAG_HOME, HomeFragment())
            }
            true
        }
    }
    private fun setFragment(tag: String, fragment: Fragment){
        val manager: FragmentManager=supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if(manager.findFragmentByTag(tag)==null){
            fragTransaction.add(R.id.mainFrameLayout,fragment,tag)
        }
        val home = manager.findFragmentByTag(TAG_HOME)
        val friend = manager.findFragmentByTag(TAG_FRIEND)
        val profile = manager.findFragmentByTag(TAG_MY_PROFILE)
        val post = manager.findFragmentByTag(TAG_POST)
        val editprofile = manager.findFragmentByTag(TAG_EDIT_MY_PROFILE)


        if(home != null){
            fragTransaction.hide(home)
        }
        if(friend != null){
            fragTransaction.hide(friend)
        }
        if(profile != null){
            fragTransaction.hide(profile)
            if(editprofile!=null) {
                fragTransaction.hide(editprofile)
            }

        }
        if(post != null){
            fragTransaction.hide(post)
        }
        if(tag==TAG_HOME){
            if(home!=null) {
                fragTransaction.show(home)
            }
        }
        else if(tag== TAG_FRIEND){
            if(friend!=null){
                fragTransaction.show(friend)
            }
        }
        else if(tag == TAG_MY_PROFILE){
            if(profile!=null){
                fragTransaction.show(profile)
            }
        }
        else if(tag== TAG_POST){
            if(post!=null){
                fragTransaction.show(post)
            }
        }
        fragTransaction.commitAllowingStateLoss()
    }
    infix fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .commit()
    }

    infix fun changeFragmentWithBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)

            .commit()
    }

    infix fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
    }
}