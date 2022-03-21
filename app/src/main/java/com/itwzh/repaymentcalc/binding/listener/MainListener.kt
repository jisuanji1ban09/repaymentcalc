package com.itwzh.repaymentcalc.binding.listener

import android.content.Intent
import android.view.View
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.activity.AdvanceRepaymentPlanActivity
import com.itwzh.repaymentcalc.activity.LoanCalcActivity
import com.itwzh.repaymentcalc.activity.AdvanceRepaymentCalcActivity
import com.itwzh.repaymentcalc.activity.FullAmountRepaymentCalcActivity

class MainListener {

    fun toLoanCalc(view:View){
        val context = view.context;
        val intent:Intent = Intent(context,LoanCalcActivity().javaClass)
        context.startActivity(intent)
    }

    fun toRepaymentCalc(view:View){
        val context = view.context;
        val intent:Intent = Intent(context, AdvanceRepaymentCalcActivity().javaClass)
        context.startActivity(intent)
    }

    fun toFullRepaymentCalc(view:View){
        val context = view.context;
        val intent:Intent = Intent(context, FullAmountRepaymentCalcActivity().javaClass)
        context.startActivity(intent)
    }
    
    fun toRepaymentPlan(view: View){
        val context = view.context;
        val intent:Intent = Intent(context, AdvanceRepaymentPlanActivity().javaClass)
        intent.putExtra(CommonValues.amount,450000.0)
        intent.putExtra(CommonValues.rate,5.635)
        intent.putExtra(CommonValues.months,240)
        intent.putExtra(CommonValues.isEP,true)
        intent.putExtra(CommonValues.date,"2021年02月01日")
        intent.putExtra(CommonValues.repaymentAmount,50000.0)
        intent.putExtra(CommonValues.isAdvance,false)
        intent.putExtra(CommonValues.repaymentDate,"2022年04月01日")
        context.startActivity(intent)
    }

}