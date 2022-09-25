package com.example.admin

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ItemNotificationBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

class NotificationViewHolder(val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)
class NotificationAdapter(val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 선택 데이터 리스트
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()


    override fun getItemCount(): Int {
        return arr.length()?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as NotificationViewHolder).binding

        val notification = arr[position] as JSONObject
        binding.itemTitle.text = notification.getString("notification_title")
        binding.itemContent.text = notification.getString("notification_contents")
        binding.itemName.text="관리자 "+notification.getString("notification_writer")

        val insert_date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(notification.getString("insert_date"))
        val date = SimpleDateFormat("yyyy.MM.dd").format(insert_date)

        binding.itemDate.text=date

        binding.deleteImg.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_notification_delete)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val okBtn = dialog.findViewById<ImageButton>(R.id.okButton)
            val cancelBtn = dialog.findViewById<ImageButton>(R.id.cancelButton)

            okBtn.setOnClickListener {
                arr.remove(holder.position)
                notifyDataSetChanged()
                dialog.dismiss()

                val notificationTitle = binding.itemTitle.text.toString()
                val deleteNotificationRequest = object : StringRequest( // Volley를 이용한 http 통신
                    Request.Method.POST,
                    BuildConfig.API_KEY + "delete_notification.php",
                    Response.Listener<String> { response ->
                        if (response.toString().equals("1")) { // 공지사항 삭제 성공

                        }
                    },
                    Response.ErrorListener { error ->
                        //Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    }) {
                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                        val params: MutableMap<String, String> = HashMap()
                        params["title"] = notificationTitle
                        params["writer"] = MyApplication.prefs.getString("admin_pkey", "")
                        return params
                    }
                }
                val queue2 = Volley.newRequestQueue(context)
                queue2.add(deleteNotificationRequest)

            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }


        binding.ivItemUpImg.setOnClickListener {
            if (selectedItems.get(position)) {
                // VISIBLE -> INVISIBLE
                selectedItems.delete(position)

                binding.clItemExpand.visibility = View.GONE
            }
            binding.ivItemUpImg.visibility=View.GONE
            binding.ivItemDownImg.visibility=View.VISIBLE
        }

        binding.ivItemDownImg.setOnClickListener {

            if (selectedItems.get(position)) {

            } else {
                // INVISIBLE -> VISIBLE
                selectedItems.put(position, true)

                binding.clItemExpand.visibility = View.VISIBLE
            }
            binding.ivItemDownImg.visibility=View.GONE
            binding.ivItemUpImg.visibility=View.VISIBLE
        }


    }

}
