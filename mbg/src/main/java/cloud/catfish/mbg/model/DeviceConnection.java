package cloud.catfish.mbg.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class DeviceConnection implements Serializable {
    private String deviceId;

    private String neighbourDeviceId;

    private Integer communicationWay;

    private Integer sendWay;

    private String deviceBelongToUnit;

    private String neighbourDeviceBelongToUnit;

    private static final long serialVersionUID = 1L;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNeighbourDeviceId() {
        return neighbourDeviceId;
    }

    public void setNeighbourDeviceId(String neighbourDeviceId) {
        this.neighbourDeviceId = neighbourDeviceId;
    }

    public Integer getCommunicationWay() {
        return communicationWay;
    }

    public void setCommunicationWay(Integer communicationWay) {
        this.communicationWay = communicationWay;
    }

    public Integer getSendWay() {
        return sendWay;
    }

    public void setSendWay(Integer sendWay) {
        this.sendWay = sendWay;
    }

    public String getDeviceBelongToUnit() {
        return deviceBelongToUnit;
    }

    public void setDeviceBelongToUnit(String deviceBelongToUnit) {
        this.deviceBelongToUnit = deviceBelongToUnit;
    }

    public String getNeighbourDeviceBelongToUnit() {
        return neighbourDeviceBelongToUnit;
    }

    public void setNeighbourDeviceBelongToUnit(String neighbourDeviceBelongToUnit) {
        this.neighbourDeviceBelongToUnit = neighbourDeviceBelongToUnit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceId=").append(deviceId);
        sb.append(", neighbourDeviceId=").append(neighbourDeviceId);
        sb.append(", communicationWay=").append(communicationWay);
        sb.append(", sendWay=").append(sendWay);
        sb.append(", deviceBelongToUnit=").append(deviceBelongToUnit);
        sb.append(", neighbourDeviceBelongToUnit=").append(neighbourDeviceBelongToUnit);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}