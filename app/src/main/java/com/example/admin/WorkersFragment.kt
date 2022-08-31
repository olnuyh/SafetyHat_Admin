package com.example.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.DialogAreaDetailBinding
import com.example.admin.databinding.DialogRegisterAreaBinding
import com.example.admin.databinding.FragmentWorkersBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkersFragment : Fragment(){
    lateinit var areaActivity: AreaActivity
    lateinit var binding : FragmentWorkersBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkersBinding.inflate(inflater, container, false)

        binding.areaWorkersRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
        val adapter = WorkersAdapter(areaActivity, MyApplication.workers)
        binding.areaWorkersRecyclerView.adapter = adapter
        binding.areaWorkersRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))

        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            adapter.filter(searchText)
            binding.searchText.text = null
        }

        val spinnerAdapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
            areaActivity,
            android.R.layout.simple_spinner_dropdown_item,
            MyApplication.areaList
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

        binding.areaFilterSpinner.adapter = spinnerAdapter
        binding.areaFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == MyApplication.areaList.size - 1){
                    val queue = Volley.newRequestQueue(areaActivity)

                    val binding2 = DialogRegisterAreaBinding.inflate(layoutInflater)
                    AlertDialog.Builder(areaActivity).run {
                        setTitle("추가할 구역 입력")
                        setView(binding2.root)
                        setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                            if(binding2.dialogEditArea.text.trim().toString() == ""){
                                Toast.makeText(areaActivity, "추가하려는 구역을 입력해주세요.", Toast.LENGTH_LONG).show()
                            }
                            else{
                                val registerAreaRequest = object : StringRequest(
                                    Request.Method.POST,
                                    BuildConfig.API_KEY+"register_area.php",
                                    Response.Listener<String>{ response ->
                                        if(response.toString().equals("-1")){ // 추가하려는 구역이 이미 존재하는 경우
                                            Toast.makeText(areaActivity, "이미 존재하는 구역입니다.", Toast.LENGTH_LONG).show()
                                        }
                                        else if(response.equals("1")){ // 정상 추가 성공
                                            val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
                                                Method.GET,
                                                BuildConfig.API_KEY + "read_area_list.php",
                                                null,
                                                Response.Listener<JSONArray> { response ->
                                                    MyApplication.areaList.clear()
                                                    MyApplication.areaList.add("")
                                                    MyApplication.areaList.add("-")
                                                    for (i in 0 until response.length()){
                                                        val obj = response[i] as JSONObject
                                                        val name = obj.getString("area_name")
                                                        MyApplication.areaList.add(name)
                                                    }
                                                    MyApplication.areaList.add("+ 추가")
                                                },
                                                Response.ErrorListener { error ->
                                                    Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                                                }
                                            )

                                            queue.add(readAreaRequest)
                                            Toast.makeText(areaActivity, "구역이 추가되었습니다.", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    Response.ErrorListener { error ->
                                        Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                                    }){
                                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                                        val params : MutableMap<String, String> = HashMap()
                                        params["area"] = binding2.dialogEditArea.text.toString()
                                        return params
                                    }
                                }

                                queue.add(registerAreaRequest)
                            }
                        })
                        show()
                    }
                    binding.areaFilterSpinner.setSelection(0)

                }else{
                    adapter.spinnerFilter(MyApplication.areaList[p2])
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
         * @return A new instance of fragment WorkersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WorkersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}