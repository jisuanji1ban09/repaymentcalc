package com.itwzh.repaymentcalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.itwzh.repaymentcalc.R
import com.itwzh.repaymentcalc.databinding.ActivityRepaymentCalcBinding
import com.itwzh.repaymentcalc.utlis.ToastUtils
import com.itwzh.repaymentcalc.utlis.dateFormat
import com.itwzh.repaymentcalc.utlis.getRepaymentPlan
import com.loper7.date_time_picker.dialog.CardDatePickerDialog

class RepaymentCalcActivity : AppCompatActivity(), View.OnClickListener {

    private var loanAmount: Double = 0.0;
    private var loanTime: Int = 0;
    private var loanRate: Double = 0.0;
    private var isEP :Boolean = false;
    private var isAdvanceEP :Boolean = false;
    private var preClick:Long = 0;
    private var firstRepayment:Long = 0;

    private val mBinding:ActivityRepaymentCalcBinding by lazy {
        ActivityRepaymentCalcBinding.inflate(layoutInflater)
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
        when(v?.id){
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
        this.isAdvanceEP = isAdvanceEp;
        mBinding.checkboxAdvanceEPAI.isChecked = !isAdvanceEP
        mBinding.checkboxAdvanceEP.isChecked = isAdvanceEP
    }

    private fun resetCheckBox(isEP: Boolean) {
        this.isEP = isEP
        mBinding.checkboxEPAI.isChecked = !isEP
        mBinding.checkboxEP.isChecked = isEP
    }

    private fun showAdvanceDateChoose() {
        if (System.currentTimeMillis()-preClick<1000) return
        if (firstRepayment == 0L){
            ToastUtils.toast(this,"请选择第一次还款日期")
            return
        }
        CardDatePickerDialog.builder(this)
            .setTitle("选择日期")
            .showBackNow(false)
            .showFocusDateInfo(false)
            .setPickerLayout(R.layout.layoyt_date_choose)
            .setLabelText("年","月","日")
            .setOnChoose("确定") {millisecond->
                firstRepayment = millisecond
                mBinding.tvRepaymentDateDesc.text = dateFormat(millisecond)
            }.setOnCancel("取消"){}.
            build().show()

    }


    private fun showDateChoose() {
        if (System.currentTimeMillis()-preClick<1000) return
        preClick = System.currentTimeMillis();
        CardDatePickerDialog.builder(this)
            .setTitle("选择日期")
            .showBackNow(false)
            .showFocusDateInfo(false)
            .setPickerLayout(R.layout.layoyt_date_choose)
            .setLabelText("年","月","日")
            .setOnChoose("确定") {millisecond->
                firstRepayment = millisecond
                mBinding.tvRepaymentDateDesc.text = dateFormat(millisecond)
            }.setOnCancel("取消"){}.
            build().show()
    }

    private fun showRepaymentPlan() {
        TODO("Not yet implemented")
    }

    fun calc(){
        TODO("Not yet implemented")
    }
}