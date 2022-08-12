package com.example.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.admin.databinding.ActivityAreaBinding
import com.example.admin.databinding.FragmentAreaRegistrationBinding
import com.google.android.material.tabs.TabLayoutMediator


class AreaActivity : AppCompatActivity() {
    class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity){
        val fragments : List<Fragment>
        init {
            fragments = listOf(WorkersFragment(), AreaRegistrationFragment(), AreaCheckFragment())
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.areaViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.areaViewPager.adapter = FragmentAdapter(this)

        val tabTitles = listOf<String>("근무자", "구역등록", "구역확인") //탭에 보여질 이름

        TabLayoutMediator(binding.areaTab, binding.areaViewPager){
                tab, position -> tab.text = tabTitles[position]
        }.attach()
    }
}