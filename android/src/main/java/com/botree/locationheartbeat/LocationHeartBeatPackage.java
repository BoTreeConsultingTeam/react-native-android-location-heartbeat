package com.botree.locationheartbeat;
 
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Intent;
import android.content.Context;
import java.util.Arrays;
 
public class LocationHeartBeatPackage implements ReactPackage {
  private Context mContext;
  private LocationHeartBeatModule locationHeartBeatModule;

  public LocationHeartBeatPackage() {

  }

  public LocationHeartBeatPackage(Context activityContext) {
    mContext = activityContext;

  }
 
  @Override
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }
 
  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
 
  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    locationHeartBeatModule = new LocationHeartBeatModule(reactContext);
    return Arrays.<NativeModule>asList(locationHeartBeatModule);
    // return modules;
  } 
}