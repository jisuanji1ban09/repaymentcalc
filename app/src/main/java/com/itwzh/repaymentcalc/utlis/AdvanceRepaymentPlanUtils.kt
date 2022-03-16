package com.itwzh.repaymentcalc.utlis

import android.util.Log
import com.itwzh.repaymentcalc.model.RepaymentPlan

fun getAdvanceRepaymentPlan(
    amount: Double,//贷款金额
    months: Int,//贷款期限(月)
    rate: Double,//贷款利率
    isEP: Boolean,//还款方式 是->等额本金 否->等额本息
    firstDate: String,//第一次还款日期
    repaymentAmount: Double,//提前还款金额
    isAdvance: Boolean,//还款约定 是->减少还款金额，期限不变，否->还款金额不变，提前结束还款
    repaymentDate: String//提前还款日期
): List<RepaymentPlan> {
    var mData: ArrayList<RepaymentPlan> = ArrayList();
    val rateOfMonth = rate / (100 * 12)
    val naturalMonth = getNaturalRepaymentMonth(firstDate, repaymentDate)
    val normalEndMonth = naturalMonth - 1;
    Log.i("wzh", "naturalMonth is $naturalMonth")
    for (issue in 1..normalEndMonth) {
        var plan = RepaymentPlan()
        plan.repaymentDate = getRepaymentDate(firstDate, issue)
        plan.issueNumber = "第${issue}期"
        if (isEP) { //等额本金
            val amountEP = getAmountEP(amount, months, rateOfMonth, issue)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!!
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
        } else {  //等额本息
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
    val naturalSP = mData.last().surplusPrincipal - repaymentAmount;
    if (isAdvance) { //减少还款金额
        val surplusIssue = months - normalEndMonth
        for (issue in naturalMonth..months) {
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, issue)
            plan.issueNumber = "第${issue}期"
            if (isEP) { //等额本金
                val amountEP =
                    getAmountEP(naturalSP, surplusIssue, rateOfMonth, issue - normalEndMonth)
                if (issue == naturalMonth) {
                    plan.repaymentAmount = amountEP.get("repaymentAmount")!! + repaymentAmount
                    plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!! + repaymentAmount
                } else {
                    plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                    plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                }
                plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
                plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            } else { //等额本息
                val amountEPAI = getAmountEPAI(
                    naturalSP,
                    surplusIssue,
                    rateOfMonth,
                    if (mData.size > naturalMonth - 1) mData.last().surplusPrincipal else naturalSP
                )
                if (issue == naturalMonth) {
                    plan.repaymentAmount = amountEPAI.get("repaymentAmount")!! + repaymentAmount
                    plan.repaymentPrincipal =
                        amountEPAI.get("repaymentPrincipal")!! + repaymentAmount
                } else {
                    plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                    plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                }
                plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
            }
            mData.add(plan)
        }
    } else { // 提前结束
        if (isEP) { //等额本金
            //计算档期还款
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            val amountEP = getAmountEP(amount, months, rateOfMonth, naturalMonth)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!! +repaymentAmount
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!! -repaymentAmount
            mData.add(plan)
            var naturalS = mData.last().surplusPrincipal
            val amountOfMonth = mData.last().repaymentPrincipal - repaymentAmount
            val newIssue = months - (repaymentAmount / amountOfMonth).toInt()
            val startIssue = mData.size + 1
            for (issue in startIssue..newIssue) {
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                val amountEP = getAmountEPAdvance(amountOfMonth, naturalS, rateOfMonth)
                if (issue == naturalMonth) {
                    plan.repaymentAmount = amountEP.get("repaymentAmount")!! + repaymentAmount
                    plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!! + repaymentAmount
                } else {
                    plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                    plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                }
                plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
                plan.repaymentInterest = amountEP.get("repaymentInterest")!!
                mData.add(plan)
                naturalS = mData.last().surplusPrincipal
            }
        } else {//等额本息
            //计算一下提前还款月的还款计划
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            val amountEPAI = getAmountEPAI(
                amount,
                months,
                rateOfMonth,
                if (mData.size > 0) mData.last().surplusPrincipal else amount
            )
            plan.repaymentAmount = amountEPAI.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!! + repaymentAmount
            plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!! - repaymentAmount
            mData.add(plan)

            //剩余还款额
            var naturalS = mData.last().surplusPrincipal
            // 每月还款金额
            val amountOfMonth = mData.last().repaymentAmount-repaymentAmount

            val lastMonths = (Math.log(amountOfMonth/(amountOfMonth-naturalS*rateOfMonth))/Math.log(1+rateOfMonth)).toInt()

            for (issue in naturalMonth+1..naturalMonth+lastMonths+1) {
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                val amountEPAI = getAmountEPAIAdvance(
                    rateOfMonth,
                    amountOfMonth,
                    naturalS,
                )
                if (issue == naturalMonth) {
                    plan.repaymentAmount = amountEPAI.get("repaymentAmount")!! + repaymentAmount
                    plan.repaymentPrincipal =
                        amountEPAI.get("repaymentPrincipal")!! + repaymentAmount
                } else {
                    plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                    plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                }
                plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
                mData.add(plan)
                naturalS = mData.last().surplusPrincipal
            }
        }
    }
    return mData;
}

private fun getNaturalRepaymentMonth(firstDate: String, repaymentDate: String): Int {
    val firstYear = firstDate.substring(0, 4).toInt();
    val firstMonth = firstDate.substring(5, 7).toInt()
    val repaymentYear = repaymentDate.substring(0, 4).toInt()
    val repaymentMonth = repaymentDate.substring(5, 7).toInt()
    return (repaymentYear - firstYear) * 12 + (repaymentMonth - firstMonth) + 1
}


fun getAmountEPAdvance(
    amountOfMonth: Double,
    preAmount: Double,
    rate: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    if (preAmount>amountOfMonth){
        var surplusPrincipal = preAmount - amountOfMonth
        var repaymentInterest = preAmount * rate
        map.put("repaymentAmount", amountOfMonth + repaymentInterest)
        map.put("repaymentPrincipal", amountOfMonth)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", surplusPrincipal)
    }else{
        var repaymentInterest = preAmount * rate
        map.put("repaymentAmount", preAmount + repaymentInterest)
        map.put("repaymentPrincipal", preAmount)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", 0.0)
    }
    return map;
}

fun getAmountEPAIAdvance(
    rate: Double,
    repaymentAmount: Double, //每月还款金额
    preAmount: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    if (preAmount > repaymentAmount) {
        val repaymentInterest = preAmount * rate //利息
        val repaymentPrincipal = repaymentAmount - repaymentInterest
        val surplusPrincipal = preAmount - repaymentPrincipal //剩余本金
        map.put("repaymentAmount", repaymentPrincipal + repaymentInterest)
        map.put("repaymentPrincipal", repaymentPrincipal)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", surplusPrincipal)
    } else {
        val repaymentInterest = preAmount * rate //利息
        map.put("repaymentAmount", preAmount + repaymentInterest)
        map.put("repaymentPrincipal", preAmount)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", 0.0)
    }
    return map;
}