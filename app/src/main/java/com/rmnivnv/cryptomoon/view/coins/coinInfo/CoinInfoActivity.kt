package com.rmnivnv.cryptomoon.view.coins.coinInfo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar

import com.rmnivnv.cryptomoon.R
import com.rmnivnv.cryptomoon.utils.ResourceProvider
import com.rmnivnv.cryptomoon.utils.app
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_coin_info.*
import javax.inject.Inject

class CoinInfoActivity : AppCompatActivity(), ICoinInfo.View {

    val component by lazy { app.component.plus(CoinInfoModule(this)) }
    @Inject lateinit var presenter: ICoinInfo.Presenter
    @Inject lateinit var resProvider: ResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_info)
        component.inject(this)
        setupToolbar()
        presenter.onCreate(component, intent.extras)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setLogo(url: String) {
        Picasso.with(this)
                .load(url)
                .into(coin_info_logo)
    }

    override fun setMainPrice(price: String) {
        coin_info_main_price.text = price
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}