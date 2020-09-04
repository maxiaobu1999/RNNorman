package com.rnnorman.interaction;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.rnnorman.TestActivity;
/** 交互Module类 */
public class OpenNative extends ReactContextBaseJavaModule {
    private ReactContext mReactContext;

    public OpenNative(ReactApplicationContext context) {
        super(context);
        this.mReactContext = context;
    }

    @Override
    public String getName() {
        return "OpenNativeModule";
    }

    @ReactMethod
    public void openNativeVC(String json) {
        Bundle bundle = new Bundle();
        bundle.putString("data",json);
        Intent intent = new Intent();
        intent.setClass(mReactContext, TestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("bundle", bundle);
        mReactContext.startActivity(intent);
    }
}