package com.rideke.driver.trips

/**
 * @package com.cloneappsolutions.cabmedriver.home
 * @subpackage home
 * @category RiderContactActivity
 * @author SMR IT Solutions
 *
 */

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonKeys
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.common.views.CommonActivity
import com.rideke.driver.home.firebaseChat.ActivityChat
import kotlinx.android.synthetic.main.activity_payment.*
import javax.inject.Inject

/* ************************************************************
                      RiderContactActivity
Its used to get RiderContactActivity details
*************************************************************** */
class RiderContactActivity : CommonActivity() {

    lateinit @Inject
    var sessionManager: SessionManager
    lateinit @Inject
    var commonMethods: CommonMethods

    lateinit @BindView(R.id.mobilenumbertext)
    var mobilenumbertext: TextView
    lateinit @BindView(R.id.ridername)
    var ridername: TextView

    lateinit @BindView(R.id.ll_message)
    var llMessage: LinearLayout

    @OnClick(R.id.back)
    fun onBack() {
        onBackPressed()
    }

    @OnClick(R.id.callbutton)
    fun call() {
        try {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.CALL_PHONE
                    ),
                    1
                )
                return;
            } else {
                val intent =  Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobilenumbertext.getText().toString()));
                startActivity(intent);
            }
        } catch (e: Exception) {
            Log.i("TAGA", "callThisNo: Error=${e.localizedMessage}")
        }


    }


    @OnClick(R.id.ll_message)
    fun startChatActivity() {
        sessionManager.chatJson = ""

        startActivity(Intent(this, ActivityChat::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_contact)
        ButterKnife.bind(this)
        AppController.getAppComponent().inject(this)
        commonMethods.setheaderText(resources.getString(R.string.contact_C),common_header)
        ridername.text = intent.getStringExtra("ridername")
        mobilenumbertext.setText(getIntent().getStringExtra("mobile_number"))
        println("mobilenumbertext ${mobilenumbertext.text.toString()}")
        if (sessionManager.bookingType == CommonKeys.RideBookedType.manualBooking) {
            llMessage.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right)
    }

    companion object {

        val CALL = 0x2
    }
}
