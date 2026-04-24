package com.example.sensorcommand.processes;

import android.content.Context;

import com.example.sensorcommand.R;
import com.example.sensorcommand.helpers.SensorDataParser;
import com.example.sensorcommand.model.Sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class TestDataLoader {

    // Charge le fichier JSON depuis res/raw/
    public static String loadRawJson(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.testdata);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Parse un tableau JSON → map deviceId → capteurs
    public static Map<String, List<Sensor>> parseAll( String json ) {
        return SensorDataParser.parse( json );
    }
}
