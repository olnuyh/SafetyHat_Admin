package com.example.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.DialogRegisterAreaBinding
import com.example.admin.databinding.DialogRegisterWorkBinding
import com.example.admin.databinding.FragmentAreaRegistrationBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


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
    lateinit var area : ArrayList<String>
    lateinit var areaSelected : String
    lateinit var binding : FragmentAreaRegistrationBinding
    lateinit var spinnerAdapter : ArrayAdapter<String>

    fun readArea(){
        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                area = arrayListOf<String>()
                for (i in 0 until response.length()){
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name")
                    area.add(name)
                }
                area.add("")

                spinnerAdapter = object: ArrayAdapter<String>(
                    areaActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    area
                ){
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view: TextView = super.getView(
                            position,
                            convertView,
                            parent
                        ) as TextView

                        view.textAlignment = View.TEXT_ALIGNMENT_CENTER

                        return view
                    }

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

                    override fun getCount(): Int {
                        return super.getCount() - 1
                    }
                }

                binding.areaSpinner.adapter = spinnerAdapter
                binding.areaSpinner.setSelection(spinnerAdapter.count)
            },
            Response.ErrorListener { error ->
                Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(areaActivity)
        queue.add(readAreaRequest)
    }

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

    override fun onResume() {
        readArea()
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAreaRegistrationBinding.inflate(layoutInflater, container, false)

        binding.readWorkersRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
        val adapter = AreaAdapter(areaActivity, MyApplication.workers)
        binding.readWorkersRecyclerView.adapter = adapter
        binding.readWorkersRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))

        readArea()

        binding.areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                areaSelected = area[p2]
            }
        }

        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            adapter.filter(searchText)
        }

        binding.registerWorkBtn.setOnClickListener {
            val startHour = binding.startHour.text.toString()
            val startMinutes = binding.startMinutes.text.toString()
            val endHour = binding.endHour.text.toString()
            val endMinutes = binding.endMinutes.text.toString()

            if(areaSelected.equals("")){
                Toast.makeText(areaActivity, "구역을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(startHour.equals("") || startMinutes.equals("")){
                Toast.makeText(areaActivity, "시작 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(endHour.equals("") || endMinutes.equals("")){
                Toast.makeText(areaActivity, "종료 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val start = SimpleDateFormat("H:mm").parse(startHour+":"+ startMinutes)
                val end = SimpleDateFormat("H:mm").parse(endHour+":"+ endMinutes)

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

                            MyApplication.workerList[i].isChecked = false
                        }
                    }

                    if(resultArray.length() == 0){
                        Toast.makeText(areaActivity, "근무자를 선택해주세요.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        val dialogBinding = DialogRegisterWorkBinding.inflate(layoutInflater)

                        val dialog = AlertDialog.Builder(areaActivity).run{
                            setView(dialogBinding.root)
                            setCancelable(true)
                            show()
                        }

                        dialog.window!!.setLayout(850, WindowManager.LayoutParams.WRAP_CONTENT)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        dialogBinding.cancelButton.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialogBinding.registerWorkBtn.setOnClickListener {
                            val areaRegistrationRequest = object : StringRequest(
                                Request.Method.POST,
                                "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/register_work.php",
                                Response.Listener<String>{ response ->
                                    val readWorkersRequest = JsonArrayRequest( // Volley를 이용한 http 통신
                                        Request.Method.POST,
                                        "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/read_workers.php",
                                        null,
                                        Response.Listener<JSONArray> { response ->
                                            binding.readWorkersRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
                                            binding.readWorkersRecyclerView.adapter = AreaAdapter(areaActivity, response)
                                            binding.readWorkersRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))
                                        },
                                        Response.ErrorListener { error ->
                                            Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    )

                                    val queue = Volley.newRequestQueue(areaActivity)
                                    queue.add(readWorkersRequest)

                                    binding.areaSpinner.setSelection(spinnerAdapter.count)
                                    binding.startHour.text = null
                                    binding.startMinutes.text = null
                                    binding.endHour.text = null
                                    binding.endMinutes.text = null
                                    Toast.makeText(areaActivity, "작업이 등록되었습니다.", Toast.LENGTH_LONG).show()
                                    dialog.dismiss()
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
                }

        }
        }

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