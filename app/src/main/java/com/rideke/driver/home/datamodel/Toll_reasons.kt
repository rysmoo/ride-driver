package com.rideke.driver.home.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Toll_reasons {

    @SerializedName("toll_reasons")
    @Expose
    var extraFeeReason: ArrayList<ExtraFeeReason> = ArrayList<ExtraFeeReason>()
}
