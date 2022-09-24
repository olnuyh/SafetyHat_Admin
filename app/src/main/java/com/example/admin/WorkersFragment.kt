package com.example.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.DialogDeleteAreaBinding
import com.example.admin.databinding.DialogRegisterAreaBinding
import com.example.admin.databinding.FragmentWorkersBinding
import org.json.JSONArray
import org.json.JSONObject


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
    lateinit var spinnerAdapter : ArrayAdapter<String>

    fun readArea(){
        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                MyApplication.areaList.clear()
                MyApplication.areaList.add("-")
                for (i in 0 until response.length()) {
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name")
                    MyApplication.areaList.add(name)
                }
                MyApplication.areaList.add("+ 구역 추가")
                MyApplication.areaList.add("구역을 선택하세요.")
                binding.areaFilterSpinner.setSelection(spinnerAdapter.count)
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
        binding.areaWorkersRecyclerView.addItemDecoration(
            DividerItemDecoration(
                areaActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            adapter.filter(searchText)
            binding.searchText.text = null
        }

        spinnerAdapter = object : ArrayAdapter<String>(
            areaActivity,
            R.layout.spinner_row,
            R.id.spnItemName,
            MyApplication.areaList
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return getCustomView(position, convertView, parent)
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getCustomView(position, convertView, parent)
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }

            fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = (context as Activity).layoutInflater
                val rowView = inflater.inflate(R.layout.spinner_row, parent, false)
                val spnItemName = rowView.findViewById<View>(R.id.spnItemName) as TextView
                val spnItemDel = rowView.findViewById<View>(R.id.spnItemDel) as TextView

                if (position == count) {
                    spnItemName.setText("")
                    spnItemName.hint = getItem(count)
                    spnItemDel.visibility = View.GONE
                } else if (position == 0 || position == count - 1) {
                    spnItemName.setText(MyApplication.areaList[position])
                    spnItemDel.visibility = View.GONE
                } else {
                    spnItemName.setText(MyApplication.areaList[position])
                    spnItemDel.setOnClickListener {
                        val queue = Volley.newRequestQueue(areaActivity)

                        val binding3 = DialogDeleteAreaBinding.inflate(layoutInflater)
                        binding3.dialogDeleteArea.setText(MyApplication.areaList[position] + "구역을")
                        AlertDialog.Builder(areaActivity).run {
                            setTitle("삭제할 구역 입력")
                            setView(binding3.root)
                            setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                                val deleteAreaRequest = object : StringRequest(
                                    Request.Method.POST,
                                    BuildConfig.API_KEY + "delete_area.php",
                                    Response.Listener<String> { response ->
                                        readArea()
                                        Toast.makeText(
                                            areaActivity,
                                            "구역이 삭제되었습니다.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    Response.ErrorListener { error ->
                                        Toast.makeText(
                                            areaActivity,
                                            error.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }) {
                                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                                        val params: MutableMap<String, String> = HashMap()
                                        params["area"] = MyApplication.areaList[position]
                                        return params
                                    }
                                }

                                queue.add(deleteAreaRequest)
                            })
                            setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                                binding.areaFilterSpinner.setSelection(spinnerAdapter.count)
                            })
                            setCancelable(false)
                            show()
                        }
                    }
                }
                return rowView
            }
        }

        binding.areaFilterSpinner.adapter = spinnerAdapter
        binding.areaFilterSpinner.setSelection(spinnerAdapter.count)
        binding.areaFilterSpinner.dropDownVerticalOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, resources.displayMetrics).toInt()
        binding.areaFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(MyApplication.areaList[p2].equals("+ 구역 추가")){
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
                                            readArea()
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
                        setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                            binding.areaFilterSpinner.setSelection(spinnerAdapter.count)
                        })
                        setCancelable(false)
                        show()
                    }
                }
                else if(p2 != spinnerAdapter.count){
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