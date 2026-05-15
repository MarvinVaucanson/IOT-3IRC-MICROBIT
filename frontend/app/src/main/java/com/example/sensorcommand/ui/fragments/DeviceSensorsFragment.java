package com.example.sensorcommand.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorcommand.R;
import com.example.sensorcommand.helpers.SensorAdapter;
import com.example.sensorcommand.model.Sensor;
import com.example.sensorcommand.viewmodel.SensorViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeviceSensorsFragment extends Fragment {

    private static final String DEVICE_ID = "deviceId";
    private final List<Sensor> sensors = new ArrayList<>();
    private String deviceId;
    private SensorAdapter adapter;

    private SensorViewModel viewModel;
    private TextView textViewCurrentOrder;
    private boolean initialLoadDone = false;

    // Créer une instance du fragment avec l'identifiant de l'appareil en argument
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
        // Récupérer l'identifiant de l'appareil passé en argument
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
        TextView textViewCurrentOrder = view.findViewById( R.id.textViewCurrentOrder );
        Button btnSendOrder = view.findViewById( R.id.btnSendOrder );

        // Afficher le nom de l'appareil dans le titre et le sous-titre
        titleView.setText( "MICROBIT / " + deviceId );
        subtitleView.setText( deviceId.toUpperCase() );

        // Retourner à l'écran précédent quand cliquer sur le bouton retour
        btnBack.setOnClickListener( v -> requireActivity().getSupportFragmentManager().popBackStack() );

        // Initialiser la liste des capteurs de l'appareil
        RecyclerView recyclerView = view.findViewById( R.id.recyclerViewDeviceSensors );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );

        adapter = new SensorAdapter( sensors );
        recyclerView.setAdapter( adapter );

        // Observer les données pour mettre à jour la liste quand de nouvelles données arrivent
        setupDragAndDrop( recyclerView );

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );
        viewModel.getDeviceMap().observe( getViewLifecycleOwner(), this::updateSensors );

        btnSendOrder.setOnClickListener( v -> {
            if ( sensors.isEmpty() ) {
                Toast.makeText( requireContext(), "Aucun capteur disponible", Toast.LENGTH_SHORT ).show();
                return;
            }
            String order = adapter.buildOrderString();
            viewModel.sendOrder( order );
            Toast.makeText( requireContext(), "Ordre envoyé : " + order, Toast.LENGTH_SHORT ).show();
        } );
    }

    private void setupDragAndDrop( RecyclerView recyclerView )
    {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0 ) {

            @Override
            public boolean onMove( @NonNull RecyclerView rv,
                                   @NonNull RecyclerView.ViewHolder from,
                                   @NonNull RecyclerView.ViewHolder to ) {
                adapter.moveItem( from.getAdapterPosition(), to.getAdapterPosition() );
                updateOrderLabel();
                return true;
            }

            @Override
            public void onSwiped( @NonNull RecyclerView.ViewHolder viewHolder, int direction ) {
                // Pas de swipe
            }
        };

        new ItemTouchHelper( callback ).attachToRecyclerView( recyclerView );
    }

    private void updateOrderLabel() {
        if ( sensors.isEmpty() ) return;
        String order = adapter.buildOrderString();
        textViewCurrentOrder.setText( "ORDRE ACTUEL : " + order );
    }

    // Mettre à jour la liste des capteurs en remplaçant par priorité ou en ajoutant les nouveaux
    private void updateSensors( Map<String, List<Sensor>> deviceMap )
    {
        List<Sensor> updated = deviceMap.get( deviceId );

        if ( updated == null || updated.isEmpty() ) return;

        if ( !initialLoadDone ) {
            // Tri initial par priorité pour un ordre cohérent au départ
            sensors.clear();
            List<Sensor> sorted = new ArrayList<>( updated );
            Collections.sort( sorted, (a, b ) -> Integer.compare( a.getPriority(), b.getPriority() ) );
            sensors.addAll( sorted );
            adapter.notifyDataSetChanged();
            initialLoadDone = true;
            updateOrderLabel();
            return;
        }

        // Mise à jour des valeurs sans changer l'ordre défini par l'utilisateur
        for ( Sensor incoming : updated )
        {
            // Chercher si le capteur existe déjà dans la liste (même priorité)
            for ( int i = 0; i < sensors.size(); i++ ) {
                if ( sensors.get( i ).getPriority() == incoming.getPriority() ) {
                    sensors.set( i, incoming );
                    adapter.notifyItemChanged( i );
                    break;
                }
            }
        }
    }
}