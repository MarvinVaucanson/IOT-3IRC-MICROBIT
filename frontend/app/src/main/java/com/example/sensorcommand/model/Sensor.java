package com.example.sensorcommand.model;

public class Sensor {
    private final String deviceId;
    private final String initial;
    private final String name;
    private final String protocol;
    private final String unit;
    private final String value;

    public Sensor( String deviceId, String initial, String name, String protocol, String unit, String value ) {
        this.deviceId = deviceId;
        this.initial = initial;
        this.name = name;
        this.protocol = protocol;
        this.unit = unit;
        this.value = value;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getInitial() {
        return initial;
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
