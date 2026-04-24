package com.example.sensorcommand.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorcommand.R;
import com.example.sensorcommand.model.Sensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private final List<Sensor> sensors;

    public SensorAdapter( List<Sensor> sensors ) {
        this.sensors = sensors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_sensor, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {
        Sensor sensor = sensors.get( position );
        holder.textViewPriority.setText( String.valueOf( sensor.getPriority() ) );
        holder.textViewName.setText( sensor.getName() );
        holder.textViewProtocol.setText( sensor.getProtocol() );
        holder.textViewValue.setText( sensor.getValue() + " " + sensor.getUnit() );
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPriority, textViewName, textViewProtocol, textViewValue;

        ViewHolder( View itemView ) {
            super( itemView );
            textViewPriority = itemView.findViewById( R.id.textViewPriority );
            textViewName = itemView.findViewById( R.id.textViewName );
            textViewProtocol = itemView.findViewById( R.id.textViewProtocol );
            textViewValue = itemView.findViewById( R.id.textViewValue );
        }
    }
}
