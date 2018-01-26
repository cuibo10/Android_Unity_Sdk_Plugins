package com.quanyou.xilqp;

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
        PlatformConfig.setWeixin("wx46d63814bc3ba66e","5daf0095dcb43e6e154f8294448e65cb");
    }
}
