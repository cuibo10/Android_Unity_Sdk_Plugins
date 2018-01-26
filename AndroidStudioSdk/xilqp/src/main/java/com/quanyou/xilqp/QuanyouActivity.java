package com.quanyou.xilqp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cloudaemon.libguandujni.GuanduJNI;
import com.tencent.gcloud.voice.GCloudVoiceEngine;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.unity3d.player.UnityPlayerActivity;
import com.unity3d.player.UnityPlayer;

import java.util.Map;

/**
 * Created by MrLovelyCbb on 2017/11/13.
 */

public class QuanyouActivity extends UnityPlayerActivity
{
	private static String TAG = "unity_android";
    private static Context m_Context;
    protected UnityPlayer mUnityPlayer;
    private Vibrator mVibrator;//声明一个振动器对象
    private static WebView m_Webview;
    private static ProgressDialog progressDialog;

    static QuanyouLocation locationClient;

    private static String app_Scheme;
    private static String app_Roomid;

    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(saveInstanceState);

        // 加载Voice
        GCloudVoiceEngine.getInstance().init(getApplicationContext(), this);

        // 加载太极盾
        GuanduJNI.init();

        mUnityPlayer = super.mUnityPlayer;

        getWindow().takeSurface(null);
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        getWindow().setFormat(PixelFormat.RGB_565);

        m_Context = getApplicationContext();

        if (mUnityPlayer.getSettings().getBoolean("hide_status_bar", true))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        boolean trueColor8888 = false;
        mUnityPlayer.init(glesMode, trueColor8888);

        // init Gps location
        locationClient = new QuanyouLocation(UnityPlayer.currentActivity.getApplicationContext());

