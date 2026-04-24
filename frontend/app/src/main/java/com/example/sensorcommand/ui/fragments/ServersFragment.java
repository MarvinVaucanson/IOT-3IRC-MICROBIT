package com.example.sensorcommand.ui.fragments;

import android.os.Bundle;
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

        Button connectButton = view.findViewById( R.id.btnConnect );

        SensorViewModel viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        connectButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container, new SystemFragment() )
                    .addToBackStack( null )
                    .commit();
            try {
                String ip = editServerAddress.getText().toString();
                int port = Integer.parseInt( editPort.getText().toString() );

                connectButton.setEnabled( false );

                viewModel.connectServer( port, ip );

            } catch ( NumberFormatException e ) {
                editPort.setError( "Port invalid !" );
                connectButton.setEnabled( true );
            }
        } );
    }
}
