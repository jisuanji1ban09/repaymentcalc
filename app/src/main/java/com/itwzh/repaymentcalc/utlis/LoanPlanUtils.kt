package com.itwzh.repaymentcalc.utlis

import android.util.Log
import com.itwzh.repaymentcalc.model.RepaymentPlan
import java.math.BigDecimal
import java.text.NumberFormat

/**
 * @param isEP 是否是等额本金还款
 */
fun getRepaymentPlan(
    amount: Double,
    months: Int,
    rate: Double,
    date: String,
    isEP: Boolean
): List<RepaymentPlan> {
    var mData: ArrayList<RepaymentPlan> = ArrayList()
    var rateOfMonth = rate / (100 * 12)
    for (issue in 1..months) {
        var plan = RepaymentPlan()
        plan.repaymentDate = getRepaymentDate(date, issue)
        plan.issueNumber = "第${issue}期"
        var excessInterest = if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded
        if (isEP) {
            val amountEP = getAmountEP(amount, months, rateOfMonth, issue, excessInterest)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!!
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
            plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
        } else {
            val amountEPAI = getAmountEPAI(
                amount,
                months,
                rateOfMonth,
                if (mData.size > 0) mData.last().surplusPrincipal else amount,
                excessInterest
            )
            plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
            plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
            plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
            plan.interestNotIncluded = amountEPAI.get("interestNotIncluded")!!
        }
        mData.add(plan)
    }
    return mData
}


fun getRepaymentDate(date: String, issue: Int): String {
    var year = date.substring(0, 4).toInt()
    var month = date.substring(5, 7).toInt()
    val day = date.substring(8, 10).toInt()
    year += (month + issue - 1) / 12
    month = (month + (issue - 1)) % 12
    return "${year}年${if (month < 10) "0" + month else month.toString()}月${if (day < 10) "0" + day else day.toString()}日"
}

fun getAmountEP(
    amount: Double,
    months: Int,
    rate: Double,
    issue: Int,
    excessInterest: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    var repaymentPrincipal = amount / months
    var surplusPrincipal = amount - (repaymentPrincipal * issue)
    var repaymentInterest = (surplusPrincipal + repaymentPrincipal) * rate
    var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
    var interestNotIncluded = repaymentInterest - interset + excessInterest
    //如果额外利息大于0.01 当期多还0.01
    if (interestNotIncluded > 0.01) {
        interset += 0.01;
        interestNotIncluded -= 0.01
    }
    map.put("repaymentAmount", repaymentPrincipal + interset)
    map.put("repaymentPrincipal", repaymentPrincipal)
    map.put("repaymentInterest", interset)
    map.put("surplusPrincipal", surplusPrincipal)
    map.put("interestNotIncluded", interestNotIncluded)
    return map;
}

fun getAmountEPAI(
    amount: Double,
    months: Int,
    rate: Double,
    preAmount: Double,
    excessInterest: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    val repaymentAmount: Double =
        amount * rate * Math.pow(
            1 + rate,
            months.toDouble()
        ) / (Math.pow(1 + rate, months.toDouble()) - 1) //每月还款金额
    var repaymentInterest = preAmount * rate //利息
    var repaymentPrincipal = repaymentAmount - repaymentInterest
    var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
    var interestNotIncluded = repaymentInterest - interset + excessInterest
    //如果额外利息大于0.01 当期多还0.01
    if (interestNotIncluded > 0.01) {
        interset += 0.01;
        interestNotIncluded -= 0.01
        repaymentPrincipal -= 0.01
    }
    var surplusPrincipal = preAmount - repaymentPrincipal //剩余本金
    map.put("repaymentAmount", repaymentPrincipal + interset)
    map.put("repaymentPrincipal", repaymentPrincipal)
    map.put("repaymentInterest", interset)
    map.put("surplusPrincipal", surplusPrincipal)
    map.put("interestNotIncluded", interestNotIncluded)
    return map;
}

fun getRepaymentNormalMonth(first: String, advance: String): Int {
    var yearFirst = first.substring(0, 4).toInt()
    var monthFirst = first.substring(6, 7).toInt()
    var yearAd = advance.substring(0, 4).toInt()
    var monthAd = advance.substring(6, 7).toInt()
    return (yearAd - yearFirst) * 12 + (monthAd - monthFirst) + 1
}