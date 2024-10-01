package com.rideke.driver.home.interfaces;

import com.rideke.driver.common.model.JsonResponse;

/**
 * Created by SMR IT Solutions on 9/7/18.
 */

public interface ServiceListener {

    void onSuccess(JsonResponse jsonResp, String data);

    void onFailure(JsonResponse jsonResp, String data);


    /*void onSuccessResponse(JsonResponse jsonResp, String data);*/
}

