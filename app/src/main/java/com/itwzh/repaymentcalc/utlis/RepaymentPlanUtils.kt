package com.itwzh.repaymentcalc.utlis

import com.itwzh.repaymentcalc.model.RepaymentPlan

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
    return mData
}

//var repaymentDate: String = "",//还款日期
//var issueNumber: String = ""//期数
//var repaymentAmount: Double = 0.0, //月还款额 ->由本金和利息组成
//var repaymentPrincipal: Double = 0.0,//月还本金
//var repaymentInterest: Double = 0.0,//月还利息
//var surplusPrincipal: Double = 0.0, //剩余贷款金额

fun getRepaymentDate(date: String, issue: Int): String {
    var year = date.substring(0, 4).toInt()
    var month = date.substring(6, 7).toInt()
    var day = date.substring(9, 10).toInt()
    year += (month + issue - 1) / 12
    month = (month + (issue - 1)) % 12
    return "${year}年${if (month < 10) "0" + month else month.toString()}月${if (day < 10) "0" + day else day.toString()}日"
}

fun getAmountEP(amount: Double, months: Int, rate: Double, issue: Int): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    var repaymentPrincipal = amount / months
    var surplusPrincipal = amount - (repaymentPrincipal * issue)
    var repaymentInterest = (surplusPrincipal - repaymentPrincipal) * rate
    map.put("repaymentAmount", repaymentPrincipal + repaymentInterest)
    map.put("repaymentPrincipal", repaymentPrincipal)
    map.put("repaymentInterest", repaymentInterest)
    map.put("surplusPrincipal", surplusPrincipal)
    return map;
}

fun getAmountEPAI(
    amount: Double,
    months: Int,
    rate: Double,
    preAmount: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    val repaymentAmount: Double =
        amount * rate * Math.pow(
            1 + rate,
            months.toDouble()
        ) / (Math.pow(1 + rate, months.toDouble()) - 1) //每月还款金额
    var repaymentInterest = preAmount * rate //利息
    var repaymentPrincipal = repaymentAmount - repaymentInterest
    var surplusPrincipal = preAmount - repaymentInterest //剩余本金
    map.put("repaymentAmount", repaymentPrincipal + repaymentInterest)
    map.put("repaymentPrincipal", repaymentPrincipal)
    map.put("repaymentInterest", repaymentInterest)
    map.put("surplusPrincipal", surplusPrincipal)
    return map;
}