package com.itwzh.repaymentcalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.itwzh.repaymentcalc.binding.listener.MainListener
import com.itwzh.repaymentcalc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.listener = MainListener()
    }
}