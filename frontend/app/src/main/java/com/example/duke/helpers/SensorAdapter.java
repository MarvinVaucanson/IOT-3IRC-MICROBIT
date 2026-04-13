package com.example.duke.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duke.R;
import com.example.duke.model.Sensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private List<Sensor> sensors;

    public SensorAdapter( List<Sensor> sensors ) {
        this.sensors = sensors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_sensor, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sensor sensor = sensors.get( position );
        holder.textViewNumber.setText( String.valueOf( sensor.getNumber() ) );
        holder.textViewName.setText( String.valueOf( sensor.getName() ) );
        holder.textViewProtocol.setText( String.valueOf( sensor.getProtocol() ) );
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewName, textViewProtocol;

        ViewHolder( View itemView ) {
            super( itemView );
            textViewNumber = itemView.findViewById( R.id.textViewNumber );
            textViewName = itemView.findViewById( R.id.textViewName );
            textViewProtocol = itemView.findViewById( R.id.textViewProtocol );
        }
    }
}
