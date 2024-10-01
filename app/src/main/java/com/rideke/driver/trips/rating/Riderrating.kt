package com.rideke.driver.trips.rating

/**
 * @package com.cloneappsolutions.cabmedriver.trips.rating
 * @subpackage rating
 * @category Riderrating
 * @author SMR IT Solutions
 *
 */

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.helper.CustomDialog
import com.rideke.driver.common.model.JsonResponse
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonKeys
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.common.util.RequestCallback
import com.rideke.driver.common.views.CommonActivity
import com.rideke.driver.home.MainActivity
import com.rideke.driver.home.datamodel.TripRatingResult
import com.rideke.driver.home.interfaces.ApiService
import com.rideke.driver.home.interfaces.ServiceListener
import kotlinx.android.synthetic.main.activity_riderrating.*
import javax.inject.Inject

/* ************************************************************
                Riderrating
Its used to get the rider rating details
*************************************************************** */

class Riderrating : CommonActivity(), ServiceListener {


    lateinit var dialog: AlertDialog

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var customDialog: CustomDialog

    @BindView(R.id.driver_comments)
    lateinit var ridercomments: EditText

    @BindView(R.id.profile_image1)
    lateinit var profile_image1: ImageView

    @BindView(R.id.button)
    lateinit var button: Button

    @BindView(R.id.tv_skip)
    lateinit var tvSkip: TextView


    protected var isInternetAvailable: Boolean = false
    private var riderrate: RatingBar? = null


    lateinit var user_thumb_image: String


    @OnClick(R.id.toplayout)
    fun onToplay() {
        onBackPressed()
    }

    @OnClick(R.id.arrow)
    fun skip() {
        onBackPressed()
    }


    @OnClick(R.id.button)
    fun onSubmit() {
        val rating = riderrate!!.rating
        val ratingstr = rating.toString()
        if ("0.0" != ratingstr) {
            var ridercomment = ridercomments.text.toString()
            ridercomment = ridercomment.replace("[\\t\\n\\r]".toRegex(), " ")

            if (isInternetAvailable) {
                submitRating(ratingstr, ridercomment)
            } else {
                commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))
            }
        } else {
            commonMethods.showMessage(this, dialog, resources.getString(R.string.error_msg_rating))
        }
    }

    @OnClick(R.id.tv_skip)
    fun skipRating() {
        val rating = riderrate!!.rating
        val ratingstr = rating.toString()

        if (isInternetAvailable) {
            var ridercomment = ridercomments.text.toString()
            ridercomment = ridercomment.replace("[\\t\\n\\r]".toRegex(), " ")
            submitRating(ratingstr, ridercomment)
        } else {
            commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riderrating)

        ButterKnife.bind(this)
        AppController.getAppComponent().inject(this)
        commonMethods.setButtonText(resources.getString(R.string.submit), common_button)
        dialog = commonMethods.getAlertDialog(this)

        /*
                   *   Internet connection check
                   */
        isInternetAvailable = commonMethods.isOnline(this)

        riderrate = findViewById<View>(R.id.driver_rating) as RatingBar
        val laydir = getString(R.string.layout_direction)
        /*if ("1" == laydir) {
            riderrate!!.gravity = RatingBar.Gravity.Right
        }*/

        ridercomments.clearFocus()

        /*
                   *   Rider pofile image
                   */
        val intent = intent
        if (intent.getStringExtra("imgprofile") != null && !TextUtils.isEmpty(intent.getStringExtra("imgprofile"))) {
            user_thumb_image = intent.getStringExtra("imgprofile").toString()
        } else {
            user_thumb_image = sessionManager.riderProfilePic!!
        }

        if (!TextUtils.isEmpty(user_thumb_image))
            Picasso.get().load(user_thumb_image)
                    .into(profile_image1)


        button.isEnabled = false
        button.background = ContextCompat.getDrawable(this@Riderrating, R.drawable.app_curve_button_yellow_disable)

        riderrate?.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                if (rating >= 1) {
                    button.isEnabled = true
                    button.background = ContextCompat.getDrawable(this@Riderrating, R.drawable.app_curve_button_yellow)
                } else {
                    button.isEnabled = false
                    button.background = ContextCompat.getDrawable(this@Riderrating, R.drawable.app_curve_button_yellow_disable)
                }
            }
        }
    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        if (jsonResp.isSuccess) {
            commonMethods.hideProgressDialog()
            val earningModel = gson.fromJson(jsonResp.strResponse, TripRatingResult::class.java)
            val invoiceModels = earningModel.invoice
            val bundle = Bundle()
            bundle.putSerializable("invoiceModels", invoiceModels)

            val main = Intent(applicationContext, PaymentAmountPage::class.java)
            main.putExtras(bundle)
            main.putExtra("AmountDetails", jsonResp.strResponse)
            startActivity(main)
            /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            finish()
            //onBackPressed();
        } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {

            commonMethods.hideProgressDialog()
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        commonMethods.hideProgressDialog()
    }

    /*
      *   Submit rating API called
      */
    fun submitRating(ratingstr: String, comments: String) {
        commonMethods.showProgressDialog(this@Riderrating)

        apiService.tripRating(sessionManager.tripId!!, ratingstr, comments, sessionManager.type!!, sessionManager.accessToken!!).enqueue(RequestCallback(this))
    }

    override fun onBackPressed() {
        CommonKeys.IS_ALREADY_IN_TRIP = true
        if (intent.getIntExtra("back", 0) == 1) {
            super.onBackPressed()
        } else {
            sessionManager.clearTripID()
            sessionManager.clearTripStatus()
            sessionManager.driverStatus = CommonKeys.DriverStatus.Online
            sessionManager.isDriverAndRiderAbleToChat = false
            CommonMethods.stopFirebaseChatListenerService(applicationContext)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            riderrate!!.rating = 0f
            ridercomments.setText("")
        }

    }
}

