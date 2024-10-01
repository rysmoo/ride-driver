package com.rideke.driver.common.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rideke.driver.R
import com.rideke.driver.common.model.Support
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.home.splash.SplashActivity.Companion.checkVersionModel
import kotlinx.android.synthetic.main.activity_support_common.*
import java.util.*
import javax.inject.Inject


class SupportActivityCommon : CommonActivity(), SupportAdapter.OnClickListener {
    @BindView(R.id.rv_support_list)
    lateinit var rvSupportList: RecyclerView

    var supportList: ArrayList<Support> = ArrayList()

    @OnClick(R.id.arrow)
    fun onBack() {
        onBackPressed()
    }

    @Inject
    lateinit var commonMethods: CommonMethods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_common)

        AppController.getAppComponent().inject(this)
        ButterKnife.bind(this)
        commonMethods.setheaderText(resources.getString(R.string.support),common_header)
        //commonMethods.imageChangeforLocality(this, arrow)
        initViews()


    }

    private fun initViews() {
        supportList.clear()
        supportList.addAll(checkVersionModel.support)
        rvSupportList.adapter = SupportAdapter(this, supportList, this)
    }

    override fun onClick(pos: Int) {9
        if (checkVersionModel.support[pos].id == 1) {
            onClickWhatsApp(checkVersionModel.support[pos].link)
        }  else {
            redirectWeb(checkVersionModel.support[pos].link)
        }
    }

    private fun redirectWeb(link: String) {
        if (URLUtil.isValidUrl(link) || Patterns.WEB_URL.matcher(link).matches()) {
            val redirectLink: String = if (!(link.contains("https://") || link.contains("http://"))) {
                "http://$link"
            } else {
                link
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(redirectLink)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }else{
            Toast.makeText(this,resources.getString(R.string.not_valid_data), Toast.LENGTH_SHORT).show()
        }
    }


    fun onClickWhatsApp(phoneNumberWithCountryCode: String) {
        //val phoneNumberWithCountryCode = "+9112345678"
        val message = ""

        startActivity(
                Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format(getString(R.string.whatsapp_url), phoneNumberWithCountryCode, message))
                )
        )

    }
}