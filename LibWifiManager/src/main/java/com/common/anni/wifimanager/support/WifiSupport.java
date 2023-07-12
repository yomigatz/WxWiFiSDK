package com.common.anni.wifimanager.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.common.anni.wifimanager.model.WifiBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ${GuoZhaoHui} on 2017/11/27.
 * Email:guozhaohui628@gmail.com
 */

public class WifiSupport {

    private static final String TAG = "WifiSupport";

    private static WifiConfiguration configs = null;

    public static WifiConfiguration getConfig() {
        return configs;
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiSupport() {
    }

    public static List<ScanResult> getWifiScanResult(Context context) {
        boolean b = context == null;
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getScanResults();
    }

    public static boolean isWifiEnable(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static WifiInfo getConnectedWifiInfo(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
    }

    public static List getConfigurations(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConfiguredNetworks();
    }


    public static WifiConfiguration createWifiConfig(String SSID, String password, WifiCipherType type) {

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
//            config.wepKeys[0] = "";  //注意这里
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
        }

        if (type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        if (type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        }

        return config;

    }

    /**
     * 接入某个wifi热点
     */
    public static boolean addNetWork(WifiConfiguration config, Context context) {
        configs = config;
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiinfo = wifimanager.getConnectionInfo();

        if (null != wifiinfo) {
            wifimanager.disableNetwork(wifiinfo.getNetworkId());
        }

        boolean result = false;

        if (config.networkId > 0) {
            result = wifimanager.enableNetwork(config.networkId, true);
            wifimanager.updateNetwork(config);
        } else {

            int i = wifimanager.addNetwork(config);
            result = false;

            if (i > 0) {

                wifimanager.saveConfiguration();
                return wifimanager.enableNetwork(i, true);
            }
        }

        return result;

    }

    /**
     * 判断wifi热点支持的加密方式
     */
    public static WifiCipherType getWifiCipher(String s) {
        if (s.isEmpty()) {
            return WifiCipherType.WIFICIPHER_INVALID;
        } else if (s.contains("WEP")) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else if (s.contains("WPA") || s.contains("WPA2") || s.contains("WPS")) {
            return WifiCipherType.WIFICIPHER_WPA;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    //查看以前是否也配置过这个网络
    public static WifiConfiguration isExsits(String SSID, Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifimanager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 打开WIFI
    public static void openWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public static void closeWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(false);
        }
    }

    public static boolean isOpenWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean b = wifimanager.isWifiEnabled();
        return b;
    }

    /**
     * 将idAddress转化成string类型的Id字符串
     *
     * @param idString
     * @return
     */
    public static String getStringId(int idString) {
        StringBuffer sb = new StringBuffer();
        int b = (idString >> 0) & 0xff;
        sb.append(b + ".");
        b = (idString >> 8) & 0xff;
        sb.append(b + ".");
        b = (idString >> 16) & 0xff;
        sb.append(b + ".");
        b = (idString >> 24) & 0xff;
        sb.append(b);
        return sb.toString();
    }

    /**
     * 设置安全性
     *
     * @param capabilities
     * @return
     */
    public static String getCapabilitiesString(String capabilities) {
        if (capabilities.contains("WEP")) {
            return "WEP";
        } else if (capabilities.contains("WPA") || capabilities.contains("WPA2") || capabilities.contains("WPS")) {
            return "WPA/WPA2";
        } else {
            return "OPEN";
        }
    }

    public static boolean getIsWifiEnabled(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifimanager.isWifiEnabled();
    }

    public static void getReplace(Context context, List<WifiBean> list) {
        WifiInfo wifi = WifiSupport.getConnectedWifiInfo(context);
        List<WifiBean> listCopy = new ArrayList<>();
        listCopy.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            if (("\"" + list.get(i).getWifiName() + "\"").equals(wifi.getSSID())) {
                listCopy.add(0, list.get(i));
                listCopy.remove(i + 1);
                listCopy.get(0).setState("已连接");
            }
        }
        list.clear();
        list.addAll(listCopy);
    }

    /**
     * 去除同名WIFI
     *
     * @param oldSr 需要去除同名的列表
     * @return 返回不包含同命的列表
     */
    public static List<ScanResult> noSameName(List<ScanResult> oldSr) {
        List<ScanResult> newSr = new ArrayList<ScanResult>();
        for (ScanResult result : oldSr) {
            if (!TextUtils.isEmpty(result.SSID) && !containName(newSr, result.SSID))
                newSr.add(result);
        }
        return newSr;
    }

    /**
     * 判断一个扫描结果中，是否包含了某个名称的WIFI
     *
     * @param sr   扫描结果
     * @param name 要查询的名称
     * @return 返回true表示包含了该名称的WIFI，返回false表示不包含
     */
    public static boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    /**
     * 返回level 等级
     */
    public static int getLevel(int signalStrength) {
        if (signalStrength >= -50) {
            return 4;
        } else if (signalStrength >= -60) {
            return 3;
        } else if (signalStrength >= -70) {
            return 2;
        } else if (signalStrength >= -80) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取当前连接wifi信息
     *
     * @param context
     * @return
     */
    public static WifiInfo getCurrentWifi(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        //  String wifiId = info != null ? info.getSSID() : null;
        return info;
    }

    /**
     * 是否包括盒子wifi
     *
     * @return
     */
    public static boolean currentIsBoxWifi(String curr_ssid, String ssid) {
        if (!curr_ssid.isEmpty() && curr_ssid.contains(ssid)) {
            return true;
        }
        return false;
    }

    /**
     * 是否包括盒子wifi
     *
     * @return
     */
    public static boolean containBoxWifi(List<WifiBean> list, String ssid) {
        for (WifiBean wifiBean : list) {
            if (!wifiBean.getWifiName().isEmpty() && wifiBean.getWifiName().contains(ssid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 浏览wifi列表
     *
     * @return
     */
    public static void scanWifi(Context context) {
        WifiManager systemService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!systemService.isWifiEnabled()) {
            systemService.setWifiEnabled(true);
        }
        systemService.startScan();
    }

    public static boolean isOpenGps(Context context) {
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static void clearConfig(Context context) {
        if (configs != null) {
            WifiManager systemService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (configs.networkId > 0) {
                systemService.disconnect();
                systemService.removeNetwork(configs.networkId);
                systemService.saveConfiguration();
            } else {
                systemService.disconnect();
                removeWifiBySsid(systemService, configs.SSID);
            }
        }
    }


    public static void removeWifiBySsid(WifiManager wifiManager, String targetSsid) {
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            if (ssid.equals(targetSsid)) {
                wifiManager.removeNetwork(wifiConfig.networkId);
                wifiManager.saveConfiguration();
            }
        }
    }

    public static void alertOpenGps(final int gpsCode, final Activity activity) {
        new AlertDialog.Builder(activity).setMessage("请打开手机GPS").setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(gpsIntent, gpsCode);
            }
        }).show();
    }

}
