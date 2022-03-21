package com.itwzh.repaymentcalc.model

class LoanResultFullAmount(
    var totalAmount: Double = 0.0, //总还款额
    var loanAmount: Double = 0.0, //贷款总额
    var interestAmount: Double = 0.0, //利息总额
    var loanTimes: Int = 0, //贷款期限
    var repaymentMonth: Double = 0.0, //月还款
    var repaymentFirstMonth: Double = 0.0,//首月还款
    var repaymentType: String = "", //还款方式
    var repaymentLastDate: String = "",//最后还款日期
    var isEP: Boolean = false, //是否 是->等额本金 否->等额本息
    var repaymentNormalMonth: Int = 0, //已还期数
    var haveTotalAmount: Double = 0.0, //已还总额
    var haveRepaymentAmount: Double = 0.0, //已还本金
    var haveInterestAmount: Double = 0.0, //已还利息
    var surplusAmount: Double = 0.0,//剩余本金
    var aheadRepaymentAmount: Double = 0.0,//一次性还款额
    var aheadInterest :Double = 0.0, //一次性还款利息
    var saveInterest :Double = 0.0, // 节省利息
) {

}