package com.dribba.sfmc_flutter

import android.app.Application
import androidx.annotation.NonNull
import com.salesforce.marketingcloud.MCLogListener
import com.salesforce.marketingcloud.MarketingCloudConfig
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig

/** SfmcFlutterPlugin */
class SfmcFlutterPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sfmc_flutter")
        channel.setMethodCallHandler(this)
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "setContactKey") {
            val cKey = call.argument<String>("cId")
            if (cKey == null) {
                result.error("ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED");
                return
            }
            result.success(setContactKey(cKey))
        } else if (call.method == "setTag") {

            val tag = call.argument<String>("tag")
            if (tag == null) {
                result.error("ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED");
                return
            }
            result.success(setTag(tag))
        } else if (call.method == "removeTag") {
            val tag = call.argument<String>("tag")
            if (tag == null) {
                result.error("ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED");
                return
            }
            result.success(removeTag(tag))
        } else if (call.method == "setAttribute") {
            val attrName = call.argument<String>("name")
            val attrValue = call.argument<String>("value")
            if (attrName == null || attrValue == null) {
                result.error("ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED");
                return
            }
            result.success(setAttribute(attrName, attrValue))
        } else if (call.method == "clearAttribute") {
            val attrName = call.argument<String>("name")
            if (attrName == null) {
                result.error("ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED", "ARGS_NOT_ALLOWED");
            }
            result.success(attrName?.let { clearAttribute(it) })
        } else if (call.method == "pushEnabled") {
            pushEnabled() { res ->
                result.success(res)
            }
        } else if (call.method == "enablePush") {
            result.success(setPushEnabled(true))
        } else if (call.method == "disablePush") {
            result.success(setPushEnabled(false))
        } else if (call.method == "sdkState") {
            getSDKState() { res ->
                result.success(res)
            }
        } else if (call.method == "enableVerbose") {
            result.success(setupVerbose(true))
        } else if (call.method == "disableVerbose") {
            result.success(setupVerbose(false))
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    /*fun setupSFMC(appId: String, accessToken: String, mid: String, sfmcURL: String, locationEnabled: Boolean, inboxEnabled: Boolean, analyticsEnabled: Boolean, delayRegistration: Boolean, onDone: (_ result: Bool, _ message: String?, _ code: Int?) -> Void) {
        // Initialize logging _before_ initializing the SDK to avoid losing valuable debugging information.
        if(BuildConfig.DEBUG) {
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
        }

        SFMCSdk.configure(applicationContext as Application, SFMCSdkModuleConfig.build {
            pushModuleConfig = MarketingCloudConfig.builder().apply {
                setApplicationId("{mc_application_id}")
                setAccessToken("{mc_access_token}")
                setSenderId("{fcm_sender_id}")
                setMarketingCloudServerUrl("{marketing_cloud_url}")
                setMid("{mid}")
                setNotificationCustomizationOptions(
                        NotificationCustomizationOptions.create(R.drawable.ic_notification_icon)
                )
                // Other configuration options
            }.build(applicationContext)
        }) { initStatus ->
            // TODO handle initialization status
        }

        SFMCSdk.configure(applicationContext as Application, SFMCSdkModuleConfig.build {
            pushModuleConfig = MarketingCloudConfig.builder().apply {
                setApplicationId("")
                setAccessToken("")
                setSenderId("")
                setMarketingCloudServerUrl("")
                setMid("")
                setNotificationCustomizationOptions(
                        NotificationCustomizationOptions.create(R.drawable.ic_notification_icon)
                )
            }.build(applicationContext)
        }) { initStatus ->

        }

        let builder = MarketingCloudSDKConfigBuilder ()
                .sfmc_setApplicationId(appId)
                .sfmc_setAccessToken(accessToken)
                .sfmc_setMarketingCloudServerUrl(mid)
                .sfmc_setMid(mid)
                .sfmc_setDelayRegistration(untilContactKeyIsSet:(delayRegistration ?? false) as NSNumber)
        .sfmc_setInboxEnabled((inboxEnabled ?? true) as NSNumber)
        .sfmc_setLocationEnabled((locationEnabled ?? true) as NSNumber)
        .sfmc_setAnalyticsEnabled((analyticsEnabled ?? true) as NSNumber)
        .sfmc_build()!

        do {
            try MarketingCloudSDK.sharedInstance().sfmc_configure(with:builder)
                onDone(true, nil, nil);
            } catch let error as NSError {
                onDone(false, error.localizedDescription, error.code);
            }
        }*/

    /*
    * Contact Key Management
    * */
    fun setContactKey(contactKey: String): Boolean {
        MarketingCloudSdk.requestSdk { sdk ->
            val registrationManager = sdk.registrationManager
            registrationManager.edit().run {
                setContactKey(contactKey)
                commit()
            }
        }
        return true
    }

    /*
     * Attribute Management
     */
    fun setAttribute(name: String, value: String): Boolean {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit().run {
                // Set Attribute value
                setAttribute(name, value)
                commit()
            }
        }
        return true
    }

    fun clearAttribute(name: String): Boolean {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit().run {
                clearAttribute(name)
                commit()
            }
        }
        return true
    }

    fun attributes(): Array<String> {
        return emptyArray<String>()
    }

    /*
     * TAG Management
     */
    fun setTag(tag: String): Boolean {
        MarketingCloudSdk.requestSdk { sdk ->

            sdk.registrationManager.edit().run {
                addTags(tag)
                commit()
            }
        }
        return true
    }

    fun removeTag(tag: String): Boolean {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit().run {
                removeTags(tag)
                commit()
            }
        }
        return true
    }

    /*
     * Verbose Management
     */
    fun setupVerbose(status: Boolean): Boolean {
        if (status) {
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
        } else {
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
        }
        return true
    }

    /*
     * Verbose Management
     */
    fun pushEnabled(result: (Any?) -> Unit) {
        MarketingCloudSdk.requestSdk { sdk ->
            result.invoke(sdk.pushMessageManager.isPushEnabled())
        }
    }

    fun setPushEnabled(status: Boolean): Boolean {
        if (status) {
            MarketingCloudSdk.requestSdk { sdk -> sdk.pushMessageManager.enablePush() }
        } else {
            MarketingCloudSdk.requestSdk { sdk -> sdk.pushMessageManager.disablePush() }
        }
        return true
    }

    /*
     * SDKState Management
     */
    fun getSDKState(result: (Any?) -> Unit) {
        MarketingCloudSdk.requestSdk { sdk ->
            result.invoke(sdk.sdkState.toString())
        }
    }
}
