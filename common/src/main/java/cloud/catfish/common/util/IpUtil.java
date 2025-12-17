package cloud.catfish.common.util;

import cn.hutool.core.net.NetUtil;

/**
 * IP工具类
 */
public class IpUtil {

    /**
     * 判断IP是否在指定网段内
     *
     * @param ip   目标IP地址 (e.g. "192.168.1.10")
     * @param cidr 网段 (e.g. "192.168.1.0/24")
     * @return true 如果IP在网段内
     */
    public static boolean isInRange(String ip, String cidr) {
        return NetUtil.isInRange(ip, cidr);
    }
}
