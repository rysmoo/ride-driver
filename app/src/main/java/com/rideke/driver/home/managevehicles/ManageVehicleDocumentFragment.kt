package com.rideke.driver.home.managevehicles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.home.managevehicles.adapter.ManageDocumentsAdapter
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.home.datamodel.DriverProfileModel
import javax.inject.Inject

class ManageVehicleDocumentFragment : Fragment(), ManageDocumentsAdapter.OnClickListener {


    lateinit var manageDocuments: View

    @BindView(R.id.rv_docs)
    lateinit var rvDocs: RecyclerView


    @Inject
    lateinit var commonMethods: CommonMethods
    @BindView(R.id.tv_no_document)
    lateinit var tvNoDocument: TextView

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var driverProfileModel: DriverProfileModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        manageDocuments = inflater.inflate(R.layout.manage_documents_fragment, container, false)
        AppController.getAppComponent().inject(this)
        ButterKnife.bind(this, manageDocuments)

        driverProfileModel = Gson().fromJson(sessionManager.profileDetail, DriverProfileModel::class.java)

        Log.i("ManageDocuments", "onCreateView: ManageVehicleDocument-> driverProfileModel=${Gson().toJson(driverProfileModel)}")


        initRecyclerView()

        return manageDocuments
    }


    private fun initRecyclerView() {

        val adapter = ManageDocumentsAdapter(requireContext(), driverProfileModel.vehicle.get((activity as ManageVehicles).documentClickedPosition!!).document, this)
        rvDocs.adapter = adapter

        (activity as ManageVehicles).setHeader(getString(R.string.manage_documents))
        (activity as ManageVehicles).hideAddButton()



        if (driverProfileModel.vehicle.size == 0)
            tvNoDocument.visibility = View.VISIBLE
        else
            tvNoDocument.visibility = View.GONE

    }

    override fun onClick(pos: Int) {
        (activity as ManageVehicles).documentPosition = pos
        findNavController().navigate(R.id.action_documentFragment_to_viewDocumentFragment2)

    }

}
