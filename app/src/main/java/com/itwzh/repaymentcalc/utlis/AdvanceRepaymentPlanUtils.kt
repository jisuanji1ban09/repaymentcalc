package com.itwzh.repaymentcalc.utlis

import com.itwzh.repaymentcalc.model.RepaymentPlan

class AdvanceRepaymentPlanUtils {

    fun getAdvanceRepaymentPlan(
        amount: Double,//贷款金额
        months:Int,//贷款期限(月)
        rate:Double,//贷款利率
        isEP:Boolean,//还款方式 是->等额本金 否->等额本息
        firstDate:String,//第一次还款日期
        repaymentAmount:Double,//提前还款金额
        isAdvance:Boolean,//还款约定 是->减少还款金额，期限不变，否->还款金额不变，提前结束还款
        repaymentDate:String//提前还款日期
        ): List<RepaymentPlan> {
        var mData: ArrayList<RepaymentPlan> = ArrayList();
        val rateOfMonth = rate / (100 * 12)
        val naturalMonth = getNaturalRepaymentMonth(firstDate,repaymentDate);
        for (issue in 1..naturalMonth){
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, issue)
            plan.issueNumber = "第${issue}期"
            if (isEP) {
                val amountEP = getAmountEP(amount, months, rateOfMonth, issue)
                plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                plan.repaymentInterest = amountEP.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
            } else {
                val amountEPAI = getAmountEPAI(
                    amount,
                    months,
                    rateOfMonth,
                    if (mData.size > 0) mData.last().surplusPrincipal else amount
                )
                plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
            }
            mData.add(plan)
        }
        val naturalSP = mData.last().surplusPrincipal-repaymentAmount;
        if (isAdvance){ //减少还款金额
            val surplusIssue = months-naturalMonth;
            for (issue in naturalMonth..months){
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                if (isEP) {
                    val amountEP = getAmountEP(naturalSP, surplusIssue, rateOfMonth, issue-surplusIssue+1)
                    if (issue == naturalMonth){
                        plan.repaymentAmount = amountEP.get("repaymentAmount")!!+repaymentAmount
                        plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!+repaymentAmount
                    }else{
                        plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                        plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                    }
                    plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
                    plan.repaymentInterest = amountEP.get("repaymentInterest")!!
                } else {
                    val amountEPAI = getAmountEPAI(
                        naturalSP,
                        surplusIssue,
                        rateOfMonth,
                        if (mData.size > naturalMonth-1) mData.last().surplusPrincipal else naturalSP
                    )
                    if (issue == naturalMonth){
                        plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!+repaymentAmount
                        plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!+repaymentAmount
                    }else{
                        plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                        plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                    }
                    plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                    plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
                }
                mData.add(plan)
            }
        }else{ // 提前结束
            
        }
        return mData;
    }

    private fun getNaturalRepaymentMonth(firstDate: String, repaymentDate: String): Int {
        val firstYear = firstDate.substring(0,4).toInt();
        val firstMonth = firstDate.substring(5,7).toInt()
        val repaymentYear = repaymentDate.substring(0,4).toInt()
        val repaymentMonth = repaymentDate.substring(5,7).toInt()
        return (repaymentYear - firstYear) * 12 + (repaymentMonth - firstMonth) + 1
    }
}