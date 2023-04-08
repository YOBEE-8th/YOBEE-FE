package com.ssafy.yobee.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ViewPagerBannerItemBinding

class BannerViewPagerAdapter :
    RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewPagerViewHolder>() {

    inner class BannerViewPagerViewHolder(private val binding: ViewPagerBannerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                when (position % 3) {
                    0 -> ivBannerImage.setImageResource(R.drawable.banner_1)
                    1 -> ivBannerImage.setImageResource(R.drawable.banner_2)
                    2 -> ivBannerImage.setImageResource(R.drawable.banner_3)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewPagerViewHolder {
        return BannerViewPagerViewHolder(
            ViewPagerBannerItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BannerViewPagerViewHolder, position: Int) {
        holder.apply {
            bind(position)
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}