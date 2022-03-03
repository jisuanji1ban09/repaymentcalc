package com.itwzh.repaymentcalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.adapter.RepaymentPlanAdapter
import com.itwzh.repaymentcalc.databinding.ActivityRepaymentPlanBinding
import com.itwzh.repaymentcalc.model.RepaymentPlan
import com.itwzh.repaymentcalc.utlis.getRepaymentPlan
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable.create
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class RepaymentPlanActivity : AppCompatActivity() {

    val mBinding:ActivityRepaymentPlanBinding by lazy {
        ActivityRepaymentPlanBinding.inflate(layoutInflater)
    }

    val mAdapter:RepaymentPlanAdapter by lazy { RepaymentPlanAdapter(this) }

    private var amount: Double = 0.0;
    private var months: Int = 0;
    private var rate: Double = 0.0;
    private var isEP :Boolean = false;
    private var repaymentFirstMont :String = "2022年03月03日"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.recyleView.adapter = mAdapter
        amount = intent.getDoubleExtra(CommonValues.amount,0.0)
        months = intent.getIntExtra(CommonValues.months,0)
        rate = intent.getDoubleExtra(CommonValues.rate,0.0)
        isEP = intent.getBooleanExtra(CommonValues.isEP,false)
        val c =create<List<RepaymentPlan>> {
            val repaymentPlan = getRepaymentPlan(
                amount = amount,
                rate = rate,
                months = months,
                date = repaymentFirstMont,
                isEP = isEP
            )
            it.onNext(repaymentPlan)
        }
        c.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { data->
                mAdapter.setData(data)
            })
    }
}