package com.common.anni.wifimanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * Time: 2023/7/12
 */
public class WifiUtils {

    /**
     * 判断当前WiFi是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiOpened(Context context) {
        WifiManager wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 判断当前网络连接状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // android.permission.ACCESS_NETWORK_STATE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    /**
     * 判断是否2.4Gwifi
     *
     * @param frequency
     * @return
     */
    public static boolean is24GHzWifi(int frequency) {
        if (frequency < 2400) return false;
        return frequency <= 2500;
    }

    /**
     * 判断是否5Gwifi
     *
     * @param frequency
     * @return
     */
    public static boolean is5GHzWifi(int frequency) {
        if (frequency <= 4900) return false;
        return frequency < 5900;
    }
}
