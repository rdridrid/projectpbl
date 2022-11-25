package com.example.projectpbl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpbl.databinding.ActivityPostinginfoBinding

class MyPagerAdapter (private val viewModel: snsViewModel) : RecyclerView.Adapter<MyPagerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ActivityPostinginfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun pagePosting(userpaage: Int) {
                binding.profileImg
                binding.userName
                binding.postingDate
                binding.postingImg
                binding.postingWriting
            }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ActivityPostinginfoBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return viewModel.nowuser
    }
}