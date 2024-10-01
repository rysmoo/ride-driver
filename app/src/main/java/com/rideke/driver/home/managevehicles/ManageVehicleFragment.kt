package com.rideke.driver.home.managevehicles

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.helper.Constants
import com.rideke.driver.common.helper.CustomDialog
import com.rideke.driver.common.model.JsonResponse
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonKeys
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.common.util.Enums.DELETE_VEHICLE
import com.rideke.driver.common.util.Enums.REQ_DRIVER_PROFILE
import com.rideke.driver.common.util.RequestCallback
import com.rideke.driver.home.datamodel.AddedVehiclesModel
import com.rideke.driver.home.datamodel.DriverProfileModel
import com.rideke.driver.home.interfaces.ApiService
import com.rideke.driver.home.interfaces.ServiceListener
import com.rideke.driver.home.managevehicles.adapter.ManageVehicleAdapter
import kotlinx.android.synthetic.main.manage_vehicle_fragment.*
import javax.inject.Inject

class ManageVehicleFragment : Fragment(), ManageVehicleAdapter.OnClickListener , ServiceListener {

    private var deletePosition: Int?=null
    private var addedVehicleDetails = AddedVehiclesModel()
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var customDialog: CustomDialog
    private var dialog: AlertDialog? = null

    lateinit var manageVehicle : View

    @BindView(R.id.rv_vehicles)
    lateinit var rvVehicles: RecyclerView
    lateinit var adapter :  ManageVehicleAdapter

    lateinit var driverProfileModel: DriverProfileModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        manageVehicle = inflater.inflate(R.layout.manage_vehicle_fragment, container, false)
        AppController.getAppComponent().inject(this)
        ButterKnife.bind(this,manageVehicle)

        (requireActivity() as ManageVehicles).setHeader(getString(R.string.vehicleinformation))
        dialog = commonMethods.getAlertDialog(requireContext())
        return manageVehicle
    }

    private fun driverProfile() {
        if (commonMethods.isOnline(requireActivity())) {
            commonMethods.showProgressDialog((activity as ManageVehicles).getAppCompatActivity())
//            commonMethods.showMessage(requireActivity(), dialog, resources.getString(R.string.loading))
            apiService.getDriverProfile(sessionManager.accessToken!!)
                .enqueue(RequestCallback(REQ_DRIVER_PROFILE, this))
        } else {
            CommonMethods.showInternetNotAvailableForStoredDataViewer(requireActivity())
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as ManageVehicles).initViews()
        driverProfile()
    }

    private fun initRecyclerView() {
        adapter = ManageVehicleAdapter(requireContext(),driverProfileModel.vehicle,this)
        rvVehicles.adapter = adapter

        if(driverProfileModel.vehicle.size == 0)
            tv_no_vehicles.visibility = View.VISIBLE
        else
            tv_no_vehicles.visibility = View.GONE

    }

    override fun onClick(pos : Int,clickType : String) {
        (activity as ManageVehicles).vehicleClickPosition = pos
        if (clickType.equals(CommonKeys.DOCUMENT)){
            (activity as ManageVehicles).documentClickedPosition = pos
            findNavController().navigate(R.id.action_vehicleFragment_to_documentFragment)
        } else if (clickType.equals(CommonKeys.EDIT))
            findNavController().navigate(R.id.action_vehicleFragment_to_addVehicle)
        else if (clickType.equals(CommonKeys.DELETE)){
            confirmationPopup(pos)
        }




    }

    private fun confirmationPopup(pos : Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        // set the custom dialog components - text, image and button
        val tvMessage = dialog.findViewById<View>(R.id.tv_message) as TextView
        val cancel = dialog.findViewById<View>(R.id.signout_cancel) as TextView
        val delete = dialog.findViewById<View>(R.id.signout_signout) as TextView
        delete.text = resources.getString(R.string.delete)
        tvMessage.text = resources.getString(R.string.delete_msg)
        // if button is clicked, close the custom dialog
        cancel.setOnClickListener { dialog.dismiss() }

        delete.setOnClickListener {
            deletePosition = pos
            deleteVehicle()
            dialog.dismiss()

        }
        dialog.show()
    }

    private fun deleteVehicle() {

        commonMethods.showProgressDialog((activity as ManageVehicles).getAppCompatActivity())
        apiService.deleteVehicle(sessionManager.accessToken!!,(activity as ManageVehicles).vehicleDetails.get(deletePosition!!).id).enqueue(RequestCallback(DELETE_VEHICLE, this))
    }




    override fun onSuccess(jsonResp: JsonResponse, data: String) {

        commonMethods.hideProgressDialog()
        if (!jsonResp.isOnline) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(context, dialog, data)
            return
        }

        when (jsonResp.requestCode) {
            DELETE_VEHICLE -> if (jsonResp.isSuccess) {
                onSuccessVehicleDeleted(jsonResp)
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.showMessage(context, dialog, jsonResp.statusMsg)
            }

            REQ_DRIVER_PROFILE -> {
                if (jsonResp.isSuccess) {
                    driverProfileModel = gson.fromJson(jsonResp.strResponse, DriverProfileModel::class.java)
                    sessionManager.profileDetail = jsonResp.strResponse
                    initRecyclerView()
                } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                    commonMethods.showMessage(requireActivity(), dialog, jsonResp.statusMsg)
                }
            }

            else -> {
            }
        }
    }

    private fun onSuccessVehicleDeleted(jsonResp: JsonResponse) {

        (activity as ManageVehicles).vehicleDetails.removeAt(deletePosition!!)
        adapter.notifyDataSetChanged()

    }


    override fun onFailure(jsonResp: JsonResponse?, data: String?) {

    }

}
