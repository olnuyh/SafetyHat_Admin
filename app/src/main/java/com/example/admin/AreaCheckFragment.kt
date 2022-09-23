package com.example.admin

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.DialogAreaDetailBinding
import com.example.admin.databinding.DialogRegisterAreaBinding
import com.example.admin.databinding.FragmentAreaCheckBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AreaCheckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AreaCheckFragment : Fragment() {
    lateinit var areaActivity: AreaActivity
    lateinit var area : ArrayList<String>

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
        val binding = FragmentAreaCheckBinding.inflate(layoutInflater, container, false)

        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                area = arrayListOf<String>()
                for (i in 0 until response.length()){
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name")
                    area.add(name)
                }

                binding.areaCheckRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
                val checkAdapter = AreaCheckAdapter(areaActivity, area){
                    val readWorkersInAreaRequest = object : StringRequest(
                        Request.Method.POST,
                        BuildConfig.API_KEY+"read_workers_in_area.php",
                        Response.Listener<String>{ response ->
                            val binding = DialogAreaDetailBinding.inflate(layoutInflater)
                            AlertDialog.Builder(areaActivity).run {
                                setView(binding.root)
                                binding.dialogRecyclerView.layoutManager = LinearLayoutManager(areaActivity)
                                binding.dialogRecyclerView.adapter = WorkersAdapter(areaActivity, JSONArray(response))
                                binding.dialogRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))
                                setPositiveButton("확인", null)
                                show()
                            }
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                            val params : MutableMap<String, String> = HashMap()
                            params["area"] = it
                            return params
                        }
                    }

                    val queue = Volley.newRequestQueue(areaActivity)
                    queue.add(readWorkersInAreaRequest)
                }

                binding.areaCheckRecyclerView.adapter = checkAdapter
                binding.areaCheckRecyclerView.addItemDecoration(DividerItemDecoration(areaActivity, LinearLayoutManager.VERTICAL))

            },
            Response.ErrorListener { error ->
                Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(areaActivity)
        queue.add(readAreaRequest)

        binding.registerAreaBtn.setOnClickListener {
            val binding = DialogRegisterAreaBinding.inflate(layoutInflater)
            AlertDialog.Builder(areaActivity).run {
                setView(binding.root)
                setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                    val registerAreaRequest = object : StringRequest(
                        Request.Method.POST,
                        BuildConfig.API_KEY+"register_area.php",
                        Response.Listener<String>{ response ->
                            if(response.toString().equals("-1")){ // 회원가입 실패
                                Toast.makeText(areaActivity, "이미 존재하는 구역입니다.", Toast.LENGTH_LONG).show()
                            }
                            else if(response.equals("1")){ // 회원가입 성공
                                queue.add(readAreaRequest)
                                Toast.makeText(areaActivity, "구역 등록되었습니다.", Toast.LENGTH_LONG).show()
                            }
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(areaActivity, error.toString(), Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                            val params : MutableMap<String, String> = HashMap()
                            params["area"] = binding.dialogEditArea.text.toString()
                            return params
                        }
                    }

                    val queue = Volley.newRequestQueue(areaActivity)
                    queue.add(registerAreaRequest)
                })
                show()
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
         * @return A new instance of fragment AreaCheckFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AreaCheckFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
