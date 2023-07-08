package com.common.anni.wifimanager.interfaces;

import android.content.Context;
import android.content.Intent;

import com.common.anni.wifimanager.model.WifiBean;

import java.util.List;

public interface WifiStateListener {
  boolean canScanWifi();

  void wifiPasswordError();

  void wifiListChange(List<WifiBean> wifiBeanList);

  void wifiNetWorkState(int wifiState);//wifi 网络连接状态

  void onReceive(Context context, Intent intent);
}
