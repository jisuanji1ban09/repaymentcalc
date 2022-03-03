package com.itwzh.repaymentcalc.binding.listener

import android.content.Context
import android.content.Intent
import android.view.View
import com.itwzh.repaymentcalc.activity.LoanCalcActivity
import com.itwzh.repaymentcalc.activity.RepaymentCalcActivity

class MainListener {

    fun toLoanCalc(view:View){
        val context = view.context;
        val intent:Intent = Intent(context,LoanCalcActivity().javaClass)
        context.startActivity(intent)
    }

    fun toRepaymentCalc(view:View){
        val context = view.context;
        val intent:Intent = Intent(context, RepaymentCalcActivity().javaClass)
        context.startActivity(intent)
    }

}