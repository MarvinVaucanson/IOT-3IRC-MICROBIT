package com.example.sensorcommand.helpers;

import com.example.sensorcommand.model.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorDataParser {

    // Convertit un tableau JSON brut en une map associant chaque appareil à sa liste de capteurs
    public static Map<String, List<Sensor>> parse( String raw )
    {
        Map<String, List<Sensor>> result = new HashMap<>();

        try {
            JSONArray data = new JSONArray( raw );

            for ( int index = 0; index < data.length(); index++ )
            {
                JSONObject obj = data.getJSONObject( index );

                // Extraire les champs de chaque entrée JSON
                String deviceId = obj.optString( "deviceId", "Inconnu" ).toUpperCase();
                String sensorType = obj.getString( "sensor" ).toUpperCase();
                String value = String.valueOf( Math.round( obj.getDouble( "value" ) ) );
                String unit = obj.getString( "unit" );
                String protocol = obj.optString( "protocol", "UDP" );

                Sensor sensor;

                // Associer chaque type de capteur à son initiale d'affichage
                switch ( sensorType ) {
                    case "HUMIDITE":
                    case "HUMI":
                        sensor = new Sensor( deviceId, "H", "Humidité", protocol, unit, value );
                        break;
                    case "LUMINOSITE":
                    case "LUX":
                        sensor = new Sensor( deviceId, "L", "Luminosité", protocol, unit, value );
                        break;
                    case "TEMPERATURE":
                    case "BOU":
                        sensor = new Sensor( deviceId, "T", "Température", protocol, unit, value );
                        break;
                    case "PRESSION":
                    case "ACC":
                        sensor = new Sensor( deviceId, "P", "Pression", protocol, unit, value );
                        break;
                    case "CO2":
                        sensor = new Sensor( deviceId, "C", "CO2", protocol, unit, value );
                        break;
                    default:
                        String initial = sensorType.length() > 0 ? sensorType.substring( 0, 1 ) : "?";
                        sensor = new Sensor( deviceId, initial, sensorType, protocol, unit, value );
                        break;
                }

                // Ajouter le capteur dans la liste de l'appareil correspondant
                List<Sensor> sensorsList = result.get( deviceId );

                if ( sensorsList == null ) {
                    sensorsList = new ArrayList<>();
                    result.put( deviceId, sensorsList );
                }

                sensorsList.add( sensor );
            }

        } catch ( JSONException e ) {
            throw new RuntimeException( e );
        }

        return result;
    }
}