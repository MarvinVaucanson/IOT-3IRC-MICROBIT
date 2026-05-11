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

import com.example.sensorcommand.R;
import com.example.sensorcommand.viewmodel.SensorViewModel;

public class ServersFragment extends Fragment {

    private final Handler handler = new Handler( Looper.getMainLooper() );
    private Runnable connectRunnable;
    private SensorViewModel viewModel;
    private String ip;
    private int port;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_servers, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        EditText editPort = view.findViewById( R.id.editPort );
        EditText editServerAddress = view.findViewById( R.id.editServerAddress );

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        viewModel.getIsConnected().observe( getViewLifecycleOwner(), connected -> {
            if ( connected ) {
                startConnection();
            } else {
                stopConnection();
            }
        } );

        Button connectButton = view.findViewById( R.id.btnConnect );

        connectButton.setOnClickListener(v -> {
            try {
                ip = editServerAddress.getText().toString();
                port = Integer.parseInt( editPort.getText().toString() );

                connectButton.setEnabled( false );

                getParentFragmentManager().beginTransaction()
                        .replace( R.id.fragment_container, new SystemFragment() )
                        .addToBackStack( null )
                        .commit();

                viewModel.connectServer( port, ip );

            } catch ( NumberFormatException e ) {
                editPort.setError( "Port invalid !" );
                connectButton.setEnabled( true );
            }
        } );
    }


    private void startConnection()
    {
        stopConnection();

        connectRunnable = new Runnable() {
            @Override
            public void run() {
                viewModel.connectServer( port, ip );
                handler.postDelayed( this, 10000 );
            }
        };

        handler.postDelayed( connectRunnable, 10000 );
    }

    private void stopConnection() {
        if ( connectRunnable != null ) {
            handler.removeCallbacks( connectRunnable );
            connectRunnable = null;
        }
    }
}
