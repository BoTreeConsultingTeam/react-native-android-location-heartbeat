package com.botree.locationheartbeat;
 
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Map;
import org.json.JSONObject;
import java.util.HashMap;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import com.facebook.react.bridge.*;
import android.content.Context;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.widget.Toast;

public class LocationHeartBeatModule extends ReactContextBaseJavaModule {
  
  private LocationHeartBeatReceiver receiver;

  public LocationHeartBeatModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }
 
  @Override
  public String getName() {
    return "LocationHeartBeatModule";
  }
 
  @ReactMethod
  public void startLocationHeartBeat(final int interval) {
    if(receiver == null) {
      // Toast.makeText(getReactApplicationContext(), "Start  inside", Toast.LENGTH_LONG).show();
      receiver = new LocationHeartBeatReceiver(getReactApplicationContext());
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction("START_MONITOR_LOCATION");
      intentFilter.addAction("MONITOR_LOCATION");
      intentFilter.addAction("STOP_MONITOR_LOCATION");
      this.getCurrentActivity().registerReceiver(receiver, intentFilter);  

      Intent startMonitorIntent = new Intent("START_MONITOR_LOCATION");
      startMonitorIntent.putExtra("INTERVAL", interval );
      getReactApplicationContext().sendBroadcast(startMonitorIntent);
    }
  }

  @ReactMethod
  public void stopLocationHeartBeat() {
    if(receiver != null) {
      Intent stopMonitorIntent = new Intent("STOP_MONITOR_LOCATION");
      getReactApplicationContext().sendBroadcast(stopMonitorIntent);
      receiver = null;
    }
  }

  class LocationHeartBeatReceiver extends BroadcastReceiver {

      private ReactApplicationContext reactApplicationContext;

      public LocationHeartBeatReceiver(ReactApplicationContext reactApplicationContext) {
        super();
        this.reactApplicationContext = reactApplicationContext;
      }

      // This method call when number of wifi connections changed
      public void onReceive(Context c, Intent intent) {
        // Toast.makeText(c, "OnReceive", Toast.LENGTH_LONG).show();
        if(intent.getAction().equals("MONITOR_LOCATION")) {
        // Toast.makeText(c, "MONITOR_LOCATION", Toast.LENGTH_LONG).show();

          try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("lat", String.valueOf(intent.getDoubleExtra("Latitude", 0)));
            map.put("longt", String.valueOf(intent.getDoubleExtra("Longitude", 0)));
            JSONObject obj = new JSONObject(map);
            sendEvent("locationManager", obj.toString());
            return;
          } catch (Exception e) {
            return;
          }
        } else if(intent.getAction().equals("START_MONITOR_LOCATION")){
        // Toast.makeText(c, "START_MONITOR_LOCATION", Toast.LENGTH_LONG).show();
          try {
            Intent recIntent = new Intent(c, LocationHeartBeatService.class);
            recIntent.putExtra("INTERVAL" , intent.getIntExtra("INTERVAL", 5));
            c.startService(recIntent);
            return;
          } catch (Exception e) {
            return;
          }
        } else if(intent.getAction().equals("STOP_MONITOR_LOCATION")){
        // Toast.makeText(c, "STOP_MONITOR_LOCATION", Toast.LENGTH_LONG).show();
          try {
            Intent recIntent = new Intent(c, LocationHeartBeatService.class);
            c.stopService(recIntent);
            return;
          } catch (Exception e) {
            return;
          }
        }
      }

      private void sendEvent(String eventName, String params) {
        this.reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
      }

  }
}