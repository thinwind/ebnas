package com.alibaba.nacos.client.utils;

import java.util.Enumeration;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetUtil {

    private final static String LOCAL_IPV4;
    
    private final static String LOCAL_IPV6;

    public static String localIP() {
        return localIPv4();
    }
    
    static{
        LOCAL_IPV4 = getLocalIp(IpType.IPV4);
        LOCAL_IPV6 = getLocalIp(IpType.IPV6);
    }

    public static String localIPv4() {
         return LOCAL_IPV4;
    }

    public static String localIPv6() {
        return LOCAL_IPV6;
    }

    private static String getLocalIp(IpType ipType) {
        try {
            return getLocalHostAddress(ipType).getHostAddress();
        } catch (Exception e) {
            return "resolve_failed";
        }
    }

    public static InetAddress getLocalHostAddress(IpType ipType) throws UnknownHostException, SocketException {
        Enumeration allNetInterfaces;

        allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            //排除掉回环地址，比如127.0.0.1
            //排除掉虚拟网卡的地址，比如虚拟机网卡
            //排除掉禁用的网卡
            if (netInterface.isLoopback() || netInterface.isVirtual()
                    || !netInterface.isUp()) {
                continue;
            }
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                //排除掉本地链路地址和组播地址
                if (ip.isSiteLocalAddress() || ip.isMulticastAddress()) {
                    //如果地址是需要排除的，说明此网卡需要排除，直接break
                    //此网卡其它地址可以不用再尝试
                    break;
                }
                //使用ipv4
                if (ipType.right(ip)) {
                    return ip;
                }
            }
        }

        //如果走到了这里，说明以上的查找失败了
        //使用jdk默认的方法获取
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }
        return jdkSuppliedAddress;
    }

    public enum IpType {
        IPV4 {
            @Override
            boolean right(InetAddress address) {
                return address instanceof Inet4Address;
            }
        },
        IPV6 {
            @Override
            boolean right(InetAddress address) {
                return address instanceof Inet6Address;
            }
        };

        abstract boolean right(InetAddress address);
    }

    public static void main(String[] args) {
        System.out.println(localIP());
    }
}
