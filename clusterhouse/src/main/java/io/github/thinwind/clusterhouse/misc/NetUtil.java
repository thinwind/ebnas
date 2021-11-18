package io.github.thinwind.clusterhouse.misc;

import java.util.Enumeration;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * IP及网络相关工具
 *
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-18 18:57
 *
 */
public class NetUtil {
    /**
     * 防止被实例化
     */
    private NetUtil() {}

    private final static String LOCAL_IPv4;

    private final static String LOCAL_IPv6;

    static {
        LOCAL_IPv4 = getLocalIp(IpType.IPv4);
        LOCAL_IPv6 = getLocalIp(IpType.IPv6);
    }

    public static String localIP() {
        return LOCAL_IPv4;
    }

    public static String localIPv4() {
        return LOCAL_IPv4;
    }

    public static String localIPv6() {
        return LOCAL_IPv6;
    }

    private static String getLocalIp(IpType ipType) {
        try {
            String ip = System.getProperty("com.alibaba.nacos.client.naming.local.ip");
            if (StringUtils.isBlank(ip)) {
                ip = getLocalHostAddress(ipType).getHostAddress();
            }

            return ip;
        } catch (UnknownHostException | SocketException e) {
            return "resolve_failed";
        }
    }

    public static InetAddress getLocalHostAddress(IpType ipType)
            throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            // 排除掉回环地址，比如127.0.0.1
            // 排除掉虚拟网卡的地址，比如虚拟机网卡
            // 排除掉禁用的网卡
            if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                // 排除掉本地链路地址和组播地址
                if (ip.isSiteLocalAddress() || ip.isMulticastAddress()) {
                    // 如果地址是需要排除的，说明此网卡需要排除，直接break
                    // 此网卡其它地址可以不用再尝试
                    break;
                }
                // 使用ip
                if (ipType.right(ip)) {
                    return ip;
                }
            }
        }

        // 如果走到了这里，说明以上的查找失败了
        // 使用jdk默认的方法获取
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException(
                    "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }
        return jdkSuppliedAddress;
    }

    public static final String getClientIp(HttpServletRequest request) {
        // 优先使用client设置检测到的ip
        String ip = request.getHeader(Consts.CLIENT_IP_HEADER);
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // X-Forwarded-For：Squid 服务代理
        ip = request.getHeader("X-Forwarded-For");
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // Proxy-Client-IP：apache 服务代理
        ip = request.getHeader("Proxy-Client-IP");
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // X-Real-IP：nginx服务代理
        ip = request.getHeader("X-Real-IP");
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // WL-Proxy-Client-IP：weblogic 服务代理
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // HTTP_CLIENT_IP：有些代理服务器
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isNotBlank(ip)) {
            return first(ip);
        }

        // 还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private final static boolean isBlank(String ip) {
        return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }

    private final static boolean isNotBlank(String ip) {
        return StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    private final static String first(String ip) {
        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        int sepIdx = ip.indexOf(",");
        if (sepIdx > 0) {
            return ip.substring(0, sepIdx);
        } else {
            return ip;
        }
    }

    private static enum IpType {
        IPv4 {
            @Override
            boolean right(InetAddress address) {
                return address instanceof Inet4Address;
            }
        },
        IPv6 {
            @Override
            boolean right(InetAddress address) {
                return address instanceof Inet6Address;
            }
        };

        abstract boolean right(InetAddress address);
    }

}
