package com.rideke.driver.common.dependencies.component

/**
 * @package com.cloneappsolutions.cabmedriver
 * @subpackage dependencies.component
 * @category AppComponent
 * @author SMR IT Solutions
 *
 */

import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.database.AddFirebaseDatabase
import com.rideke.driver.common.dependencies.module.AppContainerModule
import com.rideke.driver.common.dependencies.module.ApplicationModule
import com.rideke.driver.common.dependencies.module.ImageCompressAsyncTask
import com.rideke.driver.common.dependencies.module.NetworkModule
import com.rideke.driver.common.helper.CarTypeAdapter
import com.rideke.driver.common.helper.CommonDialog
import com.rideke.driver.common.helper.RunTimePermission
import com.rideke.driver.common.util.CommonMethods
import com.rideke.driver.common.util.RequestCallback
import com.rideke.driver.common.util.userchoice.UserChoice
import com.rideke.driver.common.views.CommonActivity
import com.rideke.driver.common.views.PaymentWebViewActivity
import com.rideke.driver.common.views.SupportActivityCommon
import com.rideke.driver.common.views.SupportAdapter
import com.rideke.driver.google.direction.GetDirectionData
import com.rideke.driver.google.locationmanager.*
import com.rideke.driver.home.MainActivity
import com.rideke.driver.home.facebookAccountKit.FacebookAccountKitActivity
import com.rideke.driver.home.firebaseChat.ActivityChat
import com.rideke.driver.home.firebaseChat.AdapterFirebaseRecylcerview
import com.rideke.driver.home.firebaseChat.FirebaseChatHandler
import com.rideke.driver.home.fragments.AccountFragment
import com.rideke.driver.home.fragments.EarningActivity
import com.rideke.driver.home.fragments.HomeFragment
import com.rideke.driver.home.fragments.RatingActivity
import com.rideke.driver.home.fragments.Referral.ShowReferralOptionsActivity
import com.rideke.driver.home.fragments.currency.CurrencyListAdapter
import com.rideke.driver.home.fragments.language.LanguageAdapter
import com.rideke.driver.home.fragments.payment.*
import com.rideke.driver.home.managevehicles.*
import com.rideke.driver.home.managevehicles.adapter.*
import com.rideke.driver.home.map.GpsService
import com.rideke.driver.home.map.drawpolyline.DownloadTask
import com.rideke.driver.home.paymentstatement.*
import com.rideke.driver.home.payouts.*
import com.rideke.driver.home.payouts.adapter.PayoutCountryListAdapter
import com.rideke.driver.home.payouts.payout_model_classed.PayPalEmailAdapter
import com.rideke.driver.home.profile.DriverProfile
import com.rideke.driver.home.profile.VehiclInformation
import com.rideke.driver.home.pushnotification.MyFirebaseInstanceIDService
import com.rideke.driver.home.pushnotification.MyFirebaseMessagingService
import com.rideke.driver.home.service.FloatingWidgetService
import com.rideke.driver.home.service.ForeService
import com.rideke.driver.home.service.LocationService
import com.rideke.driver.home.signinsignup.*
import com.rideke.driver.home.splash.SplashActivity
import com.rideke.driver.trips.*
import com.rideke.driver.trips.rating.*
import com.rideke.driver.trips.tripsdetails.*
import com.rideke.driver.trips.viewmodel.ReqAccpVM

import dagger.Component
import javax.inject.Singleton


/*****************************************************************
 * App Component
 */
@Singleton
@Component(modules = [NetworkModule::class, ApplicationModule::class, AppContainerModule::class])
interface AppComponent {
    // ACTIVITY

    fun inject(bankDetailsActivity: BankDetailsActivity)


    fun inject(currencyListAdapter: CurrencyListAdapter)

    fun inject(payoutEmailActivity: PayoutEmailActivity)

    fun inject(payoutEmailListActivity: PayoutEmailListActivity)


    fun inject(payPalEmailAdapter: PayPalEmailAdapter)

    fun inject(payoutAddressDetailsActivity: PayoutAddressDetailsActivity)

    fun inject(payoutBankDetailsActivity: PayoutBankDetailsActivity)

    fun inject(payoutCoutryListAdapter2: PayoutCoutryListAdapter2)

    fun inject(priceStatementAdapter: PriceStatementAdapter)

    fun inject(paymentPage: PaymentPage)

    fun inject(driverDetailsAdapter: DriverDetailsAdapter)

    fun inject(sessionManager: SessionManager)

    fun inject(pendingTripsFragment: PendingTripsFragment)

    fun inject(accountFragment: AccountFragment)

