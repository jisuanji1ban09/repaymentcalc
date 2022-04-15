package com.itwzh.repaymentcalc.utlis

import com.itwzh.repaymentcalc.model.LoanResultAdvance
import com.itwzh.repaymentcalc.model.RepaymentPlan

fun getLoanResultAdvance(
    amount: Double,
    months: Int,
    rate: Double,
    isEP: Boolean,
    firstDate: String,
    advanceDate: String,
    advanceAmount: Double,
    isAdvance: Boolean
): LoanResultAdvance {
    var result = LoanResultAdvance()
    result.loanAmount = amount
    result.months = months
    result.isEP = isEP
    result.repaymentLastDate = getEndDate(firstDate, months)
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    if (isAdvance) result.advanceType = "缩短还款时间" else result.advanceType = "减少每月金额"
    val repaymentMonths = getRepaymentSubMonth(firstDate, advanceDate);
    result.repaymentNormalMonth = repaymentMonths
    result.advanceMonths = months - repaymentMonths - 1
    val firstRepaymentPlan = getRepaymentPlan(amount=amount,months = months,rate=rate,date = firstDate,isEP = isEP);
    val advanceRepaymentPlan = getAdvanceRepaymentPlan(amount=amount,months=months,rate=rate,isEP=isEP,firstDate = firstDate,repaymentAmount = advanceAmount,isAdvance=isAdvance,repaymentDate = advanceDate)
    getAdvanceLoanResult(firstRepaymentPlan,advanceRepaymentPlan,result,amount,isEP,repaymentMonths,firstDate,months)
    return result;
}

fun getAdvanceLoanResult(
    first: List<RepaymentPlan>, //原还款计划
    advance: List<RepaymentPlan>, //新还款计划
    result: LoanResultAdvance, //还款计算器结果
    amount: Double, // 贷款金额
    isEP: Boolean, //是否是等额本金
    repaymentMonths: Int, //已还期数, firstDate: kotlin.String){}, firstDate: kotlin.String){}
    firstDate: String, //第一次还款日期
    months: Int,//贷款期限
) {
    //原总利息
    var firstTotalInsert = 0.0
    //已还利息
    var haveRepayInsert = 0.0
    //已还本金
    var haveRepayPrincipal= 0.0

    for (issue in 0..first.size-1){
        firstTotalInsert += first.get(issue).repaymentInterest
        if (issue<repaymentMonths){
            haveRepayInsert +=first.get(issue).repaymentInterest
            haveRepayPrincipal += first.get(issue).repaymentPrincipal
        }
    }
    //原还款总额
    result.totalAmount = firstTotalInsert+amount
    //原利息总额
    result.interestTotal = firstTotalInsert
    //等额本息每月还款 等额本金第一月还款
    if (isEP) result.repaymentFirstMonth = first.get(0).repaymentAmount else result.repaymentMonth = first.get(0).repaymentAmount
    //已还本金 repaidAmount
    result.repaidAmount = haveRepayPrincipal
    //已还利息  repaidInterest
    result.repaidInterest = haveRepayInsert
    //已还总额
    result.repaidTotal = result.repaidAmount+result.repaidInterest

    //新利息总额
    var advanceTotalInsert = 0.0
    //新本金总额
    var advanceTotalPrincipal = 0.0;
    for (issue in repaymentMonths..advance.size-1){
        advanceTotalInsert += advance.get(issue).repaymentInterest
        advanceTotalPrincipal += advance.get(issue).repaymentPrincipal
    }
    //剩余本金
    result.surplusAmount = advanceTotalPrincipal
    //提前还款后总利息 新利息 + 已还利息
    result.advanceInterestTotal = advanceTotalInsert + result.repaidInterest
    //新还款总额 = 新利息+新本金
    result.surplusTotalAmount = result.loanAmount + result.advanceInterestTotal
    //提前还款后贷款期限
    result.advanceMonths = advance.size
    //新最后还款日期
    result.advanceLastDate = getEndDate(firstDate, result.advanceMonths)
    if (isEP) result.advanceRepaymentFirstMonth = advance.get(repaymentMonths+1).repaymentAmount else result.advanceRepaymentMonth = advance.get(repaymentMonths+1).repaymentAmount

    //缩短的月份
    result.reduceMonths = first.size-advance.size
    //节省利息
    result.reduceInterest = result.interestTotal - result.advanceInterestTotal


}

