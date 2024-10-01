package com.rideke.driver.home.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by SMR IT Solutions on 9/11/18.
 */

class EarningTripDetailsModel : Serializable {

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("day")
    @Expose
    var day: String? = null
    @SerializedName("daily_fare")
    @Expose
    var dailyFare: String? = null
}
