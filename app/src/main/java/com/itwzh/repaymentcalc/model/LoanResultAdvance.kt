package com.itwzh.repaymentcalc.model

data class LoanResultAdvance(
    //旧有数据
    var totalAmount: Double = 0.0,//原还款总额
    var loanAmount: Double = 0.0, //贷款金额
    var interestTotal: Double = 0.0, //原还款总利息
    var months: Int = 0, //贷款期限
    var repaymentLastDate:String = "",//原最后还款日期
    var repaymentMonth: Double = 0.0, //等额本息每月还款
    var repaymentFirstMonth: Double = 0.0, //等额本金第一月还款
    var isEP :Boolean = false, //是否 是->等额本金 否->等额本息
    var repaymentType: String = "",//还款方式
    var isAdvance:Boolean = false,//是否缩短还款时间 是->缩短时间 否->减少还款金额
    var advanceType:String = "", //提前还贷方式

    //已还数据
    var repaidTotal :Double = 0.0,//已还总额
    var repaidAmount:Double = 0.0,//已还本金
    var repaidInterest:Double = 0.0,//已还利息
    var repaymentNormalMonth:Int = 0, //已还期限



    //剩余
    var surplusTotalAmount: Double = 0.0,//剩余还款总额
    var surplusAmount: Double = 0.0,//剩余本金
    var advanceInterestTotal: Double = 0.0, //提前还款后总利息
    var advanceMonths: Int = 0, //提前还款后贷款期限
    var advanceLastDate:String = "",//新最后还款日期
    var advanceRepaymentMonth: Double = 0.0, //还款后等额本息每月还款
    var advanceRepaymentFirstMonth: Double = 0.0, //还款后等额本金第一月还款

    //减少信息
    var reduceMonths:Int = 0,//缩短的月份
    var reduceInterest:Double = 0.0,//节省利息
) {

}
