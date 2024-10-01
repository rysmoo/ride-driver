package com.rideke.driver.home.pushnotification

/**
 * @package com.cloneappsolutions.cabmedriver.home.pushnotification
 * @subpackage pushnotification model
 * @category MyFirebaseMessagingService
 * @author SMR IT Solutions
 *
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.PowerManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.helper.ManualBookingDialog
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonKeys
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.common.util.CommonMethods.Companion.DebuggableLogE
import com.rideke.driver.home.datamodel.TripDetailsModel
import com.rideke.driver.trips.RequestAcceptActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


/* ************************************************************
                MyFirebaseMessagingService
Its used to get the pushnotification FirebaseMessagingService function
*************************************************************** */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var gson: Gson


    override fun onCreate() {
        super.onCreate()
        AppController.getAppComponent().inject(this)
        setLocale()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        DebuggableLogE(TAG, "From: " + remoteMessage.from)
        wakeUpScreen()

        Log.i(TAG, "onMessageReceived: data=${remoteMessage.data}")
        Log.i(TAG, "onMessageReceived: notification=${remoteMessage.notification}")
        Log.i(TAG, "onMessageReceived: notification?.body=${remoteMessage.notification?.body}")
        Log.i(TAG, "onMessageReceived: notification?.title=${remoteMessage.notification?.title}")

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            DebuggableLogE(TAG, "Data Payload: " + remoteMessage.data.toString())

            try {
                val json = JSONObject(remoteMessage.data.toString())
                commonMethods.handleDataMessage(json,this)
//                handleDataMessage(json)
                if (remoteMessage.notification != null) {
                    DebuggableLogE(TAG, "Notification Body: " + remoteMessage.notification?.body)
                }

            } catch (e: Exception) {
                DebuggableLogE(TAG, "Exception: " + e.message)
            }

        }


    }

    @SuppressLint("InvalidWakeLockTag")
    private fun wakeUpScreen() {
        val pm = this.getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        Log.e("screen on......", "" + isScreenOn)
        if (!isScreenOn) {
            val wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "MyLock")
            wl.acquire(10000)
            val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
            wl_cpu.acquire(10000)
        }
    }


    /*
     *  Handle push notification message
     */

    /*fun handleDataMessage(json: JSONObject) {
        if (json.getJSONObject("custom").has("id")) {
            val notificationId = json.getJSONObject("custom").getString("id")
            if (sessionManager.notificationID.equals(notificationId))
                return
            else
                sessionManager.notificationID = json.getJSONObject("custom").getString("id")
        }

        DebuggableLogE(TAG, "push json: $json")
        val TripStatus = sessionManager.tripStatus
        val DriverStatus = sessionManager.driverStatus
        val UserId = sessionManager.accessToken
        try {
            val mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.real_time_db))
            mFirebaseDatabase!!.child(FirebaseDbKeys.Notification).child(sessionManager.userId!!).removeValue()

            if (json.getJSONObject("custom").has("request_id")) {
                val requestId = json.getJSONObject("custom").getString("request_id")
                if (sessionManager.requestId.equals(requestId))
                    return
                else
                    sessionManager.requestId = json.getJSONObject("custom").getString("request_id")
            }
            DebuggableLogE(TAG, "push json: $json")

            *//*
         *  Handle push notification and broadcast message to other activity
         *//*
            sessionManager.pushJson = json.toString()

            if (!NotificationUtils.isAppIsInBackground(this)) {
                //CommonMethods.DebuggableLogE(TAG, "IF: " + json.toString());
                // app is in foreground, broadcast the push message


                try {
                    val json = JSONObject(json.toString())
                    var requestId = ""
                    var isPool = false
                    val jsonObject = JSONObject(json.toString())
                    if (jsonObject.getJSONObject("custom").has("ride_request")) {
                        isPool = json.getJSONObject("custom").getJSONObject("ride_request").getBoolean("is_pool")
                        requestId = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id")
                    }


                    if (!sessionManager.requestId.equals(requestId)) {
                        sessionManager.requestId = requestId


                        if (json.getJSONObject("custom").has("ride_request")) {
                            if (DriverStatus == "Online"
                                && (TripStatus == null || TripStatus == CommonKeys.TripDriverStatus.EndTrip || TripStatus == "" || isPool)
                                && UserId != null) {
                                //  Intent rider=new Intent(getApplicationContext(), Riderrating.class);

                                val requstreceivepage = Intent(this, RequestReceiveActivity::class.java)
                                requstreceivepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                requstreceivepage.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                requstreceivepage.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

                                startActivity(requstreceivepage)

                                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                                pushNotification.putExtra("message", "message")
                                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                                //startActivity(rider);
                            }
                        }

                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (json.getJSONObject("custom").has("cancel_trip")) {

                    //val tripriders = json.getJSONObject("custom").getJSONObject("cancel_trip").getJSONArray("trip_riders")
                    sessionManager.isTrip = false
                    sessionManager.clearTripID()
                    sessionManager.clearTripStatus()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)

                    val dialogs = Intent(this, CommonDialog::class.java)
                    println("Langugage " + resources.getString(R.string.yourtripcanceledrider))
                    sessionManager.dialogMessage = resources.getString(R.string.yourtripcanceledrider)
                    dialogs.putExtra("message", resources.getString(R.string.yourtripcanceledrider))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 1)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(dialogs)


                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    val riderProfile = json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image")
                    sessionManager.riderProfilePic = riderProfile

                    val dialogs = Intent(this, CommonDialog::class.java)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 2)
                    startActivity(dialogs)

                } else if (json.getJSONObject("custom").has("custom_message")) {
                    val notificationUtils = NotificationUtils(this)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        notificationUtils.playNotificationSound()
                    }
                    val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                    val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")
                    notificationUtils.generateNotification(this, message, title)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"), this)
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    initSinchService()
                } else if (json.getJSONObject("custom").has("chat_notification")) {

                    val tripId = json.getJSONObject("custom").getJSONObject("chat_notification").getString("trip_id")

                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    //String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
                    if (!ActivityChat.isOnChat || !sessionManager.tripId!!.equals(tripId)) {
                        *//*  val notificationIntent = Intent(this, ActivityChat::class.java)
                      notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                      notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())

                      val notificationUtils = NotificationUtils(this)
                      notificationUtils.playNotificationSound()
                      val message = json.getJSONObject("custom").getJSONObject("chat_notification").getString("message_data")
                      val title = getString(R.string.app_name)
                      println("ChatNotification : Driver" + message)
                      notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null)
*//*
                        val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                        pushNotification.putExtra("message", "message")
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                        //startActivity(rider);
                    }
                } else {
                    DebuggableLogE("Ride Request Received", "unable to process")
                }


            } else {
                DebuggableLogE(TAG, "ELSE: $json")

                // app is in background, show the notification in notification tray
                if (json.getJSONObject("custom").has("ride_request")) {

                    val isPool = json.getJSONObject("custom").getJSONObject("ride_request").getBoolean("is_pool")

                    if (DriverStatus == "Online"
                        && (TripStatus == null || TripStatus == CommonKeys.TripDriverStatus.EndTrip || TripStatus == "" || isPool)
                        && UserId != null) {


                        try {
                            val json = JSONObject(json.toString())
                            var requestId = ""


                            val jsonObject = JSONObject(json.toString())
                            if (jsonObject.getJSONObject("custom").has("ride_request")) {
                                requestId = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id")
                            }
                            println("Get Session requestId " + sessionManager.requestId)
                            println("Get requestId " + requestId)
                            if (!sessionManager.requestId.equals(requestId)) {
                                sessionManager.requestId = requestId

                                var msg = json.getJSONObject("custom").getJSONObject("ride_request").getString("title")

                                if (jsonObject.getJSONObject("custom").has("ride_request")) {
                                    msg = resources.getString(R.string.trip_request)
                                }

                                val endtime = jsonObject.getJSONObject("custom").getString("end_time")

                                val duration = commonMethods.difference(commonMethods.getCurrentTimeIntoLong(), endtime.toLong())
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && NotificationUtils.isAppIsInBackground(this)) {
                                    CommonKeys.isRideRequest = true
                                    Constants.RequestEndTime = json.getJSONObject("custom").getString("end_time")
                                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                    val intent = Intent(this, RequestReceiveActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    //notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())
                                    val notificationUtils = NotificationUtils(this)
                                    notificationUtils.playNotificationSound()
                                    val pm: PowerManager = getSystemService(POWER_SERVICE) as PowerManager
                                    val isScreenOn: Boolean = if (Build.VERSION.SDK_INT >= 20) pm.isInteractive() else pm.isScreenOn() // check if screen is on

                                    if (!isScreenOn) {
                                        val wl: PowerManager.WakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock")
                                        wl.acquire(3000) //set your time in milliseconds
                                    }
                                    val title = getString(R.string.app_name)
                                    notificationUtils.showNotificationMessage(title, msg, timeStamp, intent, null, duration)
                                    val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                                    pushNotification.putExtra("message", "message")
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                                } else {
                                    val notificationUtils = NotificationUtils(this)
                                    notificationUtils.playNotificationSound()
                                    notificationUtils.generateNotification(this, "", msg)
                                    sessionManager.isDriverAndRiderAbleToChat = false
                                    CommonMethods.stopFirebaseChatListenerService(this)
                                    val intent = Intent(this, RequestReceiveActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }

                            }


                        } catch (e: Exception) {

                        }


                    }

                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID()
                    sessionManager.clearTripStatus()
                    *//*val tripriders = json.getJSONObject("custom").getJSONObject("cancel_trip").getJSONArray("trip_riders")
                    if (tripriders.length() > 0) {
                        sessionManager.isTrip = true
                    } else {
                        sessionManager.isTrip = false
                    }*//*
                    sessionManager.isTrip = false
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && NotificationUtils.isAppIsInBackground(this)) {
                        AddFirebaseDatabase().removeNodesAfterCompletedTrip(this)
                        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())
                        val notificationUtils = NotificationUtils(this)
                        notificationUtils.playNotificationSound()
                        val message = resources.getString(R.string.yourtripcanceledrider)
                        val title = getString(R.string.app_name)
                        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, null, 0L)
                        val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                        pushNotification.putExtra("message", "message")
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                    } else {
                        val dialogs = Intent(this, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.yourtripcanceledrider))
                        dialogs.putExtra("type", 1)
                        dialogs.putExtra("status", 1)
                        startActivity(dialogs)
                    }

                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    val riderProfile = json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image")
                    sessionManager.riderProfilePic = riderProfile


                    AddFirebaseDatabase().removeNodesAfterCompletedTrip(this)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && NotificationUtils.isAppIsInBackground(this)) {
                        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())
                        val notificationUtils = NotificationUtils(this)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            notificationUtils.playNotificationSound()
                        val message = resources.getString(R.string.paymentcompleted)
                        val title = getString(R.string.app_name)
                        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, null, 0L)
                        val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                        pushNotification.putExtra("message", "message")
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                    } else {
                        val dialogs = Intent(this, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                        dialogs.putExtra("type", 1)
                        dialogs.putExtra("status", 2)
                        startActivity(dialogs)
                    }

                } else if (json.getJSONObject("custom").has("custom_message")) {
                    val notificationUtils = NotificationUtils(this)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        notificationUtils.playNotificationSound()
                    }
                    val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                    val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")
                    notificationUtils.generateNotification(this, message, title)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"), this)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"), this)
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    CommonKeys.keyCaller = json.getJSONObject("custom").getJSONObject("user_calling").getString("title")
                    initSinchService()
                } else if (json.getJSONObject("custom").has("chat_notification")) {


                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    //String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());


                    val notificationIntent = Intent(this, ActivityChat::class.java)
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())

                    sessionManager.chatJson = json.toString()
                    val notificationUtils = NotificationUtils(this)
                    notificationUtils.playNotificationSound()
                    val message = json.getJSONObject("custom").getJSONObject("chat_notification").getString("message_data")
                    //val title = getString(R.string.app_name)
                    val title = json.getJSONObject("custom").getJSONObject("chat_notification").getString("user_name")
                    println("ChatNotification : Driver" + message)
                    notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null, 0L)


                } else {
                    DebuggableLogE("Ride Request Received", "unable to process")
                }

            }
        } catch (e: JSONException) {
            DebuggableLogE(TAG, "Json Exception: " + e.message)
            e.printStackTrace()
        } catch (e: Exception) {
            DebuggableLogE(TAG, "Exception: " + e.message)
        }
    }*/


    /////////////////////////////////////////////////////////////////////
    
    
    /*private fun handleDataMessage(json: JSONObject , context: Context) {
        DebuggableLogE(TAG, "push json: $json")
        val TripStatus = sessionManager.tripStatus
        val DriverStatus = sessionManager.driverStatus
        val UserId = sessionManager.accessToken
        try {

            *//*
             *  Handle push notification and broadcast message to other activity
             *//*
            sessionManager.pushJson = json.toString()

            if (!NotificationUtils.isAppIsInBackground(context)) {
                //CommonMethods.DebuggableLogE(TAG, "IF: " + json.toString());
                // app is in foreground, broadcast the push message


                try {
                    val json = JSONObject(json.toString())
                    var requestId = ""
                    val isPool = json.getJSONObject("custom").getJSONObject("ride_request").getBoolean("is_pool")


                    val jsonObject = JSONObject(json.toString())
                    if (jsonObject.getJSONObject("custom").has("ride_request")) {
                        requestId = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id")
                    }


                    if (!sessionManager.requestId.equals(requestId)) {
                        sessionManager.requestId = requestId


                        if (json.getJSONObject("custom").has("ride_request")) {
                            if (DriverStatus == "Online"
                                    && (TripStatus == null || TripStatus == CommonKeys.TripDriverStatus.EndTrip || TripStatus == "" || isPool)
                                    && UserId != null) {
                                //  Intent rider=new Intent(getApplicationContext(), Riderrating.class);


                                val requstreceivepage = Intent(context, RequestReceiveActivity::class.java)
                                requstreceivepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                requstreceivepage.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(requstreceivepage)

                                *//* val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                                 pushNotification.putExtra("message", "message")
                                 LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)*//*
                                //startActivity(rider);
                            }
                        }

                    }


                } catch (e: Exception) {

                }






                if (json.getJSONObject("custom").has("cancel_trip")) {

                    val tripriders=json.getJSONObject("custom").getJSONObject("cancel_trip").getJSONArray("trip_riders")
                    if(tripriders.length()>0){
                        sessionManager.isTrip=true
                    }else{
                        sessionManager.isTrip=false
                    }
                    sessionManager.clearTripID()
                    sessionManager.clearTripStatus()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(context)
                    val dialogs = Intent(context, CommonDialog::class.java)
                    println("Langugage " + resources.getString(R.string.yourtripcanceledrider))
                    sessionManager.dialogMessage = resources.getString(R.string.yourtripcanceledrider)
                    dialogs.putExtra("message", resources.getString(R.string.yourtripcanceledrider))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 1)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(dialogs)


                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    val riderProfile = json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image")
                    sessionManager.riderProfilePic = riderProfile

                    val dialogs = Intent(context, CommonDialog::class.java)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 2)
                    startActivity(dialogs)

                } else if (json.getJSONObject("custom").has("custom_message")) {
                    val notificationUtils = NotificationUtils(context)
                    notificationUtils.playNotificationSound()

                    val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                    val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")

                    notificationUtils.generateNotification(context, message, title)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"),context)
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    initSinchService()
                } else if (json.getJSONObject("custom").has("chat_notification")) {

                    val tripId = json.getJSONObject("custom").getJSONObject("chat_notification").getString("trip_id")

                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    //String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
                    if (!ActivityChat.isOnChat || !sessionManager.tripId!!.equals(tripId)) {
                        *//*  val notificationIntent = Intent(context, ActivityChat::class.java)
                          notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                          notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())

                          val notificationUtils = NotificationUtils(context)
                          notificationUtils.playNotificationSound()
                          val message = json.getJSONObject("custom").getJSONObject("chat_notification").getString("message_data")
                          val title = getString(R.string.app_name)
                          println("ChatNotification : Driver" + message)
                          notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null)
  *//*
                        val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                        pushNotification.putExtra("message", "message")
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                        //startActivity(rider);
                    }
                } else {
                    DebuggableLogE("Ride Request Received", "unable to process")
                }


            } else {
                DebuggableLogE(TAG, "ELSE: $json")

                // app is in background, show the notification in notification tray
                if (json.getJSONObject("custom").has("ride_request")) {

                    val isPool = json.getJSONObject("custom").getJSONObject("ride_request").getBoolean("is_pool")

                    if (DriverStatus == "Online"
                            && (TripStatus == null || TripStatus == CommonKeys.TripDriverStatus.EndTrip || TripStatus == "" || isPool)
                            && UserId != null) {


                        try {
                            val json = JSONObject(json.toString())
                            var requestId = ""


                            val jsonObject = JSONObject(json.toString())
                            if (jsonObject.getJSONObject("custom").has("ride_request")) {
                                requestId = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id")
                            }

                            if (!sessionManager.requestId.equals(requestId)) {
                                sessionManager.requestId = requestId
                                val title = json.getJSONObject("custom").getJSONObject("ride_request").getString("title")

                                val notificationUtils = NotificationUtils(context)
                                notificationUtils.playNotificationSound()
                                notificationUtils.generateNotification(context, "", title)
                                sessionManager.isDriverAndRiderAbleToChat = false
                                CommonMethods.stopFirebaseChatListenerService(context)
                                val intent = Intent(this, RequestReceiveActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        } catch (e: Exception) {

                        }
                    }
                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID()
                    sessionManager.clearTripStatus()
                    val tripriders=json.getJSONObject("custom").getJSONObject("cancel_trip").getJSONArray("trip_riders")
                    if(tripriders.length()>0){
                        sessionManager.isTrip=true
                    }else{
                        sessionManager.isTrip=false
                    }
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(context)
                    val dialogs = Intent(context, CommonDialog::class.java)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    dialogs.putExtra("message", resources.getString(R.string.yourtripcanceledrider))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 1)
                    startActivity(dialogs)


                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    val riderProfile = json.getJSONObject("custom").getJSONObject("trip_payment").getString("rider_thumb_image")
                    sessionManager.riderProfilePic = riderProfile

                    val dialogs = Intent(context, CommonDialog::class.java)
                    dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                    dialogs.putExtra("type", 1)
                    dialogs.putExtra("status", 2)
                    startActivity(dialogs)

                }*//* else if (json.getJSONObject("custom").has("custom_message")) {
                    val notificationUtils = NotificationUtils(context)
                    notificationUtils.playNotificationSound()
                    val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                    val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")

                    notificationUtils.generateNotification(context, message, title)
                }*//* else if (json.getJSONObject("custom").has("manual_booking_trip_booked_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.bookedInfo, json.getJSONObject("custom").getJSONObject("manual_booking_trip_booked_info"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_reminder")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.reminder, json.getJSONObject("custom").getJSONObject("manual_booking_trip_reminder"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_canceled_info")) {
                    manualBookingTripBookedInfo(CommonKeys.ManualBookingPopupType.cancel, json.getJSONObject("custom").getJSONObject("manual_booking_trip_canceled_info"),context)
                } else if (json.getJSONObject("custom").has("manual_booking_trip_assigned")) {
                    manualBookingTripStarts(json.getJSONObject("custom").getJSONObject("manual_booking_trip_assigned"),context)
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    initSinchService()
                } else if (json.getJSONObject("custom").has("chat_notification")) {


                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    //String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());


                    val notificationIntent = Intent(context, ActivityChat::class.java)
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())

                    sessionManager.chatJson = json.toString()
                    val notificationUtils = NotificationUtils(context)
                    notificationUtils.playNotificationSound()

                    val message = json.getJSONObject("custom").getJSONObject("chat_notification").getString("message_data")
                    //val title = getString(R.string.app_name)
                    val title = json.getJSONObject("custom").getJSONObject("chat_notification").getString("user_name")
                    println("ChatNotification : Driver" + message)
                    notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null, 0L)


                } else {
                    DebuggableLogE("Ride Request Received", "unable to process")
                }

            }
        } catch (e: JSONException) {
            DebuggableLogE(TAG, "Json Exception: " + e.message)
            e.printStackTrace()
        } catch (e: Exception) {
            DebuggableLogE(TAG, "Exception: " + e.message)
        }

    }*/

    private fun initSinchService() {
       /* if (!sessionManager.accessToken.isNullOrEmpty()) {
            startService(Intent(this, CabmeSinchService::class.java))
        }*/
    }



    fun manualBookingTripBookedInfo(manualBookedPopupType: Int, jsonObject: JSONObject , context: Context) {
        var riderName = ""
        var riderContactNumber = ""
        var riderPickupLocation = ""
        var riderPickupDateAndTime = ""
        try {
            riderName = jsonObject.getString("rider_first_name") + " " + jsonObject.getString("rider_last_name")
            riderContactNumber = jsonObject.getString("rider_country_code") + " " + jsonObject.getString("rider_mobile_number")
            riderPickupLocation = jsonObject.getString("pickup_location")
            riderPickupDateAndTime = jsonObject.getString("date") + " - " + jsonObject.getString("time")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val dialogs = Intent(context, ManualBookingDialog::class.java)
        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_NAME, riderName)
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_CONTACT_NUMBER, riderContactNumber)
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_LOCATION, riderPickupLocation)
        dialogs.putExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_DATE_AND_TIME, riderPickupDateAndTime)
        dialogs.putExtra(CommonKeys.KEY_TYPE, manualBookedPopupType)
        startActivity(dialogs)

    }

    fun manualBookingTripStarts(jsonResp: JSONObject, context: Context) {


        val riderModel = gson.fromJson(jsonResp.toString(), TripDetailsModel::class.java)
        sessionManager.riderName = riderModel.riderDetails.get(0).name
        sessionManager.riderId = riderModel.riderDetails.get(0).riderId!!
        sessionManager.riderRating = riderModel.riderDetails.get(0).rating
        sessionManager.riderProfilePic = riderModel.riderDetails.get(0).profileImage
        sessionManager.bookingType = riderModel.riderDetails.get(0).bookingType
        sessionManager.tripId = riderModel.riderDetails.get(0).tripId.toString()
        sessionManager.subTripStatus = resources.getString(R.string.confirm_arrived)
        //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
        sessionManager.tripStatus = CommonKeys.TripDriverStatus.ConfirmArrived
        //sessionManager.paymentMethod = riderModel.paymentMode

        sessionManager.isDriverAndRiderAbleToChat = true
        CommonMethods.startFirebaseChatListenerService(this)


        /* if (!WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS)) {
             DebuggableLogE("locationupdate", "StartWork:")
             WorkerUtils.startWorkManager(CommonKeys.WorkKeyForUpdateGPS, CommonKeys.WorkTagForUpdateGPS, UpdateGPSWorker::class.java,this,sessionManager.driverStatus)
         }*/

        //  acceptedDriverDetails = new AcceptedDriverDetails(ridername, mobilenumber, profileimg, ratingvalue, cartype, pickuplocation, droplocation, pickuplatitude, droplatitude, droplongitude, pickuplongitude);
        //        mPlayer.stop();
        val requestaccept = Intent(context, RequestAcceptActivity::class.java)
        requestaccept.putExtra("riderDetails", riderModel)
        requestaccept.putExtra("tripstatus", resources.getString(R.string.confirm_arrived))
        requestaccept.putExtra(CommonKeys.KEY_IS_NEED_TO_PLAY_SOUND, CommonKeys.YES)
        requestaccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(requestaccept)
    }




    fun setLocale() {
        val lang = sessionManager.language

        if (lang != "") {
            val langC = sessionManager.languageCode
            val locale = Locale(langC)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(
                    config,
                    resources.displayMetrics
            )
        } else {
            sessionManager.language = "English"
            sessionManager.languageCode = "en"
        }


    }

    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}
