package com.itwzh.repaymentcalc.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itwzh.repaymentcalc.CommonValues
import com.itwzh.repaymentcalc.adapter.RepaymentPlanAdapter
import com.itwzh.repaymentcalc.databinding.ActivityRepaymentPlanBinding
import com.itwzh.repaymentcalc.listener.JumpListener
import com.itwzh.repaymentcalc.model.RepaymentPlan
import com.itwzh.repaymentcalc.utlis.getRepaymentPlan
import com.itwzh.repaymentcalc.widget.JumpDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable.create
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class RepaymentPlanActivity : AppCompatActivity() {

    val mBinding: ActivityRepaymentPlanBinding by lazy {
        ActivityRepaymentPlanBinding.inflate(layoutInflater)
    }

    val mAdapter: RepaymentPlanAdapter by lazy { RepaymentPlanAdapter(this) }

    private var amount: Double = 0.0;
    private var months: Int = 0;
    private var rate: Double = 0.0;
    private var isEP: Boolean = false;
    private var repaymentFirstMonth: String = "2022年03月03日"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.recyleView.adapter = mAdapter
        amount = intent.getDoubleExtra(CommonValues.amount, 0.0)
        months = intent.getIntExtra(CommonValues.months, 0)
        rate = intent.getDoubleExtra(CommonValues.rate, 0.0)
        isEP = intent.getBooleanExtra(CommonValues.isEP, false)
        repaymentFirstMonth = intent.getStringExtra(CommonValues.date).toString()
        mBinding.recyleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> mBinding.fb.visibility = View.VISIBLE
                    RecyclerView.SCROLL_STATE_DRAGGING,
                    RecyclerView.SCROLL_STATE_SETTLING -> mBinding.fb.visibility = View.GONE
                }

            }
        })
        mBinding.fb.setOnClickListener {
            val count = mAdapter.itemCount;
            val manager: LinearLayoutManager =
                mBinding.recyleView.layoutManager as LinearLayoutManager
            val firstP = manager.findFirstCompletelyVisibleItemPosition()
            val lastP = manager.findLastCompletelyVisibleItemPosition()
            var state = 1
            if (firstP == 0) {
                state = 2
            }
            if (lastP == count - 1)
                state = 3
            JumpDialog(this, count = count, state = state, object : JumpListener {
                override fun jump(issue: Int) {
                    if (issue < 0 || issue > count) {
                        mBinding.recyleView.scrollToPosition(count - 1)
                    } else if (issue > 0) {
                        mBinding.recyleView.scrollToPosition(issue - 1)
                    } else {
                        mBinding.recyleView.scrollToPosition(0)
                    }
                }

            }).show()
        }
        val c = create<List<RepaymentPlan>> {
            val repaymentPlan = getRepaymentPlan(
                amount = amount,
                rate = rate,
                months = months,
                date = repaymentFirstMonth,
                isEP = isEP
            )
            for (issue in 0..repaymentPlan.size-1){
//                Log.i("kfzx-wzh",repaymentPlan.get(issue).toString())
            }
            it.onNext(repaymentPlan)
        }
        c.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { data ->
                mAdapter.setData(data)
                mBinding.fb.visibility = View.VISIBLE
            })
    }
}