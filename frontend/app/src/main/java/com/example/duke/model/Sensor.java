package com.example.duke.model;

public class Sensor {
    private String deviceId;
    private int priority;
    private String name;
    private String protocol;
    private String unit;
    private String value;

    public Sensor( String deviceId, int priority, String name, String protocol, String unit, String value ) {
        this.deviceId = deviceId;
        this.priority = priority;
        this.name = name;
        this.protocol = protocol;
        this.unit = unit;
        this.value = value;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUnit() {
        return unit;
    }

    public String getValue() {
        return value;
    }
}
