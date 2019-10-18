package com.ajithab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;


public class RNFileShareIntentModule extends ReactContextBaseJavaModule {

  public static String TAG = "[ReactContextBaseJavaModule]";

  private final ReactApplicationContext reactContext;

  public static Intent shareIntent = null;

  public RNFileShareIntentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  public static void setShareIntent(Intent intent) {
    shareIntent = new Intent(intent);
  }

  @ReactMethod
  public void getFilepath(Callback successCallback) {
    Log.d(TAG, "[getFilepath][shareIntent]: " + shareIntent);
    Intent intent = null;
    if (shareIntent != null) {
      intent = shareIntent;
    } else {
      Activity mActivity = getCurrentActivity();
      if(mActivity == null) { return; }
      intent = mActivity.getIntent();
      Log.d(TAG, "[getFilepath][getCurrentActivity]: " + intent);
    }

    String action = intent.getAction();
    String type = intent.getType();

    Log.d(TAG, "[getFilepath]: " + action + " " + type);
    Log.d(TAG, "[getFilepath][intent]: " + intent);

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        String input = intent.getStringExtra(Intent.EXTRA_TEXT);
        successCallback.invoke(input);
      } else if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("application/")) {
        Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
          successCallback.invoke(fileUri.toString());
        }
      }else {
        Toast.makeText(reactContext, "Type is not support", Toast.LENGTH_SHORT).show();
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("application/")) {
          ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
          if (fileUris != null) {
            String completeString = new String();
            for (Uri uri: fileUris) {
              completeString += uri.toString() + ",";
            }
            successCallback.invoke(completeString);
          }
        } else {
          Toast.makeText(reactContext, "Type is not support", Toast.LENGTH_SHORT).show();
        }
    }
  }

  @ReactMethod
  public void clearFilePath() {
    shareIntent = null;
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }

    Intent intent = mActivity.getIntent();
    if (intent == null) { return; }
    String type = intent.getType();
    if (TextUtils.isEmpty(type)) { return; }
    if ("text/plain".equals(type)) {
      intent.removeExtra(Intent.EXTRA_TEXT);
    } else if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("application/")) {
      intent.removeExtra(Intent.EXTRA_STREAM);
    }
  }
  @Override
  public String getName() {
    return "RNFileShareIntent";
  }
}
