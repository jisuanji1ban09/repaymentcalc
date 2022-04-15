package com.itwzh.repaymentcalc.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.databinding.ActivityAdvanceRepaymentCalcBinding
import com.itwzh.repaymentcalc.utlis.*
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import kotlin.math.min

class AdvanceRepaymentCalcActivity : AppCompatActivity(), View.OnClickListener {

    private var loanAmount: Double = 0.0
    private var loanTime: Int = 0
    private var loanRate: Double = 0.0
    private var isEP: Boolean = false
    private var firstDate: String = ""
    private var repaymentAmount: Double = 0.0
    private var isAdvanceEP: Boolean = false
    private var advanceDate: String = ""
    private var preClick: Long = 0
    private var firstRepayment: Long = 0
    private var advanceRepayment: Long = 0
    private val maxTime = getMaxMonthByChoose()
    private val minTime = getMinMonthByChoose()

    private val mBinding: ActivityAdvanceRepaymentCalcBinding by lazy {
        ActivityAdvanceRepaymentCalcBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.btnCalc.setOnClickListener(this)
        mBinding.btnShow.setOnClickListener(this)
        mBinding.clRepaymentDateChoose.setOnClickListener(this)
        mBinding.checkboxEPAI.setOnClickListener(this)
        mBinding.checkboxEP.setOnClickListener(this)
        mBinding.checkboxAdvanceEP.setOnClickListener(this)
        mBinding.checkboxAdvanceEPAI.setOnClickListener(this)
        mBinding.clAdvanceRepaymentDateChoose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnCalc.id -> calc()
            mBinding.btnShow.id -> showRepaymentPlan()
            mBinding.clRepaymentDateChoose.id -> showDateChoose()
            mBinding.checkboxEPAI.id -> resetCheckBox(false)
            mBinding.checkboxEP.id -> resetCheckBox(true)
            mBinding.checkboxAdvanceEP.id -> resetAdvanceCheckBox(true)
            mBinding.checkboxAdvanceEPAI.id -> resetAdvanceCheckBox(false)
            mBinding.clAdvanceRepaymentDateChoose.id -> showAdvanceDateChoose()
        }
    }


    private fun resetAdvanceCheckBox(isAdvanceEp: Boolean) {
        this.isAdvanceEP = isAdvanceEp
        mBinding.checkboxAdvanceEPAI.isChecked = !isAdvanceEP
        mBinding.checkboxAdvanceEP.isChecked = isAdvanceEP
    }

    private fun resetCheckBox(isEP: Boolean) {
        this.isEP = isEP
        mBinding.checkboxEPAI.isChecked = !isEP
        mBinding.checkboxEP.isChecked = isEP
    }

    private fun showAdvanceDateChoose() {
        if (System.currentTimeMillis() - preClick < 1000) return
        preClick = System.currentTimeMillis()
        if (firstRepayment == 0L) {
            ToastUtils.toast(this, "请选择第一次还款日期")
            return
        }
        loanTime =
            if (TextUtils.isEmpty(mBinding.editLoanTime.text.toString())) 0 else mBinding.editLoanTime.text.toString()
                .toInt()
        if (loanTime == 0) {
            ToastUtils.toast(this, "请输入贷款时长")
            return
        }
        firstDate =
            if (TextUtils.isEmpty(mBinding.tvRepaymentDateDesc.text.toString())) "" else mBinding.tvRepaymentDateDesc.text.toString()
        val endMillisecond = dateParse(getEndDate(firstDate, loanTime))
        if (System.currentTimeMillis()>endMillisecond){
            ToastUtils.toast(this, "贷款已结清")
            return
        }
        val minTime = getNextMonth(firstRepayment)
        val maxTime = getAdvanceMaxMonth(firstDate = firstRepayment, loanTime - 1)
        CardDatePickerDialog.builder(this)
            .setTitle("选择日期")
            .showBackNow(false)
            .showFocusDateInfo(false)
            .setDefaultTime(advanceRepayment)
            .setMinTime(minTime)
            .setMaxTime(maxTime)
            .setPickerLayout(R.layout.layoyt_month_choose)
            .setLabelText("年", "月")
            .setOnChoose("确定") { millisecond ->
                advanceRepayment = millisecond
                mBinding.tvAdvanceRepaymentDateDesc.text =
                    advanceDateFormat(millisecond, dateFormat(firstRepayment))
            }.setOnCancel("取消") {}.build().show()

    }


    private fun showDateChoose() {
        if (System.currentTimeMillis() - preClick < 1000) return
        preClick = System.currentTimeMillis()
        CardDatePickerDialog.builder(this)
            .setTitle("选择日期")
            .showBackNow(false)
            .setDefaultTime(firstRepayment)
            .setMinTime(minTime)
            .setMaxTime(maxTime)
            .showFocusDateInfo(false)
            .setPickerLayout(R.layout.layoyt_date_choose)
            .setLabelText("年", "月", "日")
            .setOnChoose("确定") { millisecond ->
                firstRepayment = millisecond
                mBinding.tvRepaymentDateDesc.text = dateFormat(millisecond)
                mBinding.tvAdvanceRepaymentDateDesc.text = ""
            }.setOnCancel("取消") {}.build().show()
    }

    private fun showRepaymentPlan() {
        hideSoftKeyboard(this, mBinding.btnCalc)
        if (getCalcParam()) {
            val intent: Intent = Intent(this, AdvanceRepaymentPlanActivity().javaClass)
            intent.putExtra(CommonValues.amount, loanAmount)
            intent.putExtra(CommonValues.rate, loanRate)
            intent.putExtra(CommonValues.months, loanTime)
            intent.putExtra(CommonValues.isEP, isEP)
            intent.putExtra(CommonValues.date, firstDate)
            intent.putExtra(CommonValues.repaymentAmount, repaymentAmount)
            intent.putExtra(CommonValues.isAdvance, isAdvanceEP)
            intent.putExtra(CommonValues.repaymentDate, advanceDate)
            startActivity(intent)
        }
    }

    fun calc() {
        hideSoftKeyboard(this, mBinding.btnCalc)
        if (getCalcParam()) {
            val loanResult = getLoanResultAdvance(
                amount = loanAmount,
                months = loanTime,
                rate = loanRate,
                isEP = isEP,
                firstDate = firstDate,
                advanceAmount = repaymentAmount,
                isAdvance = isAdvanceEP,
                advanceDate = advanceDate
            )
            if (loanResult.isAdvance) {
                mBinding.tvLoanTimes.text = "原贷款期限"
                mBinding.clLoanTimesNew.visibility = View.VISIBLE
                mBinding.lineLoanTimesNew.visibility = View.VISIBLE
                mBinding.tvLastDate.text = "原最后还款日期"
                mBinding.clReduceMonths.visibility = View.VISIBLE
                mBinding.lineReduceMonths.visibility=View.VISIBLE
            } else {
                mBinding.tvLoanTimes.text = "贷款期限"
                mBinding.clLoanTimesNew.visibility = View.GONE
                mBinding.lineLoanTimesNew.visibility = View.GONE
                mBinding.tvLastDate.text = "最后还款日期"

                mBinding.clReduceMonths.visibility = View.GONE
                mBinding.lineReduceMonths.visibility=View.GONE

            }
            if (loanResult.isEP) {
                //月还款额
                mBinding.clRepaymentMonth.visibility = View.GONE
                mBinding.clRepaymentMonthNew.visibility = View.GONE

                //首月还款
                mBinding.clRepaymentMonthFirst.visibility = View.VISIBLE
                mBinding.clRepaymentMonthFirstNew.visibility = View.VISIBLE

            } else {
                //月还款额
                mBinding.clRepaymentMonth.visibility = View.VISIBLE
                mBinding.clRepaymentMonthNew.visibility = View.VISIBLE

                //首月还款
                mBinding.clRepaymentMonthFirst.visibility = View.GONE
                mBinding.clRepaymentMonthFirstNew.visibility = View.GONE
            }
            mBinding.cardResult.visibility = View.VISIBLE
            mBinding.result = loanResult
            mBinding.btnShow.visibility = View.VISIBLE
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
        repaymentAmount =
            if (TextUtils.isEmpty(mBinding.editRepaymentAmount.text.toString())) 0.0 else mBinding.editRepaymentAmount.text.toString()
                .toDouble()
        advanceDate =
            if (TextUtils.isEmpty(mBinding.tvAdvanceRepaymentDateDesc.text.toString())) "" else mBinding.tvAdvanceRepaymentDateDesc.text.toString()
        if (loanAmount == 0.0) {
            ToastUtils.toast(this, "贷款金额错误")
            return false
        }

        if (loanTime == 0) {
            ToastUtils.toast(this, "贷款时长错误")
            return false
        }
        if (loanTime<2){
            ToastUtils.toast(this, "请使用贷款计算器计算")
            return false
        }
        if (loanRate == 0.0) {
            ToastUtils.toast(this, "贷款利率错误")
            return false
        }
        if (TextUtils.isEmpty(firstDate)) {
            ToastUtils.toast(this, "请选择第一次还款日期")
            return false
        }
        if (repaymentAmount == 0.0) {
            ToastUtils.toast(this, "提前还款金额错误")
            return false
        }
        if (TextUtils.isEmpty(advanceDate)) {
            ToastUtils.toast(this, "请选择提前约定还款日期")
            return false
        }
        if (advanceRepayment < firstRepayment) {
            ToastUtils.toast(this, "提前还款日期错误")
            return false
        }
        val endRepaymentDay = dateParse(getEndDate(firstDate, months = loanTime - 1))
        if (advanceRepayment > endRepaymentDay) {
            ToastUtils.toast(this, "提前还款日期错误")
            return false
        }
        return true
    }
}