        Intent intent = getIntent();
        if(intent != null)
        {
            app_Scheme = intent.getScheme();
            if(!TextUtils.isEmpty(app_Scheme))
            {
                Uri uri = intent.getData();
                app_Roomid = uri.getQueryParameter("roomId");
            }
        }
    }

    /**
     * Req WeiXin Login
     * @param platform   0 本平台 1 微信 2 QQ 3 微博
     */
    public static void Cbb_ReqQyLogin(final int platform)
    {
        if(platform == 1)
        {
            if(!QuanyouUtils.isWeixinAvilible(UnityPlayer.currentActivity.getApplicationContext()))
            {
                return;
            }

            UMShareAPI.get(UnityPlayer.currentActivity).getPlatformInfo(UnityPlayer.currentActivity, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {

                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> map) {

                    if(share_media == SHARE_MEDIA.WEIXIN)
                    {
                           String nick = map.get("screen_name").toString();
                            String openid = map.get("openid");
                            String unionid = map.get("unionid");
                            String accessToken = map.get("accessToken");
                            String iconUrl =  map.get("profile_image_url");
                            String sex = map.get("gender");
                            Log.d("###","#########################=>"+map.toString());

                            String sendStr = platform +"#"+nick+"#"+openid+"#"+unionid+"#"+accessToken+"#"+iconUrl+"#"+sex;
                            System.out.println("sendStr Cbb_ReqQyLogin  =========> "+ sendStr);

                            Cbb_CallUnityFunc("Logic", "RespQyLogin",sendStr);
//                            Cbb_CallUnityFunc("Logic","RespQyLogin",map.toString());
                    }
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {

                }
            });
        }

        return;
    }

    /**
     * Req Join Room by Roomid
     */
    public static void Cbb_ReqWebJoinRoom(final String roomid)
    {
        Cbb_CallUnityFunc("Logic","Cbb_RespWebJoinRoom",roomid);
        return;
    }

    /**
     * Req Begin Get Location gps Start
     */
    public static void Cbb_ReqGetLocation()
    {
        QuanyouLocation.mLocationClient.start();
        return;
    }

    /**
     * Open System App Browser
     * Choose Browser
     */
    public static void Cbb_ReqOpenBrowser(String url)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        UnityPlayer.currentActivity.startActivity(Intent.createChooser(intent, "请选择一款浏览器"));
        return;
    }

    /**
     * Open System Browser by Url
     */
    public static void Cbb_ReqSysBrowser(String url)
    {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        UnityPlayer.currentActivity.startActivity(intent);
    }

    /**
     * Charge Money Api
     * @param chargeUrl
     * @param price
     */
    public static void Cbb_ReqWeixinCharge(String chargeUrl,int price)
    {
        if(m_Webview == null) {
            m_Webview = new WebView(UnityPlayer.currentActivity);
        }
        WebSettings webSettings = m_Webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//能够执行javaScript
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        progressDialog = new ProgressDialog(UnityPlayer.currentActivity, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("                加载中 请稍后...");//加载显示的信息
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆环风格

        //点击屏幕不消失
        progressDialog.setCanceledOnTouchOutside(false);

        m_Webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    Uri uri = Uri.parse(url);
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        UnityPlayer.currentActivity.startActivity(intent);
                        return true;
                    } else if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
                        Cbb_ReqOpenBrowser(url);
                    }
                }catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Logger.i("onPageStarted url=" + url);
                progressDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                 System.out.println(" onPageFinished  url="+url);
                try {
                    Uri uri = Uri.parse(url);
                    if (url.startsWith("weixin://wap/pay?")) {
                        progressDialog.dismiss();
                    } else if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
                        progressDialog.dismiss();
                    }
                }catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                    progressDialog.dismiss();
                }
                super.onPageFinished(view, url);
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    UnityPlayer.currentActivity.startActivity(intent);
                    return null;
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }
        });
        m_Webview.loadUrl(chargeUrl);
    }

    /**
     * Req Get Location gps stop
     */
    public static void Cbb_ReqStopLocaiton()
    {
        QuanyouLocation.mLocationClient.stop();
        return;
    }

    /**
     * Req Share Func
     * Share
     * 邀请、分享包房、分享下载
     * @param platform  0 本平台 1 微信 2 QQ 3 微博
     * @param shareType 1 朋友圈 2 朋友
     * @param shareTitle
     * @param shareContent
     * @param shareUrl
     * @param shareImgPath 分享的图标Icon
     */
    public static void Cbb_ReqWxShare(final int platform,final int shareType,String shareTitle,String shareContent,String shareUrl)
    {
        UMWeb web = null;
        ShareAction shareAction = new ShareAction(UnityPlayer.currentActivity);
        web = new UMWeb(shareUrl);
        web.setThumb(QuanyouUtils.getUmImage(m_Context));
        web.setDescription(shareContent);
        web.setTitle(shareTitle);
        shareAction.withMedia(web);
        // 朋友圈
        if(shareType == 1)
        {
            shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if (shareType == 2)// 好友
        {
            shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
        }

        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShare",platform + "|"+shareType+"|0");
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShare",platform + "|"+shareType+"|1");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                Cbb_CallUnityFunc("Logic","RespQyShare",platform + "|"+shareType+"|2");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShare",platform + "|"+shareType+"|3");
            }
        }).share();

        return;
    }

    /**
     * Share Image by filePath
     * Share Image
     * @param platform 0 本平台 1 微信 2 QQ 3 微博
     * @param shareType  1 朋友圈 2 好友
     * @param path
     */
    public static void Cbb_ReqShareImg(final int platform,final int shareType,String filePath)
    {
        ShareAction shareAction = new ShareAction(UnityPlayer.currentActivity);
        shareAction.withMedia(QuanyouUtils.getUmImage(m_Context,filePath));
        if(shareType == 1)
        {
            shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if(shareType == 2) {
            shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
        }

        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShareImg",platform + "|"+shareType+"|0");
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShareImg",platform + "|"+shareType+"|1");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                Cbb_CallUnityFunc("Logic","RespQyShareImg",platform + "|"+shareType+"|2");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Cbb_CallUnityFunc("Logic","RespQyShareImg",platform + "|"+shareType+"|3");
            }
        }).share();

        return;
    }

    /**
     * 太极盾
     * @param mappingAddr
     * @param mappingPort
     * @param mappingProtocol
     */
    public static void GetGuanduIPaddrReq(String mappingAddr,int mappingPort,int mappingProtocol)
    {
        GuanduJNI.getSecurityServerIPAndPort(mappingAddr,mappingPort,mappingProtocol);
        String RomoteIPAddr = GuanduJNI.ServerIP + "#" + GuanduJNI.ServerPort;

        Cbb_CallUnityFunc("Logic","Cbb_GetGuanduIPaddrResp",RomoteIPAddr);
    }

    /**
     * 剪切板 复制、粘贴  copy、pause
     * @param copyStr
     */
    public void Cbb_ReqClipboardCopy(String copyStr)
    {
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        //cm.setText(copyStr);
        ClipData clipData = ClipData.newPlainText("qy_Clipboard",copyStr);
        cm.setPrimaryClip(clipData);

        Cbb_CallUnityFunc("Logic","Cbb_RespClipboardCopy","1");
        Toast.makeText(UnityPlayer.currentActivity,"复制成功，可以发给朋友们了。",Toast.LENGTH_LONG).show();
        return;
    }

    /**
     * get Clipboard Content Send Unity
     */
    public void Cbb_ReqClipboardPaste()
    {
        ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        // check Clipboard content text is null
        if(!cm.hasPrimaryClip())
            return;

        ClipData cd = cm.getPrimaryClip();
        ClipData.Item item = cd.getItemAt(0);

        Cbb_CallUnityFunc("Logic","Cbb_RespClipboardPaste",item.coerceToText(m_Context).toString());

        // 清空剪切板
        cd = ClipData.newPlainText("","");
        cm.setPrimaryClip(cd);
    }

    /**
     * Clear Clipboard
     */
    public void Cbb_ReqClearClipboard()
    {
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("","");
        cm.setPrimaryClip(cd);

        Cbb_CallUnityFunc("Logic","Cbb_RespClipboardClear"," ");
        return;
    }

    /**
     * Android local Notification
     * @param title
     * @param content
     */
    @SuppressWarnings("deprecation")
    public void Cbb_ReqSendNotification(String alertTitle,String title, String content)
    {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(UnityPlayer.currentActivity);
        mBuilder.setContentTitle(title)
                .setContentTitle(content)
                .setTicker(alertTitle)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.umeng_socialize_wechat);

        Intent intent = new Intent(UnityPlayer.currentActivity,UnityPlayer.currentActivity.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(UnityPlayer.currentActivity,0,intent,0);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        manager.notify(1,notification);
       return;
    }

    /**
     * GetBatteryState
     * @return
     */
    public static void Cbb_ReqMonitorBatteryState()
    {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = m_Context.registerReceiver(null,iFilter);
        int rawLevel = intent.getIntExtra("level",0); // get current power
        int scale    = intent.getIntExtra("scale",0); // get total power
        int status   = intent.getIntExtra("status",0);// get current power status
        int health   = intent.getIntExtra("health",0);// get battery status
//        int batteryV = intent.getIntExtra("voltage",0);// get battery (umA)
//        int temperature = intent.getIntExtra("temperature",0);// get battery

        int level = -1;
        if(rawLevel > 0 && scale > 0)
        {
            level = (rawLevel * 100) / scale;
            // levelPower
            // totalPower
            Cbb_CallUnityFunc("Logic","RespMobileBattery",level+"_"+scale);
        }

        Cbb_CallUnityFunc("Logic","RespMobileBatteryStatus",status+"");

        return;
    }

    // 获取WiFi信号强度
    //分析wifi信号强度
    //获取RSSI，RSSI就是接受信号强度指示。
    //得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，
    //-50到-70表示信号偏差，小于-70表示最差，
    //有可能连接不上或者掉线，一般Wifi已断则值为-200。
    public void ObtainWifiInfo() {

//        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
//        WifiInfo info = wifiManager.getConnectionInfo();
//        if(info.getBSSID() != null)
//        {
//            int strength = WifiManager.calculateSignalLevel(info.getRssi(),5);
////            int speed = info.getLinkSpeed();
////            String units = WifiInfo.LINK_SPEED_UNITS;
////            String ssid = info.getSSID();
////            int ip = info.getIpAddress();
////            String mac = info.getMacAddress();
//
//            Cbb_CallUnityFunc("Logic","RespWifiStrength",strength+"");
//        }
    }

    /**
     * Mobile Shake
     */
    public void MobileShake()
    {
//        mVibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
//        mVibrator.vibrate(new long[]{100,10,100,1000},-1);
    }

        /**
         * when unity3d download apk,auto update apk
         * @param path this path in apk location
         */
    public static void Cbb_ReqAutoInstallApk(String path)
    {
        QuanyouUtils.AutoInstall(m_Context,path);

        Cbb_CallUnityFunc("Logic","Cbb_RespAutoInstallApk","1");

        return;
    }

    /**
     * 显示加载
     */
    public static void Cbb_ShowProcess()
    {
        return;
    }

    /**
     * 隐藏加载
     */
    public static void Cbb_HideProcess()
    {
        return;
    }

    /**
     * 加载广告
     */
    public static void Cbb_LoadingAd()
    {
        return;
    }

    /**
     * 闪屏界面
     */
    public static void Cbb_FlashUI()
    {
        return;
    }

    /**
     * 移除闪屏
     */
    public static void Cbb_RemoveFlash()
    {
        return;
    }

    /**
     * 调用Unity 方法
     * @param _objName Unity对象名字
     * @param _funcStr Unity函数名字
     * @param _content Unity传进入的参数 String
     */
    public static void Cbb_CallUnityFunc(String _objName,String _funcStr,String _content)
    {
        UnityPlayer.UnitySendMessage(_objName,_funcStr,_content);
    }

    private void Cbb_Premission()
    {
        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(UnityPlayer.currentActivity,mPermissionList,123);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(UnityPlayer.currentActivity).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == 1)
        {
            System.out.println("授权成功!");
        }
    }

}
