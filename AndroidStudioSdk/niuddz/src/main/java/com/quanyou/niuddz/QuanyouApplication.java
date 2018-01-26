package com.quanyou.niuddz;

import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by MrLovelyCbb on 2017/11/13.
 */

public class QuanyouApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Config.DEBUG = true;
        UMShareAPI.get(this);
    }

    {
        PlatformConfig.setWeixin("wx51b8225c286c0f03","5a53590a4e729de3137611f857410a6c");
    }
}
