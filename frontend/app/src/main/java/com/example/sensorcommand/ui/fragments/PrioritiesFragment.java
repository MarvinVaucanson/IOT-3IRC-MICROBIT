package com.example.sensorcommand.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorcommand.R;
import com.example.sensorcommand.helpers.DeviceAdapter;
import com.example.sensorcommand.model.Sensor;
import com.example.sensorcommand.viewmodel.SensorViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrioritiesFragment extends Fragment {

    private DeviceAdapter adapter;
    private final List<String> deviceIds = new ArrayList<>();

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_priorities, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        RecyclerView recyclerView = view.findViewById( R.id.recyclerViewDevices );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );

        adapter = new DeviceAdapter( deviceIds, this::openDevice );
        recyclerView.setAdapter( adapter );

        SensorViewModel viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );
        viewModel.getDeviceMap().observe( getViewLifecycleOwner(), this::updateDeviceList );
    }

    @SuppressLint( "NotifyDataSetChanged" )
    private void updateDeviceList( Map<String, List<Sensor>> deviceMap ) {
        deviceIds.clear();
        deviceIds.addAll( deviceMap.keySet() );

        Collections.sort( deviceIds );

        adapter.notifyDataSetChanged();
    }

    private void openDevice( String deviceId ) {
        DeviceSensorsFragment fragment = DeviceSensorsFragment.newInstance( deviceId );
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.fragment_container, fragment )
                .addToBackStack( null )
                .commit();
    }
}