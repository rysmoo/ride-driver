package com.rideke.driver.home.paymentstatement

/**
 * @package com.cloneappsolutions.cabmedriver.home.paymentstatement
 * @subpackage paymentstatement model
 * @category PayModel
 * @author SMR IT Solutions
 *
 */

import java.io.Serializable

/* ************************************************************
                PayModel
Its used to  PayModel get method
*************************************************************** */

class PayModel : Serializable {
    var type: String? = null
    //Detailed list space

    lateinit var tripDateTime: String
    lateinit var tripAmount: String
    lateinit var dailyTrip: String

    constructor() {

    }

    /*
    *  Driver pay list getter and setter
    */
    constructor(tripdatetime: String, tripamount: String, tripdaily: String) {
        this.tripDateTime = tripdatetime
        this.tripAmount = tripamount
        this.dailyTrip = tripdaily
    }

}
