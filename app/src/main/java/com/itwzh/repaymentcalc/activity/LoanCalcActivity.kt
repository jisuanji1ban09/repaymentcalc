package com.itwzh.repaymentcalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.itwzh.repaymentcalc.databinding.ActivityLoanCalcBinding
import com.itwzh.repaymentcalc.utlis.ToastUtils
import com.itwzh.repaymentcalc.utlis.getLoanResult

class LoanCalcActivity : AppCompatActivity(), View.OnClickListener {

    private var loanAmount: Double = 0.0;
    private var loanTime: Int = 0;
    private var loanRate: Double = 0.0;
    private var isEP :Boolean = false;

    private val mBinding: ActivityLoanCalcBinding by lazy {
        ActivityLoanCalcBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.btnCalc.setOnClickListener(this)
        mBinding.btnShow.setOnClickListener(this)
        mBinding.clRepaymentDateChoose.setOnClickListener(this)
        mBinding.checkboxEPAI.setOnClickListener(this)
        mBinding.checkboxEP.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnCalc.id -> calc()
            mBinding.btnShow.id -> showRepaymentPlan()
            mBinding.clRepaymentDateChoose.id -> showDateChoose()
            mBinding.checkboxEPAI.id -> resetCheckBox(false)
            mBinding.checkboxEP.id -> resetCheckBox(true)
        }
    }

    private fun resetCheckBox(isEP: Boolean) {
        this.isEP = isEP
        mBinding.checkboxEPAI.isChecked = !isEP
        mBinding.checkboxEP.isChecked = isEP
    }

    fun calc() {
        getCalcParam()
        val loanResult = getLoanResult(loanAmount, loanTime, loanRate,isEP)
        mBinding.result = loanResult
    }

    fun showRepaymentPlan() {

    }

    fun showDateChoose() {

    }

    fun getCalcParam() {
        loanAmount =
            if (TextUtils.isEmpty(mBinding.editLoadAmount.text.toString())) 0.0 else mBinding.editLoadAmount.text.toString()
                .toDouble();
        loanTime =
            if (TextUtils.isEmpty(mBinding.editLoanTime.text.toString())) 0 else mBinding.editLoanTime.text.toString()
                .toInt();
        loanRate =
            if (TextUtils.isEmpty(mBinding.editLoadRate.text.toString())) 0.0 else mBinding.editLoadRate.text.toString()
                .toDouble();
        if (loanAmount == 0.0) return ToastUtils.toast(this, "贷款金额错误")

        if (loanTime == 0) return ToastUtils.toast(this, "贷款时长错误")

        if (loanRate == 0.0) return ToastUtils.toast(this, "贷款利率错误")

    }


}