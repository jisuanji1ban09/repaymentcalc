package com.itwzh.repaymentcalc.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.itwzh.repaymentcalc.databinding.ItemRepaymentPlanBinding
import com.itwzh.repaymentcalc.model.RepaymentPlan

class RepaymentPlanViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(plan: RepaymentPlan) {
        val mBinding :ItemRepaymentPlanBinding = binding as ItemRepaymentPlanBinding;
        mBinding.model = plan
    }
}