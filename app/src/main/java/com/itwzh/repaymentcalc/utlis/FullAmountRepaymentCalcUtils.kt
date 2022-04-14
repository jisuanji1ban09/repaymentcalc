package com.itwzh.repaymentcalc.utlis

import android.util.Log
import com.itwzh.repaymentcalc.model.LoanResultFullAmount
import com.itwzh.repaymentcalc.model.RepaymentPlan

fun getLoanResultFullAmount(
    amount: Double,
    months: Int,
    rate: Double,
    isEP: Boolean,
    firstDate: String,
    advanceDate: String,
): LoanResultFullAmount {
    val result = LoanResultFullAmount()
    result.loanAmount = amount
    result.loanTimes = months
    result.isEP = isEP
    result.repaymentLastDate = getRepaymentDate(firstDate, months)
    //已还期数
    val repaymentNormalMonth = getRepaymentSubMonth(firstDate, advanceDate)
    result.repaymentNormalMonth = repaymentNormalMonth;
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    val repaymentPlan = getRepaymentPlan(amount, months, rate, firstDate, isEP)
    if (isEP) {
        calcEPLoan(amount, months, rate, repaymentNormalMonth, result,repaymentPlan,firstDate)
    } else {
        calcEPAILoan(amount, months, rate, repaymentNormalMonth, result,repaymentPlan,firstDate)
    }
    return result
}

/**
 * 等额本息
 */
fun calcEPAILoan(
    principal: Double,
    months: Int,
    rate: Double,
    payTimes: Int,
    result: LoanResultFullAmount,
    repaymentPlan: List<RepaymentPlan>,
    date:String,
) {

    var totalInsert = 0.0
    var haveRepaymentAmount = 0.0
    var payInterest = 0.0
    for (issue in 0..repaymentPlan.size-1){
        totalInsert += repaymentPlan.get(issue).repaymentInterest
        if (issue<payTimes){
            haveRepaymentAmount += repaymentPlan.get(issue).repaymentPrincipal
            payInterest += repaymentPlan.get(issue).repaymentInterest
            Log.i("kfzx-wzh","第{$issue}期：日期是{${repaymentPlan.get(issue).repaymentDate}}，已还本金{$haveRepaymentAmount}元，已还利息{$payInterest}元")
        }
    }


    val monthRate: Double = rate / (100 * 12) //月利率
    val preLoan: Double =
        principal * monthRate * Math.pow(
            1 + monthRate,
            months.toDouble()
        ) / (Math.pow(1 + monthRate, months.toDouble()) - 1) //每月还款金额



    //总还款额
    result.totalAmount = principal+totalInsert
    //利息总额
    result.interestAmount = totalInsert
    //月还款
    result.repaymentMonth = preLoan
    //已还本金
    result.haveRepaymentAmount = haveRepaymentAmount
    //已还利息
    result.haveInterestAmount = payInterest
    //已还总额
    result.haveTotalAmount = result.haveRepaymentAmount + result.haveInterestAmount
    //剩余本金
    result.surplusAmount = result.loanAmount - result.haveRepaymentAmount
    //一次性还款利息
    result.aheadInterest = result.surplusAmount * monthRate
    //一次性还款总额
    result.aheadRepaymentAmount = result.aheadInterest + result.surplusAmount
    //节省利息
    val saveInterest = result.interestAmount - result.haveInterestAmount - result.aheadInterest
    result.saveInterest = saveInterest
}


/**
 * 等额本金
 */
fun calcEPLoan(
    principal: Double,
    months: Int,
    rate: Double,
    payTimes: Int,
    result: LoanResultFullAmount,
    repaymentPlan: List<RepaymentPlan>,
    date:String,
) {

    var totalInsert = 0.0
    var haveRepaymentAmount = 0.0
    var payInterest = 0.0
    for (issue in 0..repaymentPlan.size-1){
        totalInsert += repaymentPlan.get(issue).repaymentInterest
        if (issue<payTimes){
            haveRepaymentAmount += repaymentPlan.get(issue).repaymentPrincipal
            payInterest += repaymentPlan.get(issue).repaymentInterest
            Log.i("kfzx-wzh","第{$issue}期：日期是{${repaymentPlan.get(issue).repaymentDate}}，已还本金{$haveRepaymentAmount}元，已还利息{$payInterest}元")
        }
    }

    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    val totalMoney = principal + totalInsert //还款总额

    result.interestAmount = totalInsert
    result.repaymentFirstMonth = firstMonth
    result.totalAmount = totalMoney


    //已还本金
    result.haveRepaymentAmount = haveRepaymentAmount
    //已还利息
    result.haveInterestAmount = payInterest
    //已还总额
    result.haveTotalAmount = result.haveRepaymentAmount + result.haveInterestAmount
    //剩余本金
    val surplusAmount = principal - result.haveRepaymentAmount
    result.surplusAmount = surplusAmount
    //一次性还款利息
    val aheadInterest = result.surplusAmount * monthRate
    result.aheadInterest = aheadInterest
    //一次性还款总额
    result.aheadRepaymentAmount = result.surplusAmount+result.aheadInterest
    result.saveInterest = result.interestAmount - payInterest - result.aheadInterest
}


fun getRepaymentSubMonth(first: String, advance: String): Int {
    var yearFirst = first.substring(0, 4).toInt()
    var monthFirst = first.substring(6, 7).toInt()
    var yearAd = advance.substring(0, 4).toInt()
    var monthAd = advance.substring(6, 7).toInt()
    return (yearAd - yearFirst) * 12 + (monthAd - monthFirst)
}
