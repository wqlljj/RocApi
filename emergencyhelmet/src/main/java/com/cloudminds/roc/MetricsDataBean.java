package com.cloudminds.roc;

/**
 * Created by cloud on 2019/1/8.
 */

public class MetricsDataBean {

    /**
     * capacity : 2917
     * residueCapacity : 481
     * percent : 30 %
     */

    private int capacity;
    private int residueCapacity;
    private String percent;
    String healthState;
    String batteryState;
    String other;
    String pluged;

    public String getPluged() {
        return pluged;
    }

    public void setPluged(String pluged) {
        this.pluged = pluged;
    }

    public MetricsDataBean() {
    }

    public MetricsDataBean(int capacity, int residueCapacity, String percent) {
        this.capacity = capacity;
        this.residueCapacity = residueCapacity;
        this.percent = percent;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getResidueCapacity() {
        return residueCapacity;
    }

    public void setResidueCapacity(int residueCapacity) {
        this.residueCapacity = residueCapacity;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getHealthState() {
        return healthState;
    }

    public void setHealthState(String healthState) {
        this.healthState = healthState;
    }

    public String getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(String batteryState) {
        this.batteryState = batteryState;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "MetricsDataBean{" +
                "capacity=" + capacity +
                ", residueCapacity=" + residueCapacity +
                ", percent='" + percent + '\'' +
                ", healthState='" + healthState + '\'' +
                ", batteryState='" + batteryState + '\'' +
                ", other='" + other + '\'' +
                ", pluged='" + pluged + '\'' +
                '}';
    }
}
