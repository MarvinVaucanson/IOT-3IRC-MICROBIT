package com.example.duke.model;

public class Sensor {
    private final String deviceId;
    private final int priority;
    private final String name;
    private final String protocol;
    private final String unit;
    private final String value;

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
