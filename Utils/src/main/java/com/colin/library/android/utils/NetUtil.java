package com.colin.library.android.utils;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.colin.library.android.annotation.NetType;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2018-12-13 15:39
 * <p>
 * 描述： 网络工具类
 */
public final class NetUtil {
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkInfo getActiveNetworkInfo() {
        final ConnectivityManager manager = AppUtil.getConnectivityManager();
        return null == manager ? null : manager.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected() {
        final NetworkInfo networkInfo = getActiveNetworkInfo();
        return null != networkInfo && networkInfo.isConnected();
    }

    /**
     * 判断是否是wifi连接
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifi() {
        return isWifi(getActiveNetworkInfo());
    }

    public static boolean isWifi(@Nullable final NetworkInfo networkInfo) {
        return null != networkInfo && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet() {
        return isEthernet(AppUtil.getConnectivityManager());
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet(@NonNull final ConnectivityManager manager) {
        final NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return null != networkInfo && networkInfo.isConnected();
    }

    /**
     * 打开或关闭移动数据网
     *
     * @param enabled {@code true}: 打开
     *                {@code false}: 关闭
     */
    public static void setDataEnabled(final boolean enabled) {
        final TelephonyManager manager = AppUtil.getTelephonyManager();
        if (null == manager) return;
        try {
            final Method setMobileDataEnabledMethod = manager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setMobileDataEnabledMethod.invoke(manager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查当前网络是否可用
     *
     * @return
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isNetworkAvailable() {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        final ConnectivityManager manager = AppUtil.getConnectivityManager();
        if (null == manager) return false;
        final NetworkInfo[] networkInfo = manager.getAllNetworkInfo();
        if (networkInfo.length == 0) return false;
        for (int i = 0; i < networkInfo.length; i++) {
            // 判断当前网络状态是否为连接状态
            if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) return true;
        }
        return false;
    }

    @NetType
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getNetType() {
        return getNetType(AppUtil.getConnectivityManager());
    }

    @NetType
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getNetType(@NonNull final ConnectivityManager manager) {
        if (isEthernet(manager)) return NetType.NETWORK_ETHERNET;
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return getNetType(networkInfo);
    }

    @NetType
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getNetType(@Nullable final NetworkInfo networkInfo) {
        //获取失败
        if (null == networkInfo || !networkInfo.isAvailable()) return NetType.NETWORK_NONE;
        //WIFI
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) return NetType.NETWORK_WIFI;
        switch (networkInfo.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_GPRS://1
            case TelephonyManager.NETWORK_TYPE_EDGE://2
            case TelephonyManager.NETWORK_TYPE_CDMA://4
            case TelephonyManager.NETWORK_TYPE_1xRTT://7
            case TelephonyManager.NETWORK_TYPE_IDEN://11
            case TelephonyManager.NETWORK_TYPE_GSM://16
                return NetType.NETWORK_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS://3
            case TelephonyManager.NETWORK_TYPE_EVDO_0://5
            case TelephonyManager.NETWORK_TYPE_EVDO_A://6
            case TelephonyManager.NETWORK_TYPE_HSDPA://8
            case TelephonyManager.NETWORK_TYPE_HSUPA://9
            case TelephonyManager.NETWORK_TYPE_HSPA://10
            case TelephonyManager.NETWORK_TYPE_EVDO_B://12
            case TelephonyManager.NETWORK_TYPE_EHRPD://14
            case TelephonyManager.NETWORK_TYPE_HSPAP://15
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA://17
            case 19:
                return NetType.NETWORK_3G;
            case TelephonyManager.NETWORK_TYPE_LTE://13
            case TelephonyManager.NETWORK_TYPE_IWLAN://18
                return NetType.NETWORK_4G;
            case TelephonyManager.NETWORK_TYPE_NR://20
                return NetType.NETWORK_5G;
            default:
                return getNetType(networkInfo.getSubtypeName());
        }
    }

    @NetType
    private static int getNetType(@Nullable final String subType) {
        if ("TD-SCDMA".equalsIgnoreCase(subType) || "WCDMA".equalsIgnoreCase(subType)
                || "CDMA2000".equalsIgnoreCase(subType)
        ) return NetType.NETWORK_3G;
        return NetType.NETWORK_MOBILE;
    }

    @NonNull
    public static String getNetType(@NetType final int type) {
        switch (type) {
            case NetType.NETWORK_NONE:
                return "无网络";
            case NetType.NETWORK_WIFI:
                return "WIFI";
            case NetType.NETWORK_ETHERNET:
                return "以太网";
            case NetType.NETWORK_2G:
                return "2G";
            case NetType.NETWORK_3G:
                return "3G";
            case NetType.NETWORK_4G:
                return "4G";
            case NetType.NETWORK_5G:
                return "5G";
            case NetType.NETWORK_MOBILE:
            default:
                return "未知网络";
        }
    }

    /**
     * 获取网络运营商名称 英文
     * China Telecom
     *
     * @return 运营商名称
     */
    @Nullable
    public static String getNetworkOperatorName() {
        TelephonyManager manager = AppUtil.getTelephonyManager();
        if (null == manager) return null;
        if (manager.getSimState() == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkOperatorName();
        return null;
    }

    /**
     * 获取电话卡运营商名字 中文 中国移动、如中国联通、中国电信
     *
     * @return
     */
    @Nullable
    public static String getSimOperatorName() {
        TelephonyManager manager = AppUtil.getTelephonyManager();
        if (null == manager) return null;
        if (manager.getSimState() == TelephonyManager.SIM_STATE_READY)
            return manager.getSimOperatorName();
        return null;
    }


    /**
     * 获取IP地址
     * <p>
     * 需添加权限
     * {@code <uses-permission android:name="android.permission.INTERNET"/>}
     * </p>
     *
     * @param useIPv4 是否用IPv4
     * @return IP地址
     */
    @NonNull
    @RequiresPermission(INTERNET)
    public static String getIPAddress(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) if (isIPv4) return hostAddress;
                    else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0 ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) return broadcast.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

}
