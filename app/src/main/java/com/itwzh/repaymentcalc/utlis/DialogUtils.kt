package com.itwzh.repaymentcalc.utlis

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AlertDialog
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.listener.JumpListener


fun jumpIssue(mContext: Activity, count: Int, listener: JumpListener) :AlertDialog{
    var MessageDialog : Builder = Builder(mContext);
    val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = layoutInflater.inflate(R.layout.dialog_edittext, null) as LinearLayout
    val input1 = view.findViewById<View>(R.id.edittext1) as EditText
    input1.hint = "请输入1到${count}"
    MessageDialog.setView(view)
    MessageDialog.setTitle("期数跳转")
    MessageDialog.setPositiveButton("确定",
        DialogInterface.OnClickListener { arg0, arg1 ->
            val issue = input1.text.toString()
            listener.jump(issue.toInt())
            arg0.dismiss()
        })
    return MessageDialog.create()
}
