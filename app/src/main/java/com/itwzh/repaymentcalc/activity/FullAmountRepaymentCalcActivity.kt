package com.itwzh.repaymentcalc.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.databinding.ActivityFullAmountRepaymentCalcBinding
import com.itwzh.repaymentcalc.utlis.*
import com.loper7.date_time_picker.dialog.CardDatePickerDialog

class FullAmountRepaymentCalcActivity : AppCompatActivity(), View.OnClickListener {

    private var loanAmount: Double = 0.0
    private var loanTime: Int = 0
    private var loanRate: Double = 0.0
    private var isEP: Boolean = false
    private var firstDate: String = ""
    private var advanceDate: String = ""
    private var preClick: Long = 0
    private var firstRepayment: Long = 0
    private var advanceRepayment: Long = 0
    private val maxTime = getMaxMonthByChoose()
    private val minTime = getMinMonthByChoose()

    private val mBinding: ActivityFullAmountRepaymentCalcBinding by lazy {
        ActivityFullAmountRepaymentCalcBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.btnCalc.setOnClickListener(this)
        mBinding.clRepaymentDateChoose.setOnClickListener(this)
        mBinding.checkboxEPAI.setOnClickListener(this)
        mBinding.checkboxEP.setOnClickListener(this)
        mBinding.clAdvanceRepaymentDateChoose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnCalc.id -> calc()
            mBinding.checkboxEPAI.id -> resetCheckBox(false)
            mBinding.checkboxEP.id -> resetCheckBox(true)
            mBinding.clRepaymentDateChoose.id -> showDateChoose()
            mBinding.clAdvanceRepaymentDateChoose.id -> showAdvanceDateChoose()
        }
    }

    private fun showAdvanceDateChoose() {
        if (System.currentTimeMillis() - preClick < 1000) return
        preClick = System.currentTimeMillis()
        if (firstRepayment == 0L) {
            ToastUtils.toast(this, "??????????????????????????????")
            return
        }

        loanTime =
            if (TextUtils.isEmpty(mBinding.editLoanTime.text.toString())) 0 else mBinding.editLoanTime.text.toString()
                .toInt()
        if (loanTime == 0) {
            ToastUtils.toast(this, "?????????????????????")
            return
        }
        firstDate =
            if (TextUtils.isEmpty(mBinding.tvRepaymentDateDesc.text.toString())) "" else mBinding.tvRepaymentDateDesc.text.toString()
        val endMillisecond = dateParse(getEndDate(firstDate, loanTime))
        if (System.currentTimeMillis()>endMillisecond){
            ToastUtils.toast(this, "???????????????")
            return
        }
        val minTime = getNextMonth(firstRepayment)
        val maxTime = getAdvanceMaxMonth(firstDate = firstRepayment, loanTime - 1)
        CardDatePickerDialog.builder(this)
            .setTitle("????????????")
            .showBackNow(false)
            .showFocusDateInfo(false)
            .setDefaultTime(advanceRepayment)
            .setMinTime(minTime)
            .setMaxTime(maxTime)
            .setPickerLayout(R.layout.layoyt_month_choose)
            .setLabelText("???", "???")
            .setOnChoose("??????") { millisecond ->
                advanceRepayment = millisecond
                mBinding.tvAdvanceRepaymentDateDesc.text = advanceDateFormat(millisecond, dateFormat(firstRepayment))
            }.setOnCancel("??????") {}.build().show()
    }

    private fun showDateChoose() {
        if (System.currentTimeMillis() - preClick < 1000) return
        preClick = System.currentTimeMillis()
        CardDatePickerDialog.builder(this)
            .setTitle("????????????")
            .showBackNow(false)
            .setDefaultTime(firstRepayment)
            .setMinTime(minTime)
            .setMaxTime(maxTime)
            .showFocusDateInfo(false)
            .setPickerLayout(R.layout.layoyt_date_choose)
            .setLabelText("???", "???", "???")
            .setOnChoose("??????") { millisecond ->
                firstRepayment = millisecond
                mBinding.tvRepaymentDateDesc.text = dateFormat(millisecond)
                mBinding.tvAdvanceRepaymentDateDesc.text = ""
            }.setOnCancel("??????") {}.build().show()
    }

    private fun resetCheckBox(newEp: Boolean) {
        this.isEP = newEp
        mBinding.checkboxEPAI.isChecked = !isEP
        mBinding.checkboxEP.isChecked = isEP
    }

    fun calc() {
        hideSoftKeyboard(this, mBinding.btnCalc)
        if (getCalcParam()) {
            val loanResult = getLoanResultFullAmount(
                amount = loanAmount,
                months = loanTime,
                rate = loanRate,
                isEP = isEP,
                firstDate = firstDate,
                advanceDate = advanceDate
            )
            if (loanResult.isEP) {
                mBinding.clRepaymentMonth.visibility = View.GONE
                mBinding.clRepaymentMonthFirst.visibility = View.VISIBLE
            } else {
                mBinding.clRepaymentMonth.visibility = View.VISIBLE
                mBinding.clRepaymentMonthFirst.visibility = View.GONE
            }
            mBinding.cardResult.visibility = View.VISIBLE
            mBinding.result = loanResult

        }
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
        advanceDate =
            if (TextUtils.isEmpty(mBinding.tvAdvanceRepaymentDateDesc.text.toString())) "" else mBinding.tvAdvanceRepaymentDateDesc.text.toString()
        if (loanAmount == 0.0) {
            ToastUtils.toast(this, "??????????????????")
            return false
        }

        if (loanTime == 0) {
            ToastUtils.toast(this, "??????????????????")
            return false
        }
        if (loanTime<2){
            ToastUtils.toast(this, "??????????????????????????????")
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
        if (TextUtils.isEmpty(advanceDate)) {
            ToastUtils.toast(this, "?????????????????????????????????")
            return false
        }
        if (advanceRepayment < firstRepayment) {
            ToastUtils.toast(this, "????????????????????????")
            return false
        }
        val endRepaymentDay = dateParse(getEndDate(firstDate, months = loanTime - 1))
        if (advanceRepayment > endRepaymentDay) {
            ToastUtils.toast(this, "????????????????????????")
            return false
        }
        return true
    }

}