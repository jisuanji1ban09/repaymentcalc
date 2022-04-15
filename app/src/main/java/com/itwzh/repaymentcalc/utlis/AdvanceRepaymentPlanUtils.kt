package com.itwzh.repaymentcalc.utlis

import android.util.Log
import com.itwzh.repaymentcalc.model.RepaymentPlan
import java.math.BigDecimal

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
        var excessInterest = if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded
        if (isEP) { //等额本金
            val amountEP = getAmountEP(amount, months, rateOfMonth, issue, excessInterest)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!!
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
            plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
        } else {  //等额本息
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
    if (isAdvance) { //减少还款金额
        val surplusIssue = months - normalEndMonth - 1
        if (isEP) {//等额本金
            //先计算提前还款当期的还款计划
            //计算提前还款当期还款
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            var excessInterest =
                if (naturalMonth == 1) 0.0 else mData.get(naturalMonth - 2).interestNotIncluded
            val amountEP = getAmountEP(amount, months, rateOfMonth, naturalMonth, excessInterest)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!! + repaymentAmount
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!! - repaymentAmount
            plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
            mData.add(plan)
            //剩余的还款计划 比如还款当期是第15期，需要从16期重新计算
            //剩余还款金额
            val susurplusAmount = mData.last().surplusPrincipal

            for (issue in naturalMonth + 1..months) {
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                var excessInterest =
                    if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded

                val amountEP =
                    getAmountEP(
                        susurplusAmount,
                        surplusIssue,
                        rateOfMonth,
                        issue - naturalMonth,
                        excessInterest
                    )
                plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
                plan.repaymentInterest = amountEP.get("repaymentInterest")!!
                plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
                mData.add(plan)
            }


        } else {//等额本息
            //先计算提前还款当期的还款计划
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            var excessInterest =
                if (naturalMonth == 1) 0.0 else mData.get(naturalMonth - 2).interestNotIncluded
            val amountEPAI = getAmountEPAI(
                amount,
                months,
                rateOfMonth,
                if (mData.size > 0) mData.last().surplusPrincipal else amount,
                excessInterest
            )
            plan.repaymentAmount = amountEPAI.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!! + repaymentAmount
            plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!! - repaymentAmount
            plan.interestNotIncluded = amountEPAI.get("interestNotIncluded")!!
            mData.add(plan)

            //剩余还款金额
            val susurplusAmount = mData.last().surplusPrincipal
            for (issue in naturalMonth + 1..months) {

                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                var excessInterest =
                    if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded
                val amountEPAI = getAmountEPAI(
                    susurplusAmount,
                    surplusIssue,
                    rateOfMonth,
                    mData.last().surplusPrincipal,
                    excessInterest
                )

                plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
                plan.interestNotIncluded = amountEPAI.get("interestNotIncluded")!!
                mData.add(plan)
            }

        }
    } else { // 提前结束
        if (isEP) { //等额本金
            //计算提前还款当期还款
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            var excessInterest =
                if (naturalMonth == 1) 0.0 else mData.get(naturalMonth - 2).interestNotIncluded
            val amountEP = getAmountEP(amount, months, rateOfMonth, naturalMonth, excessInterest)
            plan.repaymentAmount = amountEP.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!! + repaymentAmount
            plan.repaymentInterest = amountEP.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEP.get("surplusPrincipal")!! - repaymentAmount
            plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
            mData.add(plan)
            //就算剩余待还金额
            var naturalS = mData.last().surplusPrincipal
            //月还本金
            val amountOfMonth =
                getAmountOfMonth(principal = amount, months = months, loanRate = rate, isEP = isEP)
            //计算最新还款时间
            val newIssue = getNewAdvanceEpMonth(amountOfMonth, repaymentAmount, months)
            val startIssue = mData.size + 1
            for (issue in startIssue..newIssue) {
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                var excessInterest =
                    if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded
                val amountEP =
                    getAmountEPAdvance(amountOfMonth, naturalS, rateOfMonth, excessInterest)
                plan.repaymentAmount = amountEP.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEP.get("repaymentPrincipal")!!
                plan.surplusPrincipal = amountEP.get("surplusPrincipal")!!
                plan.repaymentInterest = amountEP.get("repaymentInterest")!!
                plan.interestNotIncluded = amountEP.get("interestNotIncluded")!!
                mData.add(plan)
                naturalS = mData.last().surplusPrincipal
            }
        } else {//等额本息
            //计算一下提前还款月的还款计划
            var plan = RepaymentPlan()
            plan.repaymentDate = getRepaymentDate(firstDate, naturalMonth)
            plan.issueNumber = "第${naturalMonth}期"
            var excessInterest =
                if (naturalMonth == 1) 0.0 else mData.get(naturalMonth - 2).interestNotIncluded
            val amountEPAI = getAmountEPAI(
                amount,
                months,
                rateOfMonth,
                if (mData.size > 0) mData.last().surplusPrincipal else amount,
                excessInterest
            )
            plan.repaymentAmount = amountEPAI.get("repaymentAmount")!! + repaymentAmount
            plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!! + repaymentAmount
            plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
            plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!! - repaymentAmount
            plan.interestNotIncluded = amountEPAI.get("interestNotIncluded")!!
            mData.add(plan)

            //剩余还款额
            var naturalS = mData.last().surplusPrincipal
            // 每月还款金额
            val amountOfMonth =
                getAmountOfMonth(principal = amount, months = months, loanRate = rate, isEP = isEP)

            val lastMonths = getNewAdvanceNotEpMonth(
                amountOfMonth = amountOfMonth,
                naturalS = naturalS,
                rateOfMonth = rateOfMonth
            )

            for (issue in naturalMonth + 1..naturalMonth + lastMonths) {
                var plan = RepaymentPlan()
                plan.repaymentDate = getRepaymentDate(firstDate, issue)
                plan.issueNumber = "第${issue}期"
                var excessInterest =
                    if (issue == 1) 0.0 else mData.get(issue - 2).interestNotIncluded
                val amountEPAI = getAmountEPAIAdvance(
                    rateOfMonth,
                    amountOfMonth,
                    naturalS,
                    excessInterest
                )
                plan.repaymentAmount = amountEPAI.get("repaymentAmount")!!
                plan.repaymentPrincipal = amountEPAI.get("repaymentPrincipal")!!
                plan.repaymentInterest = amountEPAI.get("repaymentInterest")!!
                plan.surplusPrincipal = amountEPAI.get("surplusPrincipal")!!
                plan.interestNotIncluded = amountEPAI.get("interestNotIncluded")!!
                mData.add(plan)
                naturalS = mData.last().surplusPrincipal
            }
        }
    }
    return mData;
}

