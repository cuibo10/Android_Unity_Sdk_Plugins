package com.quanyou.niuddz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.umeng.socialize.media.UMImage;
import com.unity3d.player.UnityPlayer;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;

/**
 * Created by MrLovelyCbb on 2017/11/14.
 */

public class QuanyouUtils {

    public static final int REQUEST_CODE_PERMISSION_SINGLE_RECORD   = 1;
    public static final int REQUEST_CODE_PERMISSION_SINGLE_STORAGE  = 2;
    public static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 3;
    public static final int REQUEST_CODE_PERMISSION_MULTI = 4;
    public static final int REQUEST_CODE_SETTING = 999;

//    权限申请 -- 1 录音权限
//    权限申请 -- 2 存储权限
//    权限申请 -- 3 定位权限
//    权限申请 -- 4 多个权限申请
    public static void ReqPermission(final Activity currentActivity,int type)
    {
        int ReqCode = REQUEST_CODE_PERMISSION_SINGLE_STORAGE;
        String[] PermissionArr = Permission.MICROPHONE;
        switch (type)
        {
            case REQUEST_CODE_PERMISSION_SINGLE_RECORD:
                ReqCode = REQUEST_CODE_PERMISSION_SINGLE_RECORD;
                PermissionArr = Permission.MICROPHONE;
                break;
            case REQUEST_CODE_PERMISSION_SINGLE_STORAGE:
                ReqCode = REQUEST_CODE_PERMISSION_SINGLE_STORAGE;
                PermissionArr = Permission.STORAGE;
                break;
            case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                ReqCode = REQUEST_CODE_PERMISSION_SINGLE_LOCATION;
                PermissionArr = Permission.LOCATION;
                break;
        }

        if(type == REQUEST_CODE_PERMISSION_MULTI)
        {
            ReqCode = REQUEST_CODE_PERMISSION_MULTI;
            AndPermission.with(currentActivity)
                    .requestCode(ReqCode)
                    .permission(Permission.MICROPHONE,Permission.STORAGE,Permission.LOCATION,Permission.WIFI)
                    .callback(permissionListener)
                    .rationale(rationaleListener)
                    .start();
//            String[] MutiPermissionArr = new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_WIFI_STATE,
//                    Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.CHANGE_NETWORK_STATE,
//                    Manifest.permission.CHANGE_WIFI_STATE,
//                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.INTERNET,
//                    Manifest.permission.WRITE_SETTINGS,
//                    Manifest.permission.DISABLE_KEYGUARD,
//                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
//                    Manifest.permission.EXPAND_STATUS_BAR
//            };
//            AndPermission.with(currentActivity)
//                    .requestCode(ReqCode)
//                    .permission(MutiPermissionArr)
//                    .callback(permissionListener)
//                    .rationale(rationaleListener)
//                    .start();
        }else {
            AndPermission.with(currentActivity)
                    .requestCode(ReqCode)
                    .permission(PermissionArr)
                    .callback(permissionListener)
                    .rationale(rationaleListener)
                    .start();
        }
    }

