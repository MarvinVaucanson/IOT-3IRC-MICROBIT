package com.example.sensorcommand.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorcommand.R;
import com.example.sensorcommand.helpers.SensorAdapter;
import com.example.sensorcommand.model.Sensor;
import com.example.sensorcommand.viewmodel.SensorViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceSensorsFragment extends Fragment {

    private static final String DEVICE_ID = "deviceId";
    private final List<Sensor> sensors = new ArrayList<>();
    private String deviceId;
    private SensorAdapter adapter;

    public static DeviceSensorsFragment newInstance( String deviceId ) {
        DeviceSensorsFragment fragment = new DeviceSensorsFragment();

        Bundle args = new Bundle();
        args.putString( DEVICE_ID, deviceId );

        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            deviceId = getArguments().getString( DEVICE_ID );
        }
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_device_sensors, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        TextView titleView = view.findViewById( R.id.textViewDeviceTitle );
        TextView subtitleView = view.findViewById( R.id.textViewDeviceSubtitle );
        ImageView btnBack = view.findViewById( R.id.btnBack );

        titleView.setText( "MICROBIT / " + deviceId );
        subtitleView.setText( deviceId.toUpperCase() );

        btnBack.setOnClickListener( v -> requireActivity().getSupportFragmentManager().popBackStack() );

        RecyclerView recyclerView = view.findViewById( R.id.recyclerViewDeviceSensors );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );

        adapter = new SensorAdapter( sensors );
        recyclerView.setAdapter( adapter );

        SensorViewModel viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );
        viewModel.getDeviceMap().observe( getViewLifecycleOwner(), deviceMap -> {
            updateSensors( deviceMap );
        } );
    }

    private void updateSensors( Map<String, List<Sensor>> deviceMap ) {
        List<Sensor> updated = deviceMap.get( deviceId );
        if ( updated == null ) return;

        for ( Sensor incoming : updated ) {
            boolean found = false;
            for ( int i = 0; i < sensors.size(); i++ ) {
                if ( sensors.get( i ).getPriority() == incoming.getPriority() ) {
                    sensors.set( i, incoming );
                    adapter.notifyItemChanged( i );
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                sensors.add( incoming );
                adapter.notifyItemInserted( sensors.size() - 1 );
            }
        }
    }
}