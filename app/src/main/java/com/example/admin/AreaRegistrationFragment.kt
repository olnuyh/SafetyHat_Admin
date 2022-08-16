package com.example.admin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.FragmentAreaRegistrationBinding
import com.example.admin.databinding.FragmentWorkersBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AreaRegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AreaRegistrationFragment : Fragment() {
    lateinit var areaActivity: AreaActivity
    lateinit var jsonResponse : JSONArray
    lateinit var area : ArrayList<String>
    lateinit var areaSelected : String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 Activity로 형변환하여 할당
        areaActivity = context as AreaActivity
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentAreaRegistrationBinding.inflate(layoutInflater, container, false)

        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                area = arrayListOf<String>()
                area.add("구역선택")
                for (i in 0 until response.length()){
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name")
                    area.add(name)
                }

                val spinnerAdapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
                    areaActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    area
                ){
                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view: TextView = super.getDropDownView(
                            position,
                            convertView,
                            parent
                        ) as TextView

                        // set item text bold and sans serif font
                        //view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)

                        // spinner item text color
                        //view.setTextColor(Color.parseColor("#2E2D88"))

                        // set selected item style
                        //if (position == spinner.selectedItemPosition){
                            //view.background = ColorDrawable(Color.parseColor("#F5F5F5"))
                        //}


                        // spinner item text alignment center
                        view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        return view
                    }
                }

                binding.areaSpinner.adapter = spinnerAdapter
            },
            Response.ErrorListener { error ->
                Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue1 = Volley.newRequestQueue(areaActivity)
        queue1.add(readAreaRequest)

        binding.areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                areaSelected = area[p2]
            }
        }

        val readWorkersRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_workers.php",
            null,
            Response.Listener<JSONArray> { response ->
                jsonResponse = response
                binding.readWorkersRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
                binding.readWorkersRecyclerView.adapter = AreaAdapter(areaActivity, jsonResponse)
                binding.readWorkersRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))
            },
            Response.ErrorListener { error ->
                Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue2 = Volley.newRequestQueue(areaActivity)
        queue2.add(readWorkersRequest)

        binding.registerAreaBtn.setOnClickListener {
            val startHour = binding.startHour.text.toString()
            val startMinutes = binding.startMinutes.text.toString()
            val endHour = binding.endHour.text.toString()
            val endMinutes = binding.endMinutes.text.toString()

            if(areaSelected.equals("구역선택")){
                Toast.makeText(areaActivity, "구역을 선택해주세요.", Toast.LENGTH_SHORT).show()
                //Toast.makeText(areaActivity, areaSelected, Toast.LENGTH_SHORT).show()
            }
            else if(startHour.equals("") || startMinutes.equals("")){
                Toast.makeText(areaActivity, "시작 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(endHour.equals("") || endMinutes.equals("")){
                Toast.makeText(areaActivity, "종료 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val start = SimpleDateFormat("h:mm").parse(startHour+":"+ startMinutes)
                val end = SimpleDateFormat("h:mm").parse(endHour+":"+ endMinutes)

                Log.d("mobileApp", start.toString())

                if(start.after(end)){
                    Toast.makeText(areaActivity, "입력한 시간이 올바르지 않습니다. 다시 입력해주세요.", Toast.LENGTH_LONG).show()
                }
                else{
                    val resultArray = JSONArray()

                    for (i in 0 until MyApplication.workerList.size) {
                        if(MyApplication.workerList[i].isChecked){
                            val resultObj = JSONObject()
                            resultObj.put("work_area", areaSelected)
                            resultObj.put("admin_start",  SimpleDateFormat("H:mm").format(start).toString())
                            resultObj.put("admin_end",  SimpleDateFormat("H:mm").format(end).toString())
                            resultObj.put("worker_id", MyApplication.workerList[i].id)

                            resultArray.put(resultObj)
                        }
                    }

                    if(resultArray.length() == 0){
                        Toast.makeText(areaActivity, "근무자를 선택해주세요.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        val areaRegistrationRequest = object : StringRequest(
                            Request.Method.POST,
                            BuildConfig.API_KEY+"register_area.php",
                            Response.Listener<String>{ response ->
                                Toast.makeText(areaActivity, response.toString(), Toast.LENGTH_LONG).show()
                            },
                            Response.ErrorListener { error ->
                                Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                            }){
                            override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                                val params : MutableMap<String, String> = HashMap()
                                params["worker"] = resultArray.toString()
                                return params
                            }
                        }

                        val queue = Volley.newRequestQueue(areaActivity)
                        queue.add(areaRegistrationRequest)
                    }
                }
                /*

                val areaRegistrationRequest = object : StringRequest(
                    Request.Method.POST,
                    BuildConfig.API_KEY+"write_calendar.php",
                    Response.Listener<String>{ response ->
                        if(response.toString().equals("1")) { // 성공
                            Toast.makeText(this, "일정이 등록되었습니다.", Toast.LENGTH_LONG).show()
                            finish()
                            val intent = Intent(this, ReadCalendarActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                        val params : MutableMap<String, String> = HashMap()
                        params["startdate"] = startdate
                        params["enddate"] = enddate
                        params["contents"] = contents
                        params["writer"] = MyApplication.prefs.getString("admin_pkey", "")
                        return params
                    }
                }

                val queue = Volley.newRequestQueue(areaActivity)
                queue.add(areaRegistrationRequest)

            }
                 */
        } }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AreaRegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AreaRegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}