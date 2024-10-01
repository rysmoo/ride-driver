package com.rideke.driver.common.network

/**
 * @package com.cloneappsolutions.cabmedriver.common.network
 * @subpackage network
 * @category ConnectionDetector
 * @author SMR IT Solutions
 *
 */

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/*
* Check internet connection is available or not
*/
class ConnectionDetector(private val context: Context) {

    //for (int i = 0; i < info.length; i++) {
    val isConnectingToInternet: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivityManager.allNetworkInfo
            if (info != null) {
                for (i in 0..2) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
            return false
        }
}