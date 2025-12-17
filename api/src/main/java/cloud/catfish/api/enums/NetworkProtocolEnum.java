package cloud.catfish.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetworkProtocolEnum {
    HTTP("HTTP", false),
    HTTPS("HTTPS", false),
    DNS("DNS", false),
    SSH("SSH", false),
    FTP("FTP", false),
    SMTP("SMTP", false),
    
    // 需要跳过的协议
    ARP("ARP", true),
    ICMP("ICMP", true),
    DHCP("DHCP", true),
    UNKNOWN("UNKNOWN", true);

    private final String protocol;
    private final boolean skip;

    public static NetworkProtocolEnum getByProtocol(String protocol) {
        for (NetworkProtocolEnum value : values()) {
            if (value.getProtocol().equalsIgnoreCase(protocol)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
