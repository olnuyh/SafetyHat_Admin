package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityAreaBinding
import com.example.admin.databinding.FragmentAreaRegistrationBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray


class AreaActivity : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle

    class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity){
        val fragments : List<Fragment>
        init {
            fragments = listOf(WorkersFragment(), AreaRegistrationFragment())
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
        setSupportActionBar(binding.toolBar)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle.syncState()

        binding.mainDrawerView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuSos -> {
                    val intent = Intent(this, SosActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCctv -> {
                    val intent = Intent(this, CctvActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuArea -> {
                    val intent = Intent(this, AreaActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCalendar -> {
                    val intent = Intent(this, ReadCalendarActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuNotification -> {
                    val intent = Intent(this, ReadNotificationActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuSalary -> {
                    val intent = Intent(this, SalaryActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        binding.logout.setOnClickListener {
            MyApplication.prefs.clear()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.areaViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.areaViewPager.adapter = FragmentAdapter(this)

        val tabTitles = listOf<String>("근무자", "작업등록") //탭에 보여질 이름

        TabLayoutMediator(binding.areaTab, binding.areaViewPager){
                tab, position -> tab.text = tabTitles[position]
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true

        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.home,menu)
        return true
    }
}