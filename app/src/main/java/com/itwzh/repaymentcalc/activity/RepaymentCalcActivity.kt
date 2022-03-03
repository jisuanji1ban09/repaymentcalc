package com.itwzh.repaymentcalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.itwzh.repaymentcalc.databinding.ActivityRepaymentCalcBinding
import com.itwzh.repaymentcalc.utlis.getRepaymentPlan

class RepaymentCalcActivity : AppCompatActivity() {

    private val mBinding:ActivityRepaymentCalcBinding by lazy {
        ActivityRepaymentCalcBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        val repaymentPlan = getRepaymentPlan(450000.0, 240, 5.635, "2022-03-02", false);
        for (i in 0..repaymentPlan.size-1)
            Log.i("wzh",repaymentPlan[i].toString())
    }
}