    fun inject(viewDocumentFragment: ViewVehicleDocumentFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(past: CompletedTripsFragments)

    fun inject(ratingFragment: RatingActivity)

    fun inject(comments: Comments)

    fun inject(yourTrips: YourTrips)

    fun inject(carTypeAdapter: CarTypeAdapter)

    fun inject(tripDetails: TripDetails)

    fun inject(PaymentStatementActivity: PaymentStatementActivity)


    fun inject(ManageVehicleActivity: ManageVehicleFragment)
    fun inject(vehicleTypeAdapter: VehicleTypeAdapter)

    fun inject(earningFragment: EarningActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(signinSignupHomeActivity: SigninSignupHomeActivity)

    fun inject(splashActivity: SplashActivity)

    fun inject(addPayment: AddPayment)

    fun inject(riderProfilePage: RiderProfilePage)

    fun inject(setting_Activity: SettingActivity)

    fun inject(requestReceiveActivity: RequestReceiveActivity)

    fun inject(manageDriverDocFrag: ManageDriverDocumentFragment)

    fun inject(viewVehicleDocFrag: ViewDriverDocumentFragment)

    fun inject(requestAcceptActivity: RequestAcceptActivity)

    fun inject(riderContactActivity: RiderContactActivity)

    fun inject(cancelYourTripActivity: CancelYourTripActivity)

    fun inject(documentDetails: DocumentDetails)

    fun inject(paymentAmountPage: PaymentAmountPage)

    fun inject(payStatementDetails: PayStatementDetails)

    fun inject(tripEarningsDetail: TripEarningsDetail)

    fun inject(dailyEarningDetails: DailyEarningDetails)

    fun inject(riderrating: Riderrating)

    fun inject(gps_service: GpsService)

    fun inject(registerCarDetailsActivity: RegisterCarDetailsActivity)

    fun inject(resetPassword: ResetPassword)

    fun inject(register: Register)

    fun inject(registerOTPActivity: RegisterOTPActivity)

    fun inject(commonMethods: CommonMethods)

    fun inject(MobileActivity: MobileActivity)

    fun inject(signinActivity: SigninActivity)

    fun inject(requestCallback: RequestCallback)

    fun inject(runTimePermission: RunTimePermission)

    fun inject(driverProfile: DriverProfile)

    fun inject(vehiclInformation: VehiclInformation)

    fun inject(riderFeedBack: RiderFeedBack)

    fun inject(activityChat: ActivityChat)

    fun inject(facebookAccountKitActivity: FacebookAccountKitActivity)

    // Adapters
    fun inject(manageVehicleAdapter: ManageVehicleAdapter)

    fun inject(yearAdapter: YearAdapter)

    fun inject(payoutDetailsListAdapter: PayoutDetailsListAdapter)

    fun inject(languageAdapter: LanguageAdapter)

    fun inject(addVehicle: AddVehicleFragment)

    fun inject(manageDocumentsAdapter: ManageDocumentsAdapter)

    fun inject(myFirebaseMessagingService: MyFirebaseMessagingService)

    fun inject(myFirebaseInstanceIDService: MyFirebaseInstanceIDService)

    fun inject(imageCompressAsyncTask: ImageCompressAsyncTask)

    fun inject(firebaseChatHandler: FirebaseChatHandler)

    fun inject(payoutCountryListAdapter: PayoutCountryListAdapter)

    fun inject(adapterFirebaseRecylcerview: AdapterFirebaseRecylcerview)

    fun inject(makeAdapter: MakeAdapter)

    fun inject(modelAdapter: ModelAdapter)

    fun inject(reqAccpVM: ReqAccpVM)

    //    service

    fun inject(downloadTask: DownloadTask)

    fun inject(foreService: ForeService)



    //fun inject(workerUtils: WorkerUtils)


    //fun inject(updateGPSWorker: UpdateGPSWorker)


    fun inject(locationService: LocationService)

    fun inject(firebaseDatabase: AddFirebaseDatabase)

    fun inject(payToAdminActivity: PayToAdminActivity)

    fun inject(manageVehicles: ManageVehicles)

    fun inject(paymentActivity: PaymentActivity)

    fun inject(addCardActivity: AddCardActivity)

    fun inject(payoutDetailsListActivity: PayoutDetailsListActivity)

    fun inject(manageDocumentActivity: ManageVehicleDocumentFragment)


    fun inject(priceRecycleAdapter: PriceRecycleAdapter)

    fun inject(showReferralOptionsActivity: ShowReferralOptionsActivity)

    fun inject(upcomingTripsPaginationAdapter: PendingTripsPaginationAdapter)

    fun inject(pastTripsPaginationAdapter: CompletedTripsPaginationAdapter)

    fun inject(paymentMethodAdapter: PaymentMethodAdapter)

    fun inject(featuresInVehicleAdapter: FeaturesInVehicleAdapter)


    /**
     * Live Tracking Injects
     */
    fun inject(getDirectionData: GetDirectionData)

    fun inject(updateLocations: UpdateLocations)

    fun inject(trackingServiceListener: TrackingServiceListener)

    fun inject(trackingService: TrackingService)

    fun inject(androidPositionProvider: AndroidPositionProvider)

    fun inject(trackingController: TrackingController)

    fun inject(supportActivityCommon: SupportActivityCommon)

    fun inject(supportAdapter: SupportAdapter)

    fun inject(payStatementPaginationAdapter: PayStatementPaginationAdapter)

    fun inject(dailyEarnPaginationAdapter: DailyEarnPaginationAdapter)

    fun inject(dailyHoursPaginationAdapter: DailyHoursPaginationAdapter)

    fun inject(dailyEarnListAdapter: DailyEarnListAdapter)

    fun inject(paymentWebViewActivity: PaymentWebViewActivity)

    fun inject(commonActivity: CommonActivity)

    fun inject(commonDialog: CommonDialog)

    fun inject(commentsPaginationAdapter: CommentsPaginationAdapter)

    fun inject(userChoice: UserChoice)

    fun inject(commentsRecycleAdapter: CommentsRecycleAdapter)

    fun inject(floatingWidgetService: FloatingWidgetService)

    //fun inject(applicationContext: Context)

}
