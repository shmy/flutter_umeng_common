package tech.shmy.flutter_umeng_common

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import com.umeng.analytics.MobclickAgent
import com.umeng.analytics.MobclickAgent.onPageEnd
import com.umeng.analytics.MobclickAgent.onPageStart
import com.umeng.commonsdk.UMConfigure
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/** FlutterUmengCommonPlugin */
class FlutterUmengCommonPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var versionMatch = false;

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext;
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_umeng_common")
        channel.setMethodCallHandler(this)
        onAttachedEngineAdd()
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (!versionMatch) {
            Log.e("UMLog", "onMethodCall:" + call.method.toString() + ":安卓SDK版本过低，请升级至8以上")
            //return;
        }
        try {
            val args = call.arguments as List<*>
            when (call.method) {
                "getPlatformVersion" -> result.success("Android " + Build.VERSION.RELEASE)
                "initCommon" -> initCommon(args)
                "onEvent" -> onEvent(args)
                "onProfileSignIn" -> onProfileSignIn(args)
                "onProfileSignOff" -> onProfileSignOff()
                "setPageCollectionModeAuto" -> setPageCollectionModeAuto()
                "setPageCollectionModeManual" -> setPageCollectionModeManual()
                "onPageStart" -> onPageStart(args)
                "onPageEnd" -> onPageEnd(args)
                "reportError" -> reportError(args)
                else -> result.notImplemented()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("Umeng", "Exception:" + e.message)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun onAttachedEngineAdd() {
        try {
            val agent = Class.forName("com.umeng.analytics.MobclickAgent")
            val methods: Array<Method> = agent.declaredMethods
            Log.d("UMLog", methods.toString())
            for (m in methods) {
                Log.e("UMLog", "Reflect:$m")
                if (m.name.equals("onEventObject")) {
                    versionMatch = true
                    break
                }
            }
            if (!versionMatch) {
                Log.e("UMLog", "安卓SDK版本过低，建议升级至8以上")
                //return;
            } else {
                Log.e("UMLog", "安卓依赖版本检查成功")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UMLog", "SDK版本过低，请升级至8以上$e")
            return
        }
        var method: Method? = null
        try {
            val config = Class.forName("com.umeng.commonsdk.UMConfigure")
            method =
                config.getDeclaredMethod("setWraperType", String::class.java, String::class.java)
            method.isAccessible = true
            method.invoke(null, "flutter", "1.0")
            Log.i("UMLog", "setWraperType:flutter1.0 success")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            Log.e("UMLog", "setWraperType:flutter1.0$e")
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            Log.e("UMLog", "setWraperType:flutter1.0$e")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            Log.e("UMLog", "setWraperType:flutter1.0$e")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Log.e("UMLog", "setWraperType:flutter1.0$e")
        }
    }

    private fun initCommon(args: List<*>) {
        val appkey = args[0] as String
        val channel = args[2] as String
        UMConfigure.preInit(context, appkey, channel)
        UMConfigure.init(context, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, null)
        Log.i("UMLog", "initCommon:$appkey@$channel")
    }

    @Suppress("UNCHECKED_CAST")
    private fun onEvent(args: List<*>) {
        val event = args[0] as String
        var map: Map<*, *>? = null
        if (args.size > 1) {
            map = args[1] as Map<*, *>?
        }
        //JSONObject properties = new JSONObject(map);
        MobclickAgent.onEventObject(context, event, map as MutableMap<String, Any>?)
        if (map != null) {
            Log.i("UMLog", "onEventObject:$event($map)")
        } else {
            Log.i("UMLog", "onEventObject:$event")
        }
    }

    private fun onProfileSignIn(args: List<*>) {
        val userID = args[0] as String
        MobclickAgent.onProfileSignIn(userID)
        Log.i("UMLog", "onProfileSignIn:$userID")
    }

    private fun onProfileSignOff() {
        MobclickAgent.onProfileSignOff()
        Log.i("UMLog", "onProfileSignOff")
    }

    private fun setPageCollectionModeAuto() {
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        Log.i("UMLog", "setPageCollectionModeAuto")
    }

    private fun setPageCollectionModeManual() {
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)
        Log.i("UMLog", "setPageCollectionModeManual")
    }

    private fun onPageStart(args: List<*>) {
        val event = args[0] as String
        onPageStart(event)
        Log.i("UMLog", "onPageStart:$event")
    }

    private fun onPageEnd(args: List<*>) {
        val event = args[0] as String
        onPageEnd(event)
        Log.i("UMLog", "onPageEnd:$event")
    }

    private fun reportError(args: List<*>) {
        val error = args[0] as String
        MobclickAgent.reportError(context, error)
        Log.i("UMLog", "reportError:$error")
    }
}
