package com.rideke.driver.common.dependencies.module

/**
 * @package com.cloneappsolutions.cabmedriver
 * @subpackage dependencies.module
 * @category AppContainerModule
 * @author SMR IT Solutions
 *
 */


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.rideke.driver.R
import com.rideke.driver.common.configs.SessionManager
import com.rideke.driver.common.database.Sqlite
import com.rideke.driver.common.helper.CustomDialog
import com.rideke.driver.common.helper.RunTimePermission
import com.rideke.driver.common.model.JsonResponse
import com.rideke.driver.common.network.AppController
import com.rideke.driver.common.util.userchoice.UserChoice
import com.rideke.driver.home.service.ForeService
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

/*****************************************************************
 * App Container Module
 */
@Module(includes = [com.rideke.driver.common.dependencies.module.ApplicationModule::class])
class AppContainerModule {
    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun providesContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesSessionManager(): SessionManager {
        return SessionManager()
    }

    @Provides
    @Singleton
    fun providesStringArrayList(): ArrayList<String> {
        return ArrayList()
    }

    @Provides
    @Singleton
    fun providesJsonResponse(): JsonResponse {
        return JsonResponse()
    }

    @Provides
    @Singleton
    fun providesRunTimePermission(): RunTimePermission {
        return RunTimePermission()
    }

    @Provides
    @Singleton
    internal fun providesCustomDialog(): CustomDialog {
        return CustomDialog()
    }


    @Provides
    @Singleton
    internal fun providesForeService(): ForeService {
        return ForeService()
    }

    @Provides
    @Singleton
    internal fun providesSqlite(): Sqlite {
        return Sqlite(AppController.appContext)
    }
    @Provides
    @Singleton
    internal fun providesUserChoice(): UserChoice {
        return UserChoice()
    }


}
