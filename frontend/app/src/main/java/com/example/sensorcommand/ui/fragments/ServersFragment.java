package com.example.sensorcommand.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.sensorcommand.R;
import com.example.sensorcommand.viewmodel.SensorViewModel;

public class ServersFragment extends Fragment {

    private final Handler handler = new Handler( Looper.getMainLooper() );
    private Runnable syncRunnable;
    private SensorViewModel viewModel;
    private String ip;
    private int port;
    private int secondsAgo = 0;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_servers, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        EditText editPort = view.findViewById( R.id.editPort );
        EditText editServerAddress = view.findViewById( R.id.editServerAddress );

        Button connectButton = view.findViewById( R.id.btnConnect );

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        // Observer l'état de connexion au serveur UDP
        viewModel.getIsConnected().observe( getViewLifecycleOwner(), connected ->
        {
            if ( connected ) {
                startSync();
                // Désactiver le bouton se connecter au seveur
                connectButton.setEnabled( false );
            } else {
                stopSync();
                // Activer le bouton se connecter au serveur
                connectButton.setEnabled( true );
            }
        } );

        // Initialiser l'action quand cliquer sur le bouton se connecter
        connectButton.setOnClickListener(v ->
        {
            try {
                // Récupérer l'adresse IP et le port des entrées d'utilisateur
                ip = editServerAddress.getText().toString();
                port = Integer.parseInt( editPort.getText().toString() );

                // Changer de l'écran quand cliquer sur connecter
                BottomNavigationView bottomNav = requireActivity().findViewById( R.id.bottom_nav );
                if ( bottomNav != null ) {
                    bottomNav.setSelectedItemId( R.id.nav_system );
                }

                // Enregistrer les entrées de l'utilisateur
                editServerAddress.setText( ip );
                editPort.setText( String.valueOf( port ) );

                // Connecter au serveur UDP
                viewModel.connectServer( port, ip );

            }
            // Gérer le cas où le numéro de port est vide ou invalid
            catch ( NumberFormatException e ) {
                editPort.setError( "Port invalid !" );
                connectButton.setEnabled( true );
            }
        } );
    }


    private void startSync()
    {
        // Arrêter la synchronisation avant
        stopSync();

        // Synchroniser les données tous les 10 secondes
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                secondsAgo++;
                viewModel.connectServer( port, ip );
                handler.postDelayed( this, 10000 );
            }
        };

        handler.postDelayed( syncRunnable, 10000 );
    }

    private void stopSync()
    {
        // Arrêter la synchronisation si c'est pas arrêté
        if ( syncRunnable != null ) {
            handler.removeCallbacks( syncRunnable );
            syncRunnable = null;
        }
    }
}
