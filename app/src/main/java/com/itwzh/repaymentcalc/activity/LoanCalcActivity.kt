package com.itwzh.repaymentcalc.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.databinding.ActivityLoanCalcBinding
import com.itwzh.repaymentcalc.utlis.*
import com.loper7.date_time_picker.dialog.CardDatePickerDialog

class LoanCalcActivity : AppCompatActivity(), View.OnClickListener {

    private var loanAmount: Double = 0.0
    private var loanTime: Int = 0
    private var loanRate: Double = 0.0
    private var isEP: Boolean = false
    private var firstDate: String = ""
    private var preClick: Long = 0
    private var firstCheckDate: Long = 0
    private val maxTime = getMaxMonthByChoose()
    private val minTime = getMinMonthByChoose()

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
        hideSoftKeyboard(this, mBinding.btnCalc)
        val calcParam = getCalcParam()
        if (calcParam) {
            val loanResult = getLoanResult(loanAmount, loanTime, loanRate, isEP, firstDate)
            if (loanResult.isEP) {
                mBinding.clRepaymentMonth.visibility = View.GONE
                mBinding.clRepaymentMonthFirst.visibility = View.VISIBLE
            } else {
                mBinding.clRepaymentMonth.visibility = View.VISIBLE
                mBinding.clRepaymentMonthFirst.visibility = View.GONE
            }
            mBinding.cardResult.visibility = View.VISIBLE
            mBinding.result = loanResult
            mBinding.btnShow.visibility = View.VISIBLE
        }
    }

    fun showRepaymentPlan() {
        hideSoftKeyboard(this, mBinding.btnCalc)
        val calcParam = getCalcParam()
        if (calcParam) {
            val intent: Intent = Intent(this, RepaymentPlanActivity().javaClass)
            intent.putExtra(CommonValues.amount, loanAmount)
            intent.putExtra(CommonValues.rate, loanRate)
            intent.putExtra(CommonValues.months, loanTime)
            intent.putExtra(CommonValues.isEP, isEP)
            intent.putExtra(CommonValues.date, firstDate)
            startActivity(intent)
        }
    }

    fun showDateChoose() {
        if (System.currentTimeMillis() - preClick < 1000) return
        preClick = System.currentTimeMillis()
        CardDatePickerDialog.builder(this)
            .setTitle("????????????")
            .showBackNow(false)
            .setMinTime(minTime)
            .setDefaultTime(firstCheckDate)
            .setMaxTime(maxTime)
            .showFocusDateInfo(false)
            .setPickerLayout(R.layout.layoyt_date_choose)
            .setLabelText("???", "???", "???")
            .setOnChoose("??????") { millisecond ->
                firstCheckDate = millisecond
                mBinding.tvRepaymentDateDesc.text = dateFormat(millisecond)
            }.setOnCancel("??????") {}.build().show()
    }

    fun getCalcParam(): Boolean {
        loanAmount =
            if (TextUtils.isEmpty(mBinding.editLoadAmount.text.toString())) 0.0 else mBinding.editLoadAmount.text.toString()
                .toDouble()
        loanTime =
            if (TextUtils.isEmpty(mBinding.editLoanTime.text.toString())) 0 else mBinding.editLoanTime.text.toString()
                .toInt()
        loanRate =
            if (TextUtils.isEmpty(mBinding.editLoadRate.text.toString())) 0.0 else mBinding.editLoadRate.text.toString()
                .toDouble()
        firstDate =
            if (TextUtils.isEmpty(mBinding.tvRepaymentDateDesc.text.toString())) "" else mBinding.tvRepaymentDateDesc.text.toString()

        if (loanAmount == 0.0) {
            ToastUtils.toast(this, "??????????????????")
            return false
        }

        if (loanTime == 0) {
            ToastUtils.toast(this, "??????????????????")
            return false
        }

        if (loanRate == 0.0) {
            ToastUtils.toast(this, "??????????????????")
            return false
        }
        if (TextUtils.isEmpty(firstDate)) {
            ToastUtils.toast(this, "??????????????????????????????")
            return false
        }
        return true
    }


}