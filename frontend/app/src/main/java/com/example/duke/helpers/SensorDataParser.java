package com.example.duke.helpers;

import com.example.duke.model.Sensor;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorDataParser {

    public static Sensor parse(String raw ) {

        try {
            JSONObject json = new JSONObject( raw );

            String sensor = json.getString( "sensor" ).toUpperCase();
            String value = String.valueOf( json.getDouble( "value" ) );
            String unit = json.getString( "unit" );
            String protocol = json.optString( "protocol", "UDP" );

            switch ( sensor ) {
                case "BOU":
                    return new Sensor( 1, "Boussole", protocol, unit, value );
                case "ACC":
                    return new Sensor( 3, "Accéléromètre", protocol, unit, value );
                case "LUX":
                    return new Sensor( 4, "Luminosité", protocol, unit, value );
                default:
                    return null;
            }
        } catch ( JSONException e ) {
            throw new RuntimeException( e );
        }

    }
}
