package com.example.sensorcommand.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorcommand.R;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    public interface OnDeviceClickListener {
        void onClick( String deviceId );
    }

    private final List<String> deviceIds;
    private final OnDeviceClickListener listener;

    public DeviceAdapter( List<String> deviceIds, OnDeviceClickListener listener ) {
        this.deviceIds = deviceIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_device, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {
        String deviceId = deviceIds.get( position );
        holder.textViewDeviceId.setText( deviceId );
        holder.itemView.setOnClickListener( v -> listener.onClick( deviceId ) );
    }

    @Override
    public int getItemCount() {
        return deviceIds.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDeviceId;

        ViewHolder( View itemView ) {
            super( itemView );
            textViewDeviceId = itemView.findViewById( R.id.textViewDeviceId );
        }
    }
}