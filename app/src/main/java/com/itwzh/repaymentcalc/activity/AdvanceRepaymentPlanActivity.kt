package com.itwzh.repaymentcalc.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.adapter.RepaymentPlanAdapter
import com.itwzh.repaymentcalc.databinding.ActivityAdvanceRepaymentPlanBinding
import com.itwzh.repaymentcalc.model.RepaymentPlan
import com.itwzh.repaymentcalc.utlis.getAdvanceRepaymentPlan
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class AdvanceRepaymentPlanActivity : AppCompatActivity() {

    val mBinding: ActivityAdvanceRepaymentPlanBinding by lazy {
        ActivityAdvanceRepaymentPlanBinding.inflate(layoutInflater)
    }

    val mAdapter: RepaymentPlanAdapter by lazy { RepaymentPlanAdapter(this) }

    private var amount: Double = 0.0
    private var months: Int = 0;
    private var rate: Double = 0.0
    private var isEP: Boolean = false
    private var repaymentFirstMonth: String = ""
    private var repaymentAmount: Double = 0.0
    private var isAdvance: Boolean = true
    private var repaymentDate: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.recyleView.adapter = mAdapter
        amount = intent.getDoubleExtra(CommonValues.amount, 0.0)
        months = intent.getIntExtra(CommonValues.months, 0)
        rate = intent.getDoubleExtra(CommonValues.rate, 0.0)
        isEP = intent.getBooleanExtra(CommonValues.isEP, false)
        repaymentFirstMonth = intent.getStringExtra(CommonValues.date).toString()
        repaymentAmount = intent.getDoubleExtra(CommonValues.repaymentAmount, 0.0)
        isAdvance = intent.getBooleanExtra(CommonValues.isAdvance, true)
        repaymentDate = intent.getStringExtra(CommonValues.repaymentDate).toString()
        val c = Observable.create<List<RepaymentPlan>> {
            val repaymentPlan = getAdvanceRepaymentPlan(
                amount = amount,
                rate = rate,
                months = months,
                firstDate = repaymentFirstMonth,
                isEP = isEP,
                repaymentAmount = repaymentAmount,
                isAdvance = isAdvance,
                repaymentDate = repaymentDate
            )
            it.onNext(repaymentPlan)
        }
        c.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { data ->
                mAdapter.setData(data)
            })
    }
}