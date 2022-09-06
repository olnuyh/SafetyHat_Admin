package com.example.admin

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton

class DeleteNotificationDialog (context: Context) {
    private val dialog = Dialog(context)

    fun showDia(){
        dialog.setContentView(R.layout.dialog_notification_delete)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val okBtn = dialog.findViewById<ImageButton>(R.id.okButton)
        val cancelBtn = dialog.findViewById<ImageButton>(R.id.cancelButton)

        okBtn.setOnClickListener {

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}