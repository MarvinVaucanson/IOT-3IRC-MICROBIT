package com.example.duke.processes;

import android.content.Context;

import com.example.duke.R;
import com.example.duke.helpers.SensorDataParser;
import com.example.duke.model.Sensor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    // Parse un tableau JSON → liste de Capteurs
    public static List<Sensor> parseAll(String json) {
        List<Sensor> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                Sensor sensor = SensorDataParser.parse(array.getJSONObject(i).toString());
                if ( sensor != null) result.add( sensor );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
