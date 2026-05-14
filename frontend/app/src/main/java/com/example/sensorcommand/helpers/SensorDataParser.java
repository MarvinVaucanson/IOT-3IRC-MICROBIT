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

                // Associer chaque type de capteur à sa priorité d'affichage
                switch ( sensorType ) {
                    case "HUMIDITE":
                        sensor = new Sensor( deviceId, 1, "Humidité", protocol, unit, value );
                        break;
                    case "LUMINOSITE":
                        sensor = new Sensor( deviceId, 4, "Luminosité", protocol, unit, value );
                        break;
                    case "TEMPERATURE":
                        sensor = new Sensor( deviceId, 5, "Température", protocol, unit, value );
                        break;
                    case "PRESSION":
                        sensor = new Sensor( deviceId, 6, "Pression", protocol, unit, value );
                        break;
                    default:
                        sensor = new Sensor( deviceId, 0, sensorType, protocol, unit, value );
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