fun getNewAdvanceNotEpMonth(amountOfMonth: Double, naturalS: Double, rateOfMonth: Double): Int {
    val surplusMonths =
        Math.log(amountOfMonth / (amountOfMonth - naturalS * rateOfMonth)) / Math.log(1 + rateOfMonth)
    if (surplusMonths % 1 == 0.0) {
        return surplusMonths.toInt()
    } else {
        return surplusMonths.toInt() + 1
    }
}

fun getNaturalRepaymentMonth(firstDate: String, repaymentDate: String): Int {
    val firstYear = firstDate.substring(0, 4).toInt();
    val firstMonth = firstDate.substring(5, 7).toInt()
    val repaymentYear = repaymentDate.substring(0, 4).toInt()
    val repaymentMonth = repaymentDate.substring(5, 7).toInt()
    return (repaymentYear - firstYear) * 12 + (repaymentMonth - firstMonth) + 1
}


fun getAmountEPAdvance(
    amountOfMonth: Double,
    preAmount: Double,
    rate: Double,
    excessInterest: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    if (preAmount > amountOfMonth) {
        var surplusPrincipal = preAmount - amountOfMonth
        var repaymentInterest = preAmount * rate
        var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
        var interestNotIncluded = repaymentInterest - interset + excessInterest
        //如果额外利息大于0.01 当期多还0.01
        if (interestNotIncluded > 0.01) {
            interset += 0.01;
            interestNotIncluded -= 0.01
        }
        map.put("repaymentAmount", amountOfMonth + interset)
        map.put("repaymentPrincipal", amountOfMonth)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", surplusPrincipal)
        map.put("interestNotIncluded", interestNotIncluded)
    } else {
        var repaymentInterest = preAmount * rate
        var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
        var interestNotIncluded = repaymentInterest - interset + excessInterest
        //如果额外利息大于0.01 当期多还0.01
        if (interestNotIncluded > 0.01) {
            interset += 0.01;
            interestNotIncluded -= 0.01
        }
        map.put("repaymentAmount", preAmount + interset)
        map.put("repaymentPrincipal", preAmount)
        map.put("repaymentInterest", repaymentInterest)
        map.put("surplusPrincipal", 0.0)
        map.put("interestNotIncluded", interestNotIncluded)
    }
    return map;
}

