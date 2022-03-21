package com.itwzh.repaymentcalc.widget

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.listener.JumpListener
import com.itwzh.repaymentcalc.utlis.jumpIssue

class JumpDialog(context: Activity, count:Int, state:Int, @NonNull listener: JumpListener) :
    Dialog(context) {
    private val mContext: Activity = context
    private val count: Int = count
    private val state: Int = state
    private val listener: JumpListener = listener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_jump)
        val llTop = findViewById<LinearLayout>(R.id.llTop)
        val llBottom = findViewById<LinearLayout>(R.id.llBottom)
        val llIssue = findViewById<LinearLayout>(R.id.llIssue)
        when(state){
            1 -> {
                llTop.visibility = View.VISIBLE
                llBottom.visibility = View.VISIBLE
            }
            2 -> {
                llTop.visibility = View.GONE
                llBottom.visibility = View.VISIBLE
            }
            3 ->{
                llTop.visibility = View.VISIBLE
                llBottom.visibility = View.GONE
            }
        }
        llTop.setOnClickListener {
            listener.jump(0)
            dismiss()
        }
        llBottom.setOnClickListener {
            listener.jump(-1)
            dismiss()
        }
        llIssue.setOnClickListener {
            jumpIssue(mContext,count,listener).show()
            dismiss()
        }
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        val params = this.window!!.attributes
        params.gravity = Gravity.CENTER
        this.window!!.attributes = params
    }

    override fun show() {
        super.show()
    }
}