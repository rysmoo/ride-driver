# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bowshulsheikrahaman/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
-keep class android.support.v7.widget.** { *; }

-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}


-ignorewarnings

#-keep class * {
#    public private *;
#}

#-keep public class

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }


-keepclasseswithmembernames class * {
    @butterknife.BindView <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#For retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepattributes *Annotation*,SourceFile,LineNumberTable


-dontwarn com.squareup.okhttp.**

-keep class com.rideke.driver.home.firebaseChat.FirebaseChatModelClass{ *; }
-keep class com.rideke.driver.home.datamodel.** { *; }
-keep class com.rideke.driver.trips.voip.CabmeSinchService.** { *; }
-keep class com.rideke.driver.home.map.FetchAddressIntentService{ *; }
-keep class com.rideke.driver.home.map.AppUtils{ *; }
-keep class com.rideke.driver.home.map.drawpolyline.** { *; }
-keep class com.rideke.driver.home.map.**   { *; }
-keep class com.rideke.driver.common.**  { *; }
-keep class com.rideke.driver.trips.tripsdetails.** { *; }
-keep class com.rideke.driver.google.direction.** { *; }
-keep class com.rideke.driver.common.util.userchoice.** { *; }
-keep class com.rideke.driver.common.model.** { *; }
-keep class com.rideke.driver.home.payouts.payout_model_classed.** { *; }


#}


-keep class com.cardinalcommerce.dependencies.internal.bouncycastle.**
-keep class com.cardinalcommerce.dependencies.internal.nimbusds.**