fun getAmountEPAIAdvance(
    rate: Double,
    repaymentAmount: Double, //每月还款金额
    preAmount: Double,
    excessInterest: Double
): Map<String, Double> {
    var map: HashMap<String, Double> = HashMap()
    if (preAmount > repaymentAmount) {
        val repaymentInterest = preAmount * rate //利息
        var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
        var interestNotIncluded = repaymentInterest - interset + excessInterest
        //如果额外利息大于0.01 当期多还0.01
        if (interestNotIncluded > 0.01) {
            interset += 0.01;
            interestNotIncluded -= 0.01
        }
        val repaymentPrincipal = repaymentAmount - interset
        val surplusPrincipal = preAmount - repaymentPrincipal //剩余本金
        map.put("repaymentAmount", repaymentPrincipal + interset)
        map.put("repaymentPrincipal", repaymentPrincipal)
        map.put("repaymentInterest", interset)
        map.put("surplusPrincipal", surplusPrincipal)
        map.put("interestNotIncluded", interestNotIncluded)
    } else {
        val repaymentInterest = preAmount * rate //利息
        var interset = BigDecimal(repaymentInterest).setScale(2, BigDecimal.ROUND_DOWN).toDouble()
        var interestNotIncluded = repaymentInterest - interset + excessInterest
        //如果额外利息大于0.01 当期多还0.01
        if (interestNotIncluded > 0.01) {
            interset += 0.01;
            interestNotIncluded -= 0.01
        }
        map.put("repaymentAmount", preAmount + interset)
        map.put("repaymentPrincipal", preAmount)
        map.put("repaymentInterest", interset)
        map.put("surplusPrincipal", 0.0)
        map.put("interestNotIncluded", interestNotIncluded)
    }
    return map;
}

fun getAmountOfMonth(
    principal: Double,
    months: Int,
    loanRate: Double,
    isEP: Boolean,
): Double {
    if (isEP) {
        return principal / months
    } else {
        val monthRate: Double = loanRate / (100 * 12) //月利率
        val preLoan: Double =
            principal * monthRate * Math.pow(
                1 + monthRate,
                months.toDouble()
            ) / (Math.pow(1 + monthRate, months.toDouble()) - 1) //每月还款金额
        return preLoan
    }
}

fun getNewAdvanceEpMonth(amountOfMonth: Double, repaymentAmount: Double, months: Int): Int {
    val reduceMonths = repaymentAmount / amountOfMonth;
    if (reduceMonths % 1 == 0.0) {
        return months - reduceMonths.toInt()
    } else {
        return months - reduceMonths.toInt() + 1
    }
}