    private static RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            String ReqMessage = "";
            switch (requestCode)
            {
                case REQUEST_CODE_PERMISSION_SINGLE_RECORD:
                    ReqMessage = "我们需要获取您的语音权限.";
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_STORAGE:
                    ReqMessage = "我们需要获取您的存储权限.";
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    ReqMessage = "我们需要获取您的定位权限.";
                    break;
                case REQUEST_CODE_PERMISSION_MULTI:
                    ReqMessage = "我们需要获取您的存储、定位、语音权限.";
                    break;
                default:
                    break;
            }
            new AlertDialog.Builder(QuanyouActivity.currentActivity)
                    .setTitle("提示")
                    .setMessage(ReqMessage)
                    .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();
        }
    };

    private static PermissionListener permissionListener = new PermissionListener() {

        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode)
            {
                case REQUEST_CODE_PERMISSION_SINGLE_RECORD:
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_STORAGE:
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    break;
                case REQUEST_CODE_PERMISSION_MULTI:
                    break;
                default:
                    break;
            }
            QuanyouActivity.Cbb_CallUnityFunc("Logic","RespPermissions",requestCode+"|"+"0");
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode)
            {
                case REQUEST_CODE_PERMISSION_SINGLE_RECORD:
                    Toast.makeText(UnityPlayer.currentActivity,"授权语音权限失败了",Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_STORAGE:
                    Toast.makeText(UnityPlayer.currentActivity,"授权存储权限失败了",Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    Toast.makeText(UnityPlayer.currentActivity,"授权定位权限失败了",Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_CODE_PERMISSION_MULTI:
//                    Toast.makeText(UnityPlayer.currentActivity,"一些权限被您拒绝或申请失败，请您到隐私设置授权，否则部分功能无法正常获取！",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            QuanyouActivity.Cbb_CallUnityFunc("Logic","RespPermissions",requestCode+"|"+"1");
//            if(AndPermission.hasAlwaysDeniedPermission(UnityPlayer.currentActivity,deniedPermissions))
//            {
//                if(UnityPlayer.currentActivity != null) {
//                    new AlertDialog.Builder(QuanyouActivity.currentActivity)
//                            .setTitle("权限申请失败")
//                            .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
//                            .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//                                    Uri uri = Uri.fromParts("package", QuanyouUtils.getPackageName(UnityPlayer.currentActivity), (String)null);
//                                    intent.setData(uri);
//                                    UnityPlayer.currentActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
//                                }
//                            })
//                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            }).show();
//
//
//                }
//            }
        }
    };

    /**
     * 绝对路径
     * @param filePath
     */
    public static int AutoInstall(Context context,String filePath)
    {
        int ret = 0;
        if(Build.VERSION.SDK_INT < 23)
        {
            Uri apk = Uri.fromFile(new File("file://"+filePath));
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);

            ret = 1;
        }else
        {
            File file = new File(filePath);
            if(file.exists())
            {
                openFile(file,context);
                ret = 1;
            }else
            {
                System.out.println("AutoInstall Error filePath ="+filePath);
                ret = 0;
            }
        }
        return ret;
    }

    private static void openFile(File file, Context context) {
        Intent intent = new Intent();
        //intent.addFlags(268435456);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    /**
     * 获取分享图片
     * @param context
     * @return
     */
    public static UMImage getUmImage(Context context)
    {
        UMImage umImage = null;
        Bitmap bitmap = QuanyouUtils.getIconBitmap(context);
        if(bitmap != null)
        {
            umImage = new UMImage(context,bitmap);
        }
        return umImage;
    }

    /**
     * 获取分享图片 + FilePath
     * @param context
     * @return
     */
    public static UMImage getUmImage(Context context,String filePath)
    {
        UMImage umImage = null;
        Bitmap bitmap = null;
        File file = new File(filePath);
        if(file.exists())
        {
            umImage = new UMImage(context, file);
        }else
        {
            bitmap = QuanyouUtils.getIconBitmap(context);
            umImage = new UMImage(context,bitmap);
        }
        return umImage;
    }

    /**
     * 获取图标Bitmap
     * @param context
     * @return
     */
    public static Bitmap getIconBitmap(Context context)
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    /**
     * 获取包名
     * @param context
     * @return
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

        /**
         * drawable To Bitmap
         * @param drawable
         * @return
         */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 判断微信是否安装
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断QQ是否安装
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}


// AndPermission.with(currentActivity)
//         .requestCode(QuanyouUtils.REQUEST_CODE_PERMISSION_MULTI)
//         .permission(Permission.MICROPHONE, Permission.STORAGE, Permission.LOCATION)
//         .callback(permissionListener)
//         // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
//         // 这样避免用户勾选不再提示，导致以后无法申请权限。
//         // 你也可以不设置。
//         .rationale(new RationaleListener() {
//@Override
//public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
//        new AlertDialog.Builder(currentActivity)
//        .setTitle("提示")
//        .setMessage("我们需要获取您的定位及存储权限.")
//        .setPositiveButton("允许", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        dialog.cancel();
//        rationale.resume();
//        }
//        })
//        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        dialog.cancel();
//        rationale.cancel();
//        }
//        }).show();
//        }
//        })
//        .start();