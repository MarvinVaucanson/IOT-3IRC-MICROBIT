package com.example.duke.model;

public class Sensor {
    private int number;
    private String name;
    private String protocol;
    private String unit;
    private String value;

    public Sensor( int number, String name, String protocol, String unit, String value ) {
        this.number = number;
        this.name = name;
        this.protocol = protocol;
        this.unit = unit;
        this.value = value;
    }

    public int getNumber() {
        return number;
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
