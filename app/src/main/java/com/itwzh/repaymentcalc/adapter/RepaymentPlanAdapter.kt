package com.itwzh.repaymentcalc.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itwzh.repaymentcalc.databinding.ItemRepaymentPlanBinding
import com.itwzh.repaymentcalc.model.RepaymentPlan

class RepaymentPlanAdapter(private val context:Context) :
    RecyclerView.Adapter<RepaymentPlanViewHolder>() {

    var mData:List<RepaymentPlan> = ArrayList()

    fun setData(mData:List<RepaymentPlan>){
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentPlanViewHolder {
        val binding = ItemRepaymentPlanBinding.inflate(LayoutInflater.from(context),parent,false)
        return RepaymentPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepaymentPlanViewHolder, position: Int) {
//        val binding = holder.itemView as ItemRepaymentPlanBinding
//        binding.model = mData.get(position)
        holder.bind(mData.get(position))
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}