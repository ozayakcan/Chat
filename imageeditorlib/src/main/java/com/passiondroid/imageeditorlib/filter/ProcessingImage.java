package com.passiondroid.imageeditorlib.filter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.passiondroid.imageeditorlib.utils.TaskCallback;
import com.passiondroid.imageeditorlib.utils.Utility;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ProcessingImage {
  private final Bitmap srcBitmap;
  private final TaskCallback<String> callback;
  private final String imagePath;
  private final Executor executor;
  private final Handler handler;
  public ProcessingImage(Bitmap srcBitmap, String imagePath, TaskCallback<String> taskCallback) {
    this.srcBitmap = srcBitmap;
    this.callback = taskCallback;
    this.imagePath = imagePath;
    executor = Executors.newSingleThreadExecutor();
    handler = new Handler(Looper.getMainLooper());
  }

  public void executeAsync(){
    executor.execute(() -> {
      String s = Utility.saveBitmap(srcBitmap,imagePath);
      handler.post(() -> {
        if(callback!=null){
          callback.onTaskDone(s);
        }
      });
    });
  }
}